package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joel on 4/1/15.
 */
public class GaugeClientDaemonTCP extends SimpleClientDaemonTCP {
  static final Logger log = Logger.getLogger(GaugeClientDaemonTCP.class);

  private volatile String hash; // the security hash to be used always when sending over information
  private volatile User user;
  protected volatile UserStatusDB usersDBRef;  // a reference to the active chatrooms DB
  protected volatile ChatroomDB chatroomDbRef; // reference to the active chatroom DB

  private enum OperationState {
    NOP, LOGIN, LIST, PING;
  }

  private OperationState state;


  public GaugeClientDaemonTCP(String hostname, int port) {
    super(hostname, port);
    init();
  }


  public GaugeClientDaemonTCP() {
    super();
    init();
  }


  private void init() {
    this.usersDBRef = null;
    this.chatroomDbRef = null;
    this.user = null;
    this.hash = null;
    this.state = OperationState.NOP;
  }


  /**
   * Sets the target DB to update
   *
   * @param db
   * @return
   */
  public synchronized GaugeClientDaemonTCP setUserlistReference(final UserStatusDB db) {
    this.usersDBRef = db;
    return this;
  }


  /**
   *
   * Sets the target chatroom DB to update
   *
   * @param db
   * @return
   */
  public synchronized GaugeClientDaemonTCP setChatroomsReference(final ChatroomDB db) {
    this.chatroomDbRef = db;
    return this;
  }


  public OperationState getState() {
    return state;
  }


  public GaugeClientDaemonTCP login(final User u) {
    Exchange exchange = new Exchange() {
      public Packet request() {
        if (u.getUsername() == null || u.getPassword() == null) {
          return null;
        }
        return new Packet("LOGIN", u.toJSONWithPassword().toString());
      }

      public void response(Packet p) {
        try {
          JSONObject json = new JSONObject(p.getPayload());
          hash = (String) json.get("hash");
          if (hash != null) {
            user = u;
          }
        } catch (JSONException e) {
          log.error("Oops.  Invalid user, no user exists / wrong password?");
        }
      }
    };
    queueExchange(exchange);
    return this;
  }


  /**
   * Returns if the client has been authenticated.
   *
   * @return
   */
  public boolean isAuthenticated() {
    return (hash != null) && (user != null);
  }


  private JSONObject createJSONWihHash() throws JSONException {
    JSONObject jsonReq = new JSONObject();
    jsonReq.put("hash", hash);
    jsonReq.put("user", user.toJSON());
    return jsonReq;
  }


  public GaugeClientDaemonTCP getUsers() {
    if (!isAuthenticated()) return this; // exit if not authenticated

    Exchange exchange = new Exchange() {
      public Packet request() {
        JSONObject jsonReq = new JSONObject();
        try {
          jsonReq = createJSONWihHash();
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return new Packet("USERLIST", jsonReq.toString());
      }

      public void response(Packet p) {
        try {
          JSONArray jsonRes = new JSONArray(p.getPayload());
          UserStatusDB db2 = new UserStatusDB(jsonRes);
          if (usersDBRef == null) {
            throw new NullPointerException();
          }
          usersDBRef.clear();
          usersDBRef.copy(db2);
          usersDBRef.print();

        } catch (JSONException e) {
          e.printStackTrace();
        } catch (NullPointerException e) {
          log.error("DB not instantiated.");
        }
      }
    };
    queueExchange(exchange);
    return this;
  }


  public GaugeClientDaemonTCP getChatrooms() {
    Exchange exchange = new Exchange() {
      public Packet request() {
        JSONObject json = null;
        try {
          json = createJSONWihHash();
        } catch (JSONException e) {
        }
        return new Packet("CHATROOMS", json.toString());
      }

      public void response(Packet p) {
        try {
          JSONArray jsonArr = new JSONArray(p.getPayload());
          if (jsonArr == null) return; // pass if invalid / cannot authenticate
          ChatroomDB db2 = new ChatroomDB(jsonArr);
          chatroomDbRef.clear();
          chatroomDbRef.copy(db2);
          chatroomDbRef.print();

        } catch (JSONException e) {
          e.printStackTrace();
          log.error("Oops!!  Cannot retrieve Chatrooms list.  Are you authenticated?");
        }
      }
    };
    queueExchange(exchange);
    return this;
  }


  public GaugeClientDaemonTCP createChatroom(final Chatroom chatroom) {
    Exchange exchange = new Exchange() {
      public Packet request() {
        JSONObject json = null;
        try {
          json = createJSONWihHash();
          json.put("chatroom", chatroom.toJSON());
        } catch (JSONException e) {
        }
        return new Packet("CREATE", json.toString());
      }

      public void response(Packet p) {
        log.debug("Updated server with new chatroom!");
      }
    };
    queueExchange(exchange);
    return this;

  }


  /**
   *
   * Verify if logged in
   *
   * @return
   */
  public boolean isLoggedIn() {
    return hash != null;
  }


}
