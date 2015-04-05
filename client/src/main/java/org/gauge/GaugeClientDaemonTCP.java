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

  private String hash; // the security hash to be used always when sending over information
  private User user;
  private UserStatusDB usersDBRef;  // a reference to the active users DB

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
    this.user = null;
    this.hash = null;
    this.state = OperationState.NOP;
  }


  /**
   * Sets the target DB to update
   * @param db
   * @return
   */
  public GaugeClientDaemonTCP setUserlistReference(UserStatusDB db) {
    this.usersDBRef = db;
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
        log.debug(p.toString());
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
   *
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
          if (usersDBRef == null) throw new Exception();
          usersDBRef.clear();
          usersDBRef.copy(db2);

        } catch (JSONException e) {
          e.printStackTrace();
        } catch (Exception e) {
          log.error("DB not instantiated.");
        }
      }
    };
    return this;
  }


}
