package org.gauge;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 4/1/15.
 */
public class PeerDaemon {

  private final int PORT_INCOMING = 3030;

  static final Logger log = Logger.getLogger(PeerDaemon.class);
  private volatile boolean isRunning;
  protected int port;

  protected volatile User user;
  protected volatile DatagramSocket incoming, outgoing;
  protected volatile ConcurrentHashMap<String, Chatroom> chatroomsActive; // use ID as key
  protected volatile ConcurrentHashMap<String, Chatroom> chatroomsAll; // use ID as key

  protected class ChatroomPacket {
    public String destId;
    public Packet packet;
    public ChatroomPacket(String destId, Packet packet) {
      this.packet = packet;
      this.destId = destId;
    }
  }

  // this is needed between threads to not corrupt buffer
  protected volatile LinkedBlockingQueue<ChatroomPacket> sendQueue;
  // a Map of linked-list message queues, separated by rooms
  // the main key refers to the main message queue, intended for daemon.
  protected volatile ConcurrentHashMap<String, LinkedBlockingQueue<Packet>> recvQueue;

  public PeerDaemon(User user, int port) {
    init(port, user);
  }

  public PeerDaemon(User user) {
    int port = 3030;
    init(port, user);
  }

  private void init(int port, User user) {
    isRunning = false;
    this.port = port;
    this.user = user;
    sendQueue = new LinkedBlockingQueue<ChatroomPacket>();
    recvQueue = new ConcurrentHashMap<String, LinkedBlockingQueue<Packet>>();
    recvQueue.put("main", new LinkedBlockingQueue<Packet>());
    chatroomsAll = new ConcurrentHashMap<String, Chatroom>();
    chatroomsActive = new ConcurrentHashMap<String, Chatroom>();
  }


  /**
   *
   * Creates a chatroom with a single user
   *
   * @param user
   * @return
   */
  public PeerDaemon create(String title, User user) {
    User[] users = {user};
    create(title, users);
    return this;
  }


  /**
   *
   * Crates a chatroom with multiple users
   *
   * Sends a create packet
   *
   * @param users
   * @return
   */
  public PeerDaemon create(String title, User[] users) {
    Chatroom chatroom = new Chatroom(title, users);
    chatroomsActive.put(chatroom.getId(), chatroom);
    Packet createPacket = new Packet("CREATE", chatroom.toJSON().toString());
    enqueSend(chatroom.getId(), createPacket);
    return this;
  }


  /**
   *
   * Method to join a chatroom by ID.  The list should be loaded.
   *
   * @return
   */
  public PeerDaemon join(String chatroomId) {
    if (chatroomsAll.containsKey(chatroomId)) {
      // share the reference, to save resources.
      Chatroom curr = chatroomsAll.get(chatroomId);
      chatroomsActive.put(chatroomId, curr);
      Packet joinPacket = new Packet("JOIN", user.toJSON().toString());
      enqueSend(chatroomId, joinPacket);
    }
    return this;
  }


  /**
   *
   * Method to leave a chatroom by ID
   *
   * @param chatroomId
   * @return
   */
  public PeerDaemon leave(String chatroomId) {
    if (chatroomsActive.containsKey(chatroomId)) {
      Chatroom curr = chatroomsActive.get(chatroomId);
      Packet leavePacket = new Packet("LEAVE", user.toJSON().toString());
      enqueSend(chatroomId, leavePacket);
    }
    return this;
  }


  /**
   * This method should be used to send a packet.
   *
   * @param destId The chatroom Destination ID
   * @param packet The packet to send.
   */
  public void enqueSend(String destId, Packet packet) {
    sendQueue.offer(new ChatroomPacket(destId, packet));
  }


  /**
   * Internal function deques packets and broadcasts them to relevant chatroom.
   */
  private void executeSend() {
    while(sendQueue.size() > 0) {
      ChatroomPacket cp = sendQueue.remove();
      Packet packet = cp.packet;
      String destId = cp.destId;
      chatroomsActive.get(destId).broadcast(packet, outgoing, PORT_INCOMING);
    }
  }


  private void enqueRecv(Packet packet) {
    String header = packet.getHeader();
    // if message intended for daemon ops, put it here
    if (header.equals("JOIN") || header.equals("LEAVE") || header.equals("CREATE")) {
      recvQueue.get("main").offer(packet);
    }

    // if message intended for rooms, put it here
    String destId = packet.getDestId();
    if (recvQueue.containsKey(destId)) {
    } else {
      // create new chatroom key if nonexistent
      recvQueue.put(destId, new LinkedBlockingQueue<Packet>());
    }
    recvQueue.get(destId).offer(packet);
  }


  /**
   * Returns a linked list of messages for a given chatroom.
   *
   * @param chatroomId
   * @return
   */
  public LinkedBlockingQueue<Packet> inboxRoom(String chatroomId) {
    if (recvQueue.containsKey(chatroomId)) {
      return recvQueue.get(chatroomId);
    }
    return null;
  }


  /**
   *
   * Retrieve a linked list of messages for daemon only i.e. "main"
   *
   * @return
   */
  public LinkedBlockingQueue<Packet> inboxMain() {
    return recvQueue.get("main");
  }


  /**
   *
   * Starts the daemon.
   *
   * @return
   */
  public PeerDaemon start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }


    isRunning = true;
    Runnable incomingDaemon = new Runnable() {
      public void run() {
        try {
          incoming = new DatagramSocket(port);
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (isRunning) {
          try {
            // get packet and put into queue
            byte[] buffer = new byte[2048];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            incoming.receive(datagram);
            Packet p = new Packet(Arrays.copyOf(buffer, datagram.getLength()));
            enqueRecv(p); // put packet into correct mailbox

          } catch (IOException e) {
            e.printStackTrace();
          }

        }
      }
    };

    Runnable outgoingDaemon = new Runnable() {
      public void run() {
        try {
          outgoing = new DatagramSocket();
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (isRunning) {
          // deque send queue and broacast to relevant chatroomsActive
          executeSend();
        }
      }
    };

    new Thread(incomingDaemon).start();
    new Thread(outgoingDaemon).start();
    try {
      Thread.sleep(500);
      log.info("PeerDaemon started.");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return this;
  }

  private void poll() {

  }


  /**
   *
   * Stops the daemon.
   *
   * @return
   */
  public PeerDaemon stop() {
    isRunning = false;
    log.info("PeerDaemon stopped.");
    return this;
  }
}
