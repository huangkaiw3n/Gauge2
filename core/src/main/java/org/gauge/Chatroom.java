package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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
  private ArrayList<User> users;


  public Chatroom(String title) {
    this.title = title;
    init();
  }


  public Chatroom(String title, User[] users) {
    this.title = title;
    init();
  }


  public Chatroom(JSONObject obj) {
    this.id = obj.has("id") ? obj.getString("id") : null;
    this.title = obj.has("title") ? obj.getString("title") : null;
    init(this.id);
    JSONArray jsonUsers;
    jsonUsers = obj.has("users") ? obj.getJSONArray("users") : null;
    int len = jsonUsers.length();
    if (jsonUsers != null) for (int i = 0; i < len; i++) {
      users.add(new User(jsonUsers.getJSONObject(i)));
    }
  }


  private void init() {
    init(null);
  }


  private void init(String id) {
    users = new ArrayList<User>();
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
  }


  public Chatroom add(User user) {
    if (!exists(user)) {
      users.add(user);
    }
    return this;
  }


  public User get(String username) {
    for (User curr : users) {
      String name  = curr.getUsername();
      if (name.equals(username)) {
        return curr;
      }
    }
    return null;
  }


  public int size() {
    return users.size();
  }


  private boolean exists(User user) {
    for (User curr : users) {
      if (user.getUsername().equals(curr.getUsername())) {
        return true;
      }
    }
    return false;
  }


  public Chatroom remove(String username) {
    for (User user : users) {
      if (user.getUsername().equals(username)) {
        users.remove(user);
        break;
      }
    }
    return this;
  }


  public JSONObject toJSON() {
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


  void broadcast(Packet packet, DatagramSocket sock, int port) {
    byte[] data = packet.toBytes();
    for (User user : users) {
      try {
        InetAddress address = user.getIp() == null ? InetAddress.getByName(user.getIp()) : null;
        DatagramPacket datagram = new DatagramPacket(data, data.length, address, port);
        sock.send(datagram);
      } catch (UnknownHostException e) {
        // fail silently if no IP information available
      } catch (IOException e) {
        log.error("Oops.  Cannot send message to fellow client.  Is socket closed?");
      }
    }
  }

}
