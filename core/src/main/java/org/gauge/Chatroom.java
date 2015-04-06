package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by joel on 4/1/15.
 */
public class Chatroom {

  static final Logger log = Logger.getLogger(Chatroom.class);

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String id;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  private String title;
  private List<User> users;


  public Chatroom(String title) {
    this.title = title;
    init();
  }


  public Chatroom(String title, User[] users) {
    this.title = title;
    init(null, users);
  }


  public Chatroom(JSONObject obj) {
    this.id = obj.has("id") ? obj.getString("id") : null;
    this.title = obj.has("title") ? obj.getString("title") : null;
    init(this.id, null);
    JSONArray jsonUsers;
    jsonUsers = obj.has("users") ? obj.getJSONArray("users") : null;
    int len = jsonUsers.length();
    if (jsonUsers != null) for (int i = 0; i < len; i++) {
      users.add(new User(jsonUsers.getJSONObject(i)));
    }
  }


  private void init() {
    init(null, null);
  }


  private void init(String id, User[] users) {
    if (users != null) {
      this.users = Collections.synchronizedList(new ArrayList<>(Arrays.asList(users)));
    } else {
      this.users = Collections.synchronizedList(new ArrayList<User>());
    }
    if (id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }


  public Chatroom add(User user) {
    log.debug("**************************");
    if (!exists(user)) {
      log.debug("======= ADDED USER");
      users.add(user);
    }
    log.debug("**************************");
    return this;
  }


  public synchronized User get(String username) {
    for (User curr : users) {
      String name = curr.getUsername();
      if (name.equals(username)) {
        return curr;
      }
    }
    return null;
  }


  public int size() {
    return users.size();
  }


  private synchronized boolean exists(User user) {
    for (User curr : users) {
      if (user.getUsername().equals(curr.getUsername())) {
        return true;
      }
    }
    return false;
  }


  public synchronized Chatroom remove(String username) {
    for (User user : users) {
      if (user.getUsername().equals(username)) {
        users.remove(user);
        break;
      }
    }
    return this;
  }


  public synchronized JSONObject toJSON() {
    JSONObject obj = new JSONObject();
    JSONArray usersJson = new JSONArray();
    for (User user : users) {
      usersJson.put(user.toJSON());
    }

    obj.put("id", id);
    obj.put("title", title);
    obj.put("users", usersJson);
    return obj;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ID=" + id + " ")
            .append("Title=" + title + " ")
            .append("Users=")
            .append(Arrays.toString(users.toArray()));

    return sb.toString();
  }


  public synchronized void broadcast(Packet packet, DatagramSocket sock) {
    broadcast(packet, sock, null);
  }


  /**
   * Function to broadcast to everyone except a given user.
   *
   * @param packet
   * @param sock
   * @param exceptUser
   */
  public synchronized void broadcast(Packet packet, DatagramSocket sock, User exceptUser) {
    byte[] data = packet.toBytes();
    for (User user : users)
      if (exceptUser != null && !user.equals(exceptUser)) {
        try {
          InetAddress address = Misc.getInetAddress(user.getIp());
          DatagramPacket datagram = new DatagramPacket(data, data.length, address, user.getPort());
          sock.send(datagram);
        } catch (UnknownHostException e) {
          // fail silently if no IP information available
        } catch (IOException e) {
          log.error("Oops.  Cannot send message to fellow client.  Is socket closed?");
        }
      }
  }

}
