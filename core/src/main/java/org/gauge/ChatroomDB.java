package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joel on 4/5/15.
 */
public class ChatroomDB {

  static final Logger log = Logger.getLogger(ChatroomDB.class);

  ConcurrentHashMap<String, Chatroom> chatrooms;

  public ChatroomDB() {
    this.chatrooms = new ConcurrentHashMap<>();
  }


  public ChatroomDB(ConcurrentHashMap<String, Chatroom> chatrooms) {
    this.chatrooms = chatrooms;
  }


  public ChatroomDB(JSONArray json) {
    this.chatrooms = new ConcurrentHashMap<>();
    for (int i = 0; i < json.length(); i++) {
      Chatroom chatroom = new Chatroom(json.getJSONObject(i));
      add(chatroom);
    }
  }


  public synchronized ChatroomDB add(Chatroom chatroom) {
    if (chatroom == null) return this; // pass if invalid
    this.chatrooms.put(chatroom.getId(), chatroom);
    return this;

  }


  public synchronized ChatroomDB delete(String chatroomId) {
    if (!has(chatroomId)) return this; // pass if user not found
    chatrooms.remove(chatroomId);
    return this;

  }


  public synchronized boolean has(String chatroomId) {
    return chatrooms.containsKey(chatroomId);
  }


  public synchronized Chatroom get(String chatroomId) {
    return chatrooms.get(chatroomId);
  }


  public synchronized JSONArray toJSON() {
    JSONArray json = new JSONArray();
    for (String key : chatrooms.keySet()) {
      json.put(chatrooms.get(key).toJSON());
    }
    return json;
  }


  public synchronized String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n--- CHATROOM LIST ---\n");
    for (String key : chatrooms.keySet()) {
      sb.append("  " + chatrooms.get(key) + "\n");
    }
    sb.append("--- ---");
    return sb.toString();
  }


  public synchronized ChatroomDB print() {
    log.info(toString());
    return this;
  }


  /**
   * Returns number of chatrooms in DB.
   *
   * @return
   */
  public int size() {
    return chatrooms.size();
  }


  /**
   * Shallow copy
   * <p/>
   * Does not delete any earlier copies in DB.
   *
   * @param db2
   * @return
   */
  public synchronized ChatroomDB copy(ChatroomDB db2) {
    for (String key : db2.chatrooms.keySet()) {
      Chatroom chatroom = db2.chatrooms.get(key);
      chatrooms.put(key, chatroom);
    }
    return this;
  }


  /**
   * flush the DB
   *
   * @return
   */
  public synchronized ChatroomDB clear() {
    chatrooms.clear();
    return this;
  }

}
