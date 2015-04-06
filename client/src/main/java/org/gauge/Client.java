package org.gauge;

import org.apache.log4j.Logger;

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
    init(userDb, chatroomDb, serverAddr, portTcp, portUdp);
  }


  private void init(UserStatusDB userDb, ChatroomDB chatroomDB, String serverAddr, int portTcp, int portUdp) {
    this.userDb = userDb;
    this.chatroomDb = chatroomDB;
    this.serverAddr = serverAddr;
    this.portTcp = this.portTcp;
    this.portUdp = this.portUdp;
    this.user = new User("","","",this.serverAddr, this.portUdp);

    tcpDaemon = new GaugeClientDaemonTCP(serverAddr, portTcp);
    udpDaemon = new PeerDaemon(user, portUdp); // the user is not configured as yet.  But points to this reference.

    tcpDaemon.setChatoomsReference(chatroomDb);
    tcpDaemon.setUserlistReference(userDb);
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
    tcpDaemon.login(user);
    return this;
  }


  public Client getUserlist() {
    if (!isSafe()) {
      log.error("Failed to get user list.");
      return this; // pass if not safe
    }
    //TODO implement
    return this;
  }


  public Client create() {
    if (!isSafe()) {
      log.error("Failed to create chatroom.");
      return this;//
    }
    //TODO implement
    return this;
  }


  private boolean isSafe() {
    boolean result = isRunning && tcpDaemon.isLoggedIn();
    if (!result) {
      log.error("Not logged in! NOT SAFE!");
    }
    return result;
  }

}




