package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joel on 4/5/15.
 * <p/>
 * A UserStatusDB hashed by username.
 * <p/>
 * Note that this object can be constructed from the JSON dump of
 * UserStatusHashDB.  However, the reverse does not apply as
 * hash information is not known.
 * <p/>
 * This is solely used for the client.
 */
public class UserStatusDB {

  static final Logger log = Logger.getLogger(UserStatusDB.class);

  ConcurrentHashMap<String, User> users;

  public UserStatusDB() {
    this.users = new ConcurrentHashMap<>();
  }


  public UserStatusDB(ConcurrentHashMap<String, User> users) {
    this.users = users;
  }


  public UserStatusDB(JSONArray json) {
    this.users = new ConcurrentHashMap<>();
    for (int i = 0; i < json.length(); i++) {
      User u = new User(json.getJSONObject(i));
      add(u);
    }
  }


  public synchronized UserStatusDB add(User user) {
    if (user == null) return this; // pass if invalid
    this.users.put(user.getUsername(), user);
    return this;

  }


  public synchronized UserStatusDB delete(String username) {
    if (!has(username)) return this; // pass if user not found
    users.remove(username);
    return this;

  }


  public synchronized boolean has(String username) {
    return users.containsKey(username);
  }


  public synchronized JSONArray toJSON() {
    JSONArray json = new JSONArray();
    for (String key : users.keySet()) {
      json.put(users.get(key));
    }
    return json;
  }


  public synchronized String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n--- USERLIST ---\n");
    for (String key : users.keySet()) {
      sb.append("  " + users.get(key) + "\n");
    }
    sb.append("--- ---");
    return sb.toString();
  }


  public synchronized UserStatusDB print() {
    log.info(toString());
    return this;
  }


  /**
   * Returns number of chatrooms in DB.
   *
   * @return
   */
  public int size() {
    return users.size();
  }


  /**
   * Shallow copy
   * <p/>
   * Does not delete any earlier copies in DB.
   *
   * @param db2
   * @return
   */
  public synchronized UserStatusDB copy(UserStatusDB db2) {
    for (String key : db2.users.keySet()) {
      User u = db2.users.get(key);
      users.put(key, u);
    }
    return this;
  }


  /**
   * flush the DB
   *
   * @return
   */
  public synchronized UserStatusDB clear() {
    users.clear();
    return this;
  }
}

