package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

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
  protected volatile ConcurrentHashMap<String, Chatroom> chatroomsActive;
  protected volatile ConcurrentHashMap<String, Chatroom> chatroomsAll;

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
  // a Map of linked-list message queues
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
    chatroomsAll = new ConcurrentHashMap<String, Chatroom>();
    chatroomsActive = new ConcurrentHashMap<String, Chatroom>();
  }


  /**
   *
   * Injection function to set the chatrooms state.  useful for testing and mocking.
   *
   * @param list
   * @return
   */
  public PeerDaemon setChatroomsList(ConcurrentHashMap<String, Chatroom> list) {
    this.chatroomsAll = list;
    return this;
  }


  /**
   * Dependency injection to inject active chatrooms
   *
   * @param list
   * @return
   */
  public PeerDaemon setActiveChatroomsList(ConcurrentHashMap<String, Chatroom> list) {
    this.chatroomsActive = list;
    return this;
  }


  /**
   *
   * Method to join a chatroom.  The list should be loaded.
   *
   * @return
   */
  public PeerDaemon join(String chatroomId) {
    if (chatroomsAll.containsKey(chatroomId)) {
      // share the reference, to save resources.
      Chatroom curr = chatroomsAll.get(chatroomId);
      chatroomsActive.put(chatroomId, curr);

      curr.broadcast(new Packet("JOIN", user.toJSON().toString()), outgoing, PORT_INCOMING);
    }
    return this;
  }


  /**
   *
   * Method to leave a chatroom.
   *
   * @param chatroomId
   * @return
   */
  public PeerDaemon leave(String chatroomId) {
    if (chatroomsActive.containsKey(chatroomId)) {
      Chatroom curr = chatroomsActive.get(chatroomId);
      curr.broadcast(new Packet("LEAVE", user.toJSON().toString()), outgoing, PORT_INCOMING);
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
  public LinkedBlockingQueue<Packet> inbox(String chatroomId) {
    if (recvQueue.containsKey(chatroomId)) {
      return recvQueue.get(chatroomId);
    }
    return null;
  }


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


  public PeerDaemon stop() {
    isRunning = false;
    log.info("PeerDaemon stopped.");
    return this;
  }
}
