package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kaiwen on 19/3/2015.
 * Username
 * IP
 */
public class UserStatusHashDB {

  static final Logger log = Logger.getLogger(UserStatusHashDB.class);

  private ConcurrentHashMap<String, User> userSet;

  public UserStatusHashDB() {
    userSet = new ConcurrentHashMap<>();
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


  public synchronized User get(String hash) {
    return userSet.get(hash);
  }


  /**
   * Performs a shallow copy of another UserStatusDB instance.
   *
   * @param db2
   * @return
   */
  public UserStatusHashDB copy(UserStatusHashDB db2) {
    for (String key : db2.userSet.keySet()) {
      this.insert(key, db2.get(key));
    }
    return this;
  }


  /**
   *
   * Clears the DB of status records.
   *
   * @return
   */
  public UserStatusHashDB clear() {
    userSet = new ConcurrentHashMap<>();
    return this;
  }


  private synchronized JSONArray toJSONArrayWithoutHash() {
    JSONArray json = new JSONArray();

    for (String key : userSet.keySet()) {
      User u = userSet.get(key);
      json.put(u.toJSON());
    }
    return json;
  }


  /**
   * Prints the database as String representation.
   */
  @Override
  public synchronized String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("--- User Status DB dump ---\n");
    for (String key : userSet.keySet()) {
      User u = userSet.get(key);
      sb.append("[ " + key + " ] ");
      sb.append(u.toString() + "\n");
    }
    sb.append("--- ---\n");
    return sb.toString();
  }


  public synchronized void print() {
    log.info(this.toString());
  }

}
