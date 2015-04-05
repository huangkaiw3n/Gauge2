package org.gauge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kaiwen on 19/3/2015.
 * Username
 * IP
 */
public class UserStatusDB {

  private ConcurrentHashMap<String, User> userSet;

  public UserStatusDB() {
    userSet = new ConcurrentHashMap<>();
  }


  public UserStatusDB(JSONArray json) {
    for (int i = 0; i < json.length(); i++) {
      JSONObject jsonUser = json.getJSONObject(i);
      User u = new User(jsonUser);
    }
  }


  public synchronized void insert(String key, User user) {
    userSet.put(key, user);
  }


  public synchronized User delete(String key) {
    return userSet.remove(key);
  }


  public synchronized User delete(User ul) {
    String usernameQuery = ul.getUsername();
    for (String key : userSet.keySet()) {
      User u = userSet.get(key);
      if (u.getUsername() == usernameQuery) {
        return userSet.remove(key);
      }
    }
    return null;
  }


  public int size() {
    return userSet.size();
  }


  public synchronized JSONArray toJSONArray() {
    return toJSONArrayWithoutHash();
  }


  private synchronized JSONArray toJSONArrayWithoutHash() {
    JSONArray json = new JSONArray();

    for (String key : userSet.keySet()) {
      User u = userSet.get(key);
      json.put(u.toJSON());
    }
    return json;
  }
}
