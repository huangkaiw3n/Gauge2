package org.gauge;

import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 4/6/15.
 */


/**
 * The Client class is the backend for all chat clients.
 * <p/>
 * This should be hooked up to a GUI for control.
 */
public class Client {

  static final Logger log = Logger.getLogger(Client.class);

  protected UserStatusDB userDb;
  protected ChatroomDB chatroomDb;
  protected int portTcp;
  protected int portUdp;
  protected String serverAddr;

  protected GaugeClientDaemonTCP tcpDaemon;
  public PeerDaemon udpDaemon;
  protected User user;

  protected boolean isRunning;

  /**
   * Creates a new chat Client instance.
   *
   * @param serverAddr The address of the tcp server
   * @param portTcp    The port of the tcp server
   * @param portUdp    The udp inbound port to bind
   */
  public Client(String serverAddr, int portTcp, int portUdp) {
    super();
    init(new UserStatusDB(), new ChatroomDB(), serverAddr, portTcp, portUdp);
  }


  /**
   * Private initialization function.
   *
   * @param userDb
   * @param chatroomDB
   * @param serverAddr
   * @param portTcp
   * @param portUdp
   */
  private void init(UserStatusDB userDb, ChatroomDB chatroomDB, String serverAddr, int portTcp, int portUdp) {
    this.userDb = userDb;
    this.chatroomDb = chatroomDB;
    this.serverAddr = serverAddr;
    this.portTcp = portTcp;
    this.portUdp = portUdp;
    this.user = new User("", "", "", this.serverAddr, this.portUdp);

    tcpDaemon = new GaugeClientDaemonTCP(serverAddr, portTcp);
    udpDaemon = new PeerDaemon(user, portUdp); // the user is not configured as yet.  But points to this reference.

    // set references
    tcpDaemon.setChatroomsReference(this.chatroomDb);
    tcpDaemon.setUserlistReference(this.userDb);
    udpDaemon.setChatroomsAllDbRef(this.chatroomDb);
  }


  /**
   * Starts the Client daemon.
   *
   * @return
   */
  public Client start() {
    // exit and return if already running
    if (isRunning) return this; // pass
    isRunning = true;

    tcpDaemon.start();
    udpDaemon.start();
    return this;
  }


  /**
   * Stops the Client daemon.
   *
   * @return
   */
  public Client stop() {
    if (!isRunning) return this; // pass
    isRunning = false;

    tcpDaemon.stop();
    udpDaemon.stop();

    return this;
  }


  /**
   * Login to client.  This function blocks program execution for 400 ms.
   * <p/>
   * Check that user is logged using isLoggedIn() before continuing.
   *
   * @param user
   * @return
   */
  public Client login(User user) {
    if (!isRunning) {
      log.error("Daemon is not running.  Cannot login.");
      return this; // pass if not running
    }
    this.user = user;
    udpDaemon.setUser(this.user);
    tcpDaemon.login(user);
    pause(400);
    return this;
  }


