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
import java.util.AbstractQueue;
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
  protected volatile ChatroomDB chatroomsActive;

  protected volatile ChatroomDB chatroomsAll;
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
    chatroomsAll = new ChatroomDB();
    chatroomsActive = new ChatroomDB();
  }

  /**
   *
   * Overwrites the chatrooms all reference in instance to
   * use another reference.
   *
   * Useful for sharing Chatroom state information among objects.
   *
   * @param db
   * @return
   */
  public PeerDaemon setChatroomsAllDbRef(final ChatroomDB db) {
    this.chatroomsAll = db;
    return this;
  }


  /**
   * Creates a chatroom with a single user
   *
   * @param user The user to connect to.  NOTE: Each user must have ID, and IP address specified.
   * @return
   */
  public Chatroom create(String title, User user) {
    User[] users = {user};
    Chatroom c = create(title, users);
    return c;
  }


  public void setUser(User user) {
    this.user = user;
  }


  /**
   *
   * Concats 2 arrays together.
   *
   * @param arr1
   * @param arr2
   * @return
   */
  private User[] arrayConcat(User[] arr1, User[] arr2) {
    User[] result = new User[arr1.length + arr2.length];
    System.arraycopy(arr1,0,result,0,arr1.length);
    System.arraycopy(arr2,0,result,arr1.length,arr2.length);
    return result;
  }


  /**
   * Crates a chatroom with multiple chatrooms
   * <p/>
   * Sends a create packet
   *
   * @param users An array of chatrooms to connect to.  NOTE: Each user must have ID, and IP address specified.
   * @return
   */
  public Chatroom create(String title, User[] users) {
    User[] singleUser = {this.user};
    Chatroom chatroom = new Chatroom(title, ArrayUtils.addAll(singleUser, users));
    String chatroomId = chatroom.getId();
    log.debug(prettyUsername() + " Created chatroom: " + chatroom.toJSON());
    chatroomsActive.add(chatroom);
    chatroomsAll.add(chatroom); // TODO: broadcast to server that chatroom has been created.

    // create the mailbox
    recvQueue.put(chatroomId, new LinkedBlockingQueue<Packet>());

    Packet createPacket = new Packet("CREATE", chatroom.toJSON().toString());
    enqueSend(chatroom.getId(), createPacket);

    return chatroom;
  }


  /**
   * Method to join a chatroom by ID.  The list should be loaded.
   *
   * @return
   */
  public Chatroom join(final String chatroomId) {
    if (!chatroomsAll.has(chatroomId)) {
      log.error(prettyUsername() + " Oops!  Cannot join chatroom. " + chatroomId + " does not exist.");
      return null;
    }

    // share the reference, to save resources.
    JSONObject joinObj = new JSONObject();
    try {
      joinObj.put("user", user.toJSON());
      joinObj.put("chatroomId", chatroomId);
      Packet joinPacket = new Packet("JOIN", joinObj.toString());
      Chatroom chatroom = chatroomsAll.get(chatroomId);
      chatroom.add(this.user);
      chatroomsActive.add(chatroom);
      enqueSend(chatroomId, joinPacket, new Callback() {
        public void execute() {
          // create the mailbox
          recvQueue.put(chatroomId, new LinkedBlockingQueue<Packet>());
        }
      });
    } catch (JSONException e) {
      log.error("Oops.  Cannot create JSON payload for JOIN packet.");
    }
    return chatroomsAll.get(chatroomId);
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
    if (chatroomsActive.has(chatroomId)) {
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
            // Delete the mailbox
            recvQueue.remove(chatroomId);
          }
        });
      } catch (JSONException e) {
        log.error("Oops.  Cannot create JSON payload for LEAVE packet.");
      }
    }
    return this;
  }


  /**
   * Sends a message to the chatroom specified.  If chatroom is
   * not found, fails silently.
   *
   * @param chatroomId
   * @param message
   * @return
   */
  public PeerDaemon sendMessage(final String chatroomId, final String message) {
    AbstractQueue<Packet> currInbox;
    Chatroom curr;
    JSONObject sendJson;
    Packet packet;

    if (!chatroomsActive.has(chatroomId)) {
      log.error(prettyUsername() + " Oops!  Cannot send message to "
              + chatroomId + ". Not found!");
      return this;
    }

    curr = chatroomsActive.get(chatroomId);
    currInbox = inboxRoom(chatroomId);
    sendJson = new JSONObject();

    try {
      sendJson.put("user", user.toJSON());
      sendJson.put("body", message);
      packet = new Packet("MSG", sendJson.toString(), chatroomId);
      enqueSend(chatroomId, packet);
      log.info(prettyUsername() + " " + chatroomId + " Sent message: " + message);
    } catch (JSONException e) {
      log.error(prettyUsername() + " Oops!  Cannot create send message: " + message);
    }

    return this;
  }


  /**
   * This method should be used to send a packet.
   *
   * @param destId The chatroom Destination ID
   * @param packet The packet to send.
   */
  protected void enqueSend(String destId, Packet packet) {
    sendQueue.offer(new ChatroomPacket(destId, packet));
  }


  protected void enqueSend(String destId, Packet packet, Callback callback) {
    sendQueue.offer(new ChatroomPacket(destId, packet, callback));
  }

  /**
   * Internal function deques packets and broadcasts them to relevant chatroom.
   */
  protected void executeSend() {
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


  protected String prettyUsername() {
    return "[ " + user.getUsername() + " ]";
  }


  protected void enqueRecv(Packet packet) {
    // if message intended for rooms, put it here
    String destId = packet.getDestId();

    log.debug(packet.getDestId());

    if (destId == null || destId.equals("")) {
      recvQueue.get("main").offer(packet);

    } else if (recvQueue.containsKey(destId)) {
      recvQueue.get(destId).offer(packet);

    } else {
      // create new chatroom key and place packet in queue if nonexistent
      recvQueue.put(destId, new LinkedBlockingQueue<Packet>());
      recvQueue.get(destId).offer(packet);
    }
  }


  public PeerDaemon printInboxes() {
    StringBuffer sb = new StringBuffer();
    sb.append("--- " + prettyUsername() + " @ INBOX ---\n");
    for (String key : recvQueue.keySet()) {
      sb.append(printInboxStringBuffer(key));
    }
    sb.append("\n");
    log.info(sb.toString());
    return this;
  }


  /**
   * Pretty prints output in given inbox
   *
   * @param chatroomId
   * @return
   */
  public PeerDaemon printInbox(String chatroomId) {
    StringBuffer sb = printInboxStringBuffer(chatroomId);
    log.info(sb.toString());
    return this;
  }


  /**
   * Helper function to print inbox to String buffer (so display will not be interrupted).
   *
   * @param chatroomId
   * @return
   */
  private StringBuffer printInboxStringBuffer(String chatroomId) {
    AbstractQueue<Packet> inbox = inboxRoom(chatroomId);
    StringBuffer sb = new StringBuffer();
    if (inbox == null) {
      log.error("  " + prettyUsername() + " Oops!  Cannot retrieve inbox " + chatroomId);
    }
    sb.append("  " + prettyUsername() + " --- Inbox @ " + chatroomId + " ---\n");
    for (Packet p : inbox) {
      sb.append(p.toString() + "\n");
    }
    sb.append("  --- ---\n");
    return sb;
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
          // deque send queue and broacast to relevant chatroom active.
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

      try {
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

          } else if (header.equals("JOIN")) {
              JSONObject obj = new JSONObject(payload);
              User user = new User(obj.getJSONObject("user"));
              String chatroomId = obj.getString("chatroomId");
              // add user to chatroom list
              chatroomsActive.get(chatroomId).add(user);
              log.info(prettyUsername() + " Added" + user.getUsername() + " to " + chatroomId);
          }
      }catch (NullPointerException e){
          e.printStackTrace();
      }
  }


  /**
   * Internal garbage collection function to clean up chatrooms, if less than 2 chatrooms.
   *
   * @param chatrooms  the map of chatrooms to perform cleaning.
   * @param chatroomId The chatroomId of chatroom to check.
   */
  private void removeIfChatroomEmpty(ChatroomDB chatrooms, String chatroomId) {
    Chatroom chatroom;
    if (!chatrooms.has(chatroomId)) {
      return;
    }
    chatroom = chatrooms.get(chatroomId);
    if (chatroom.size() <= 1) { // less than 2 chatrooms, remove chatroom from list.
      chatrooms.delete(chatroomId);
    }
  }


  private void appendChatroom(Chatroom chatroom) {
    log.debug(prettyUsername() + " I GOT HERE");
    String id = chatroom.getId();
    if (!chatroomsActive.has(id)) {
      chatroomsActive.add(chatroom);
    }

    if (!chatroomsAll.has(id)) {
      chatroomsActive.add(chatroom);
    }
  }


  public PeerDaemon printChatroomActive() {
    StringBuilder sb = new StringBuilder();
    sb.append(prettyUsername() + " --- Active Chatrooms ---\n");
    sb.append(chatroomsActive.toString());
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


  public ChatroomDB getChatroomsActive() {
    return chatroomsActive;
  }

  public ChatroomDB getChatroomsAll() {
    return chatroomsAll;
  }

}
