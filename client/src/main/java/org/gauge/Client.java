package org.gauge;

import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 4/6/15.
 */

public class Client {

  static final Logger log = Logger.getLogger(Client.class);

  protected UserStatusDB userDb;
  protected ChatroomDB chatroomDb;
  protected int portTcp;
  protected int portUdp;
  protected String serverAddr;

  protected GaugeClientDaemonTCP tcpDaemon;
  protected PeerDaemon udpDaemon;
  protected User user;

  protected boolean isRunning;

  public Client(String serverAddr, int portTcp, int portUdp) {
    super();
    init(new UserStatusDB(), new ChatroomDB(), serverAddr, portTcp, portUdp);
  }


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


  public Client start() {
    // exit and return if already running
    if (isRunning) return this; // pass
    isRunning = true;

    tcpDaemon.start();
    udpDaemon.start();
    return this;
  }


  public Client stop() {
    if (!isRunning) return this; // pass
    isRunning = false;

    tcpDaemon.stop();
    udpDaemon.stop();

    return this;
  }


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


  public Client logout() {
    stop();
    return this;
  }


  public Client loadUserlist() {
    if (!isSafe()) {
      log.error("Failed to get user list.");
      return this; // pass if not safe
    }

    tcpDaemon.getUsers();
    return this;
  }


  public Client loadChatroomList() {
    if (!isSafe()) {
      log.error("Failed to get chatroom list.");
      return this;
    }

    tcpDaemon.getChatrooms();
    return this;
  }


  public UserStatusDB getUserList() {
    return userDb;
  }


  public ChatroomDB getChatroomList() {
    return chatroomDb;
  }


  public Client create(String topic, User user) {
    if (!isSafe()) {
      log.error("Failed to create chatroom.");
      return this;
    }

    Chatroom chatroom = udpDaemon.create(topic, user);
    tcpDaemon.createChatroom(chatroom);
    return this;
  }


  public Client create(String topic, User[] users) {
    if (!isSafe()) {
      log.error("Failed to create chatroom.");
      return this;
    }

    Chatroom chatroom = udpDaemon.create(topic, users);
    tcpDaemon.createChatroom(chatroom);
    return this;
  }


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


  public Client leave(String chatroomId) {
    if (!isSafe()) {
      log.error("Failed to leave chatroom.");
      return this;
    }

    udpDaemon.leave(chatroomId);
    tcpDaemon.leaveChatroom(user, chatroomId);
    return this;
  }


  public Client message(String chatroomId, String message) {
    if (!isSafe()) {
      log.error("Failed to send message to " + chatroomId + ".");
      return this;
    }

    udpDaemon.sendMessage(chatroomId, message);
    return this;
  }


  public LinkedBlockingQueue<Packet> getInbox(String chatroomId) {
    if (!isSafe()) {
      log.error("Failed to retrieve message queue from " + chatroomId + ".");
      return null;
    }
    return udpDaemon.inboxRoom(chatroomId);
  }


  public boolean isLoggedIn() {
    return tcpDaemon.isLoggedIn();
  }


  private boolean isSafe() {
    boolean result = isRunning && tcpDaemon.isLoggedIn();
    if (!result) {
      log.error("Not logged in! NOT SAFE!");
    }
    return result;
  }


  public ChatroomDB getActiveChatrooms() {
    return udpDaemon.getChatroomsActive();
  }


  public ChatroomDB getAllChatrooms() {
    return udpDaemon.getChatroomsAll();
  }
}




