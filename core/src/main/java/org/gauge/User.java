package org.gauge;

import org.json.JSONObject;

/**
 * Created by Kaiwen on 19/3/2015.
 */
public class User {
  private String username;
  private String password;
  private String email;

  private String ip;

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

  public JSONObject toJSON() {//username email
    JSONObject obj = new JSONObject();
    obj.put("username", username);
    obj.put("email", email);
    obj.put("ip", ip);
    return obj;
  }

  public JSONObject toJSONWithPassword() {
    JSONObject obj = this.toJSON();
    obj.put("password", password);
    return obj;
  }

  @Override
  public String toString() {
    return this.toJSON().toString();
  }
}
