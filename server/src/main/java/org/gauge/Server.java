package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joel on 3/14/15.
 */
public class Server {

  static final Logger log = Logger.getLogger(Server.class);

  private volatile boolean isRunning;
  private volatile ServerSocket socket;
  private int port;

  public UserDB db;
  public UserStatusHashDB statusDb;
  public ChatroomDB chatroomDB;

  public Server(int port) {
    this.port = port;
    init();
  }

  public Server() {
    this.port = 9000;
    init();
  }

  private void init() {
    isRunning = false;
    db = new UserDB();
    statusDb = new UserStatusHashDB();
    chatroomDB = new ChatroomDB();
  }


  public UserDB getDb() {
    return db;
  }


  private void pollConnection() {
    Socket s;
    try {
      s = socket.accept();
      log.info("New Connection from: " + socket.getInetAddress());

      Packet packet = getPacket(s);
      process(s, packet);

      // close the socket when done; other sockets can now connect
      s.close();

    } catch (IOException e) {
      //TODO if socket is closed, socket.accept() should not be sent.  Fail silently.
//      e.printStackTrace();
      return;
    }
  }


  /**
   * Used to poll the socket for the packet.
   * <p/>
   * This adds another layer of abstraction over packet class,
   * number of bytes is receiived and bytes retrieved.  It is then
   * generated into a Packet instance.
   * <p/>
   * In this way, encryption can be added without altering
   * structure of packet.
   *
   * @param s
   * @return
   * @throws IOException
   */
  private Packet getPacket(Socket s) throws IOException {
    Packet result;
    DataInputStream dis;
    int length = 0;
    byte[] buffer;

    dis = new DataInputStream(s.getInputStream());
    length = dis.readInt();
    buffer = new byte[length];
    dis.read(buffer, 0, length);
    result = new Packet(buffer);

    return result;
  }


  private void sendPacket(Socket s, Packet packet) throws IOException {
    DataOutputStream dos;
    byte[] buffer = packet.toBytes();
    int length = buffer.length;

    dos = new DataOutputStream(s.getOutputStream());
    dos.writeInt(length);
    dos.write(buffer);

    log.debug("Sent packet: " + packet.toString());
  }


  private void process(Socket s, Packet packet) throws IOException {
    log.info("Got Message: " + packet.toString());
    String header = packet.getHeader();
    if (header.equals("PING")) {
      sendPacket(s, new Packet("PING", "ACK"));

    } else if (header.equals("LOGIN")) {
      JSONObject resJson = makeAuthRes(packet);
      sendPacket(s, new Packet("LOGIN", resJson.toString()));

    } else if (header.equals("USERLIST")) {
      JSONArray resJson = makeUserlistRes(packet);
      sendPacket(s, new Packet("USERLIST", resJson.toString()));
    } else if (header.equals("CHATROOMS")) {
      JSONArray resJson = makeChatroomsRes(packet);
      sendPacket(s, new Packet("CHATROOMS", resJson.toString()));
    }
  }

  private JSONArray makeChatroomsRes(Packet packet) {
    String reqString = packet.getPayload();
    JSONArray jsonArr = new JSONArray();
    if (isLoggedIn(packet)) {
      jsonArr = chatroomDB.toJSON();
    }
    return jsonArr;
  }


  private JSONArray makeUserlistRes(Packet packet) {
    String reqString = packet.getPayload();
    JSONArray jsonArr = new JSONArray();
    if (isLoggedIn(packet)) {
      jsonArr = statusDb.toJSONArray();
    }
    return jsonArr;
  }


  private JSONObject makeAuthRes(Packet packet) {
    String reqString = packet.getPayload();
    boolean status = authenticate(reqString);

    JSONObject resJson = new JSONObject();
    try {
      if (status) {
        User u = new User(new JSONObject(reqString));
        String hash = generateHash(reqString);
        resJson.put("status", "success");
        resJson.put("hash", hash); // TODO: Replace with better hash function
        // insert new user into StatusDB with salt
        statusDb.insert(hash, User.cloneWithoutPassword(u));
      } else {
        resJson.put("status", "fail");
      }
    } catch (JSONException e) {
    }
    return resJson;
  }


  private String generateHash(String randomText) {
    String hash = null;
    try {
      hash = MessageDigest.getInstance("MD5").digest(randomText.getBytes()).toString();
    } catch (NoSuchAlgorithmException e) {
    }
    return hash;
  }


  /**
   * check if logged in via packet.  The packet
   * should have a JSON property "hash".
   * @param p
   * @return
   */
  public boolean isLoggedIn(Packet p) {
    String payload = p.getPayload();
    try {
      JSONObject json = new JSONObject(payload);
      String hash = (String) json.get("hash");
      if (statusDb.get(hash) != null) {
        return true;
      }
    } catch (JSONException e) {
      log.error("User not logged in.  Cannot process packet.");
    }
    return false;
  }


  private boolean authenticate(String reqString) {
    User userAuth = null;
    try {
      userAuth = new User(new JSONObject(reqString));
    } catch (JSONException e) {
      return false;
    }

    if (db.authenticate(userAuth.getUsername(), userAuth.getPassword())) {
      return true;
    }

    return false;
  }


  public Server start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    try {
      socket = new ServerSocket();
      socket.setReuseAddress(true);
      socket.bind(new InetSocketAddress(port));
      Thread.sleep(1000); // give server time to start
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.debug("Waiting for connection on " + port + ".");

    isRunning = true;

    Runnable daemon = new Runnable() {
      public void run() {
        while (isRunning) {
          pollConnection();
        }
        log.info("Server stopped.");
      }
    };

    new Thread(daemon).start();
    log.info("Server started.");
    return this;
  }


  public Server stop() {
    isRunning = false;
    try {
      socket.close();
    } catch (IOException e) {
      // fail silently; does not seem important
    }
    return this;
  }

}
