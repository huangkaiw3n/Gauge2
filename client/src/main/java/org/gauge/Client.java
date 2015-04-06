package org.gauge;

/**
 * Created by joel on 4/6/15.
 */

public class Client {

  protected UserStatusDB userDb;
  protected ChatroomDB chatroomDb;
  protected int portTcp;
  protected int portUdp;
  protected String serverAddr;

  protected GaugeClientDaemonTCP tcpDaemon;
  protected PeerDaemon udpDaemon;
  protected User user;
  protected boolean isLogin;

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
    if (isRunning) {
      return this; // pass
    }
    isRunning = true;

    // insert code here

    return this;
  }


  public Client stop() {
    if (!isRunning) return this; // pass
    isRunning = false;

    // insert code here

    return this;
  }


  public Client create() {
    return this;
  }

}