  /**
   * Wraps Thread.sleep() and allows execution to pause
   *
   * @param ms
   */
  private void pause(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  /**
   *
   * Deprecated function
   *
   * @return
   */
//  public Client logout() {
//    stop();
//    return this;
//  }


  /**
   * Loads the user list from server.  Remember to sleep() for a while!
   *
   * @return the instance
   */
  public Client loadUserlist() {
    if (!isSafe()) {
      log.error("Failed to get user list.");
      return this; // pass if not safe
    }

    tcpDaemon.getUsers();
    return this;
  }


  /**
   * Loads the chatroom list.  Remember to sleep() for a while!
   *
   * @return the instance
   */
  public Client loadChatroomList() {
    if (!isSafe()) {
      log.error("Failed to get chatroom list.");
      return this;
    }

    tcpDaemon.getChatrooms();
    return this;
  }


  /**
   * Returns a list of all online users.  Remember to sleep() for a while!
   *
   * @return
   */
  public UserStatusDB getUserList() {
    if (!isSafe()) {
      return null;
    }
    return userDb;
  }


  /**
   * Returns a list of all chatrooms.  Remember to sleep() for a while!
   *
   * @return
   */
  public ChatroomDB getChatroomList() {
    if (!isSafe()) {
      return null;
    }
    return chatroomDb;
  }


  /**
   * Creates a chatroom.  Remmeber to sleep() for a while!
   *
   * @param topic The topic of the chatroom
   * @param user  The user to chat with
   * @return the instance
   */
  public Client create(String topic, User user) {
    if (!isSafe()) {
      log.error("Failed to create chatroom.");
      return this;
    }

    Chatroom chatroom = udpDaemon.create(topic, user);
    tcpDaemon.createChatroom(chatroom);
    return this;
  }


  /**
   * Crerates a chatroom with an array of users.  Remember to
   * sleep() for a while!
   *
   * @param topic The topic of the chatroom
   * @param users an array of users to chat with.
   * @return The instance.
   */
  public Client create(String topic, User[] users) {
    if (!isSafe()) {
      log.error("Failed to create chatroom.");
      return this;
    }

    Chatroom chatroom = udpDaemon.create(topic, users);
    tcpDaemon.createChatroom(chatroom);
    return this;
  }


  /**
   * Join an existing chatroom with given chatroom ID.
   * Remember to sleep() for a while!
   *
   * @param chatroomId
   * @return the instance.
   */
  public Client join(String chatroomId) {
    if (!isSafe()) {
      log.error("Failed to join chatroom.");
      return this;
    }

    Chatroom chatroom = udpDaemon.join(chatroomId);
    log.debug(chatroom.toString());
    tcpDaemon.joinChatroom(chatroom);
    return this;
  }


  /**
   * Leave a current active chatroom.  Remember to sleep() for
   * a while!
   *
   * @param chatroomId
   * @return the instance.
   */
  public Client leave(String chatroomId) {
    if (!isSafe()) {
      log.error("Failed to leave chatroom.");
      return this;
    }

    udpDaemon.leave(chatroomId);
    tcpDaemon.leaveChatroom(user, chatroomId);
    return this;
  }


  /**
   * Sends a message in the chatroom.  Do note that messages
   * will be placed in the other clients' chatboxes.  The message
   * will not be in the current user's chatbox.
   *
   * @param chatroomId
   * @param message    A string message to send.
   * @return the instance.
   */
  public Client message(String chatroomId, String message) {
    if (!isSafe()) {
      log.error("Failed to send message to " + chatroomId + ".");
      return this;
    }

    udpDaemon.sendMessage(chatroomId, message);
    return this;
  }


  /**
   * Gets the inbox of the current chatroom ID.
   *
   * @param chatroomId
   * @return
   */
  public LinkedBlockingQueue<Packet> getInbox(String chatroomId) {
    if (!isSafe()) {
      log.error("Failed to retrieve message queue from " + chatroomId + ".");
      return null;
    }
    return udpDaemon.inboxRoom(chatroomId);
  }


  /**
   * Returns login status of the user.
   *
   * @return
   */
  public boolean isLoggedIn() {
    return tcpDaemon.isLoggedIn();
  }


  /**
   * Checks if it is safe to perform operations.
   * <p/>
   * The client must have started and logged in successfully.
   *
   * @return
   */
  private boolean isSafe() {
    boolean result = isRunning && tcpDaemon.isLoggedIn();
    if (!result) {
      log.error("Not logged in! NOT SAFE!");
    }
    return result;
  }


  /**
   * Gets a list of active chatrooms.
   *
   * @return
   */
  public ChatroomDB getActiveChatrooms() {
    if (isSafe()) {
      return udpDaemon.getChatroomsActive();
    }
    return null;
  }


  /**
   *
   * Returns list of all chatrooms
   *
   * @return
   */
  public ChatroomDB getAllChatrooms() {
    if (isSafe()) {
      return udpDaemon.getChatroomsAll();
    }
    return null;
  }
}



