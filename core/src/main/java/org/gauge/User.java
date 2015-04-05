package org.gauge;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Kaiwen on 19/3/2015.
 */
public class User {
  private String username;
  private String password;
  private String email;
  private long lastSeen;  // unix timestamp

  private String ip;

  private int port; // incoming port

  public User() {
  }

  public User(JSONObject json) {
    super();
    if (json.has("username")) {
      this.username = (String) json.get("username");
    }

    if (json.has("password")) {
      this.password = (String) json.get("password");
    }

    if (json.has("email")) {
      this.email = (String) json.get("email");
    }

    if (json.has("ip")) {
      this.ip = (String) json.get("ip");
    }

    if (json.has("port")) {
      this.port = (int) json.get("port");
    }

    if (json.has("lastSeen")) {
      this.lastSeen = json.getLong("lastSeen");
    } else {
      updateTimestamp(); // last resort if no timestamp found
    }
  }


  public User(String username) {
    this.username = username;
  }


  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  public User(String username, String password, String email, String ip) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.ip = ip;
  }

  public User(String username, String password, String email, String ip, int port) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.ip = ip;
    this.port = port;
  }


  /**
   *
   * This MUST always be called to ensure timetamp is concurrent,
   * and used when merging DBs together.
   *
   * When called, updates the timestamp.
   *
   * Timestamp is always updated upon user instantiation.
   *
   */
  public void updateTimestamp() {
    this.lastSeen = new Date().getTime();
  }


  /**
   *
   * Checks if user is newer than the second user.
   *
   * @param u2
   * @return
   */
  public boolean isNewerThan(User u2) {
    return this.lastSeen >= u2.lastSeen;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public JSONObject toJSON() {//username email
    JSONObject obj = new JSONObject();
    obj.put("username", username);
    obj.put("email", email);
    obj.put("ip", ip);
    obj.put("port", port);
    obj.put("lastSeen", lastSeen);
    return obj;
  }

  public JSONObject toJSONWithPassword() {
    JSONObject obj = this.toJSON();
    obj.put("password", password);
    return obj;
  }


  /**
   * Creates shallow copy of user
   *
   * @param u2
   * @return
   */
  public static User clone(User u2) {
    User u = new User();
    u.username = u2.username;
    u.password = u2.password;
    u.username = u2.username;
    u.lastSeen = u2.lastSeen;
    u.ip = u2.ip;
    u.port = u2.port;
    return u;
  }


  /**
   *
   * Shallow copy of clone without password
   *
   * @param u2
   * @return
   */
  public static User cloneWithoutPassword(User u2) {
    User u = User.clone(u2);
    u.password = null;
    return u;
  }


  /**
   *
   * Equality comparison function, by username
   *
   * @param user
   * @return
   */
  public boolean equals(User user) {
    return this.username.equals(user.getUsername());
  }

  @Override
  public String toString() {
    return this.toJSON().toString();
  }
}
