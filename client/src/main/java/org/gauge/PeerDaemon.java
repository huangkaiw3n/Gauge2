package org.gauge;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Map;
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


  /**
   * Interface for a callback function.
   */
  protected interface Callback {
    public void execute();
  }

  protected class ChatroomPacket {
    public String destId;
    public Packet packet;
    public Callback callback;

    public ChatroomPacket(String destId, Packet packet) {
      this.packet = packet;
      this.destId = destId;
      this.callback = null;
    }

    public ChatroomPacket(String destId, Packet packet, Callback callback) {
      this.packet = packet;
      this.destId = destId;
      this.callback = callback;
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
   * Creates a chatroom with a single user
   *
   * @param user The user to connect to.  NOTE: Each user must have ID, and IP address specified.
   * @return
   */
  public PeerDaemon create(String title, User user) {
    User[] users = {user};
    create(title, users);
    return this;
  }


  /**
   * Crates a chatroom with multiple users
   * <p/>
   * Sends a create packet
   *
   * @param users An array of users to connect to.  NOTE: Each user must have ID, and IP address specified.
   * @return
   */
  public PeerDaemon create(String title, User[] users) {
    User[] singleUser = {this.user};
    Chatroom chatroom = new Chatroom(title, ArrayUtils.addAll(singleUser, users));
    log.debug(prettyUsername() + " Created chatroom: " + chatroom.toJSON());
    chatroomsActive.put(chatroom.getId(), chatroom);
    Packet createPacket = new Packet("CREATE", chatroom.toJSON().toString());
    enqueSend(chatroom.getId(), createPacket);
    return this;
  }


  /**
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
   * Method to leave a chatroom by ID
   * <p/>
   * Has the properties "user" and "chatroomId".
   *
   * @param chatroomId String of chatroom ID to leave.
   * @return
   */
  public PeerDaemon leave(final String chatroomId) {
    if (chatroomsActive.containsKey(chatroomId)) {
      Chatroom curr = chatroomsActive.get(chatroomId);
      JSONObject leaveObj = new JSONObject();
      try {
        leaveObj.put("user", user.toJSON());
        leaveObj.put("chatroomId", chatroomId);
        Packet leavePacket = new Packet("LEAVE", leaveObj.toString());
        enqueSend(chatroomId, leavePacket, new Callback() {
          public void execute() {
            // delete the current user from the chatroom
            chatroomsActive.get(chatroomId).remove(user.getUsername());
            // perform cleanup routines
            removeIfChatroomEmpty(chatroomsActive, chatroomId);

          }
        });
      } catch (JSONException e) {
        log.error("Oops.  Cannot create JSON payload for LEAVE packet.");
      }
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


  public void enqueSend(String destId, Packet packet, Callback callback) {
    sendQueue.offer(new ChatroomPacket(destId, packet, callback));
  }

  /**
   * Internal function deques packets and broadcasts them to relevant chatroom.
   */
  private void executeSend() {
    while (sendQueue.size() > 0) {
      ChatroomPacket cp = sendQueue.remove();
      Packet packet = cp.packet;
      String destId = cp.destId;
      log.debug(prettyUsername() + " Packet send=" + packet.toString());
      chatroomsActive.get(destId).broadcast(packet, outgoing, user);
      // execute callback if any
      if (cp.callback != null) {
        cp.callback.execute();
      }
    }
  }


  private String prettyUsername() {
    return "[ " + user.getUsername() + " ]";
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
   * Retrieve a linked list of messages for daemon only i.e. "main"
   *
   * @return
   */
  public LinkedBlockingQueue<Packet> inboxMain() {
    return recvQueue.get("main");
  }


  /**
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
          incoming = new DatagramSocket(null);
          incoming.setReuseAddress(true);
          incoming.bind(new InetSocketAddress(port));
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
            log.debug(prettyUsername() + " Packet received=" + p.toString());
            enqueRecv(p); // put packet into correct mailbox

          } catch (IOException e) {
            e.printStackTrace();
          }

        }
        if (incoming != null) incoming.close();
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
        if (outgoing != null) outgoing.close();
      }
    };


    Runnable processRequests = new Runnable() {
      public void run() {
        while (isRunning) {
          poll();
        }
      }
    };

    new Thread(incomingDaemon).start();
    log.debug(prettyUsername() + " Starting incoming thread.");
    new Thread(outgoingDaemon).start();
    log.debug(prettyUsername() + " Starting outgoing thread.");
    new Thread(processRequests).start();
    log.debug(prettyUsername() + " Starting processRequests thread.");
    try {
      Thread.sleep(500);
      log.info("PeerDaemon started.");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return this;
  }


  private void poll() {
    while (!inboxMain().isEmpty()) {
      Packet packet = inboxMain().poll();
      try {
        processPacket(packet);
      } catch (JSONException e) {
        log.error("Error processing packet?  Is it formatted correctly?");
      }

    }
  }


  private void processPacket(Packet packet) throws JSONException {
    String header = packet.getHeader();
    String payload = packet.getPayload();

    if (header.equals("CREATE")) {
      JSONObject obj = new JSONObject(payload);
      Chatroom chatroom = new Chatroom(obj);
      appendChatroom(chatroom);
    } else if (header.equals("LEAVE")) {
      JSONObject obj = new JSONObject(payload);
      User user = new User(obj.getJSONObject("user"));
      String chatroomId = obj.getString("chatroomId");
      // remove from chatroomsActive only.  Syncing of all rooms should be done with
      // server.
      chatroomsActive.get(chatroomId).remove(user.getUsername());
      removeIfChatroomEmpty(chatroomsActive, chatroomId);
      log.info(prettyUsername() + " Removed " + user.getUsername() + " from " + chatroomId);


    }
  }


  /**
   * Internal garbage collection function to clean up chatrooms, if less than 2 users.
   *
   * @param chatrooms  the map of chatrooms to perform cleaning.
   * @param chatroomId The chatroomId of chatroom to check.
   */
  private void removeIfChatroomEmpty(Map<String, Chatroom> chatrooms, String chatroomId) {
    Chatroom chatroom;
    if (!chatrooms.containsKey(chatroomId)) {
      return;
    }
    chatroom = chatrooms.get(chatroomId);
    if (chatroom.size() <= 1) { // less than 2 users, remove chatroom from list.
      chatrooms.remove(chatroomId);
    }
  }


  private void appendChatroom(Chatroom chatroom) {
    log.debug(prettyUsername() + " I GOT HERE");
    String id = chatroom.getId();
    if (!chatroomsActive.containsKey(id)) {
      chatroomsActive.put(id, chatroom);
    }

    if (!chatroomsAll.containsKey(id)) {
      chatroomsActive.put(id, chatroom);
    }
  }


  public PeerDaemon printChatroomActive() {
    StringBuilder sb = new StringBuilder();
    sb.append(prettyUsername() + " --- Active Chatrooms ---\n");
    for (String key : chatroomsActive.keySet()) {
      Chatroom chatroom = chatroomsActive.get(key);
      sb.append(chatroom.toString() + "\n");
    }
    sb.append("--- ---\n");
    log.info(sb.toString());
    return this;
  }


  /**
   * Stops the daemon.
   *
   * @return
   */
  public PeerDaemon stop() {
    isRunning = false;
    log.info("PeerDaemon stopped.");
    return this;
  }


  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
