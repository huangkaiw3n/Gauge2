package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Created by joel on 4/1/15.
 */
public class GaugeClientDaemonTCP extends SimpleClientDaemonTCP {
  static final Logger log = Logger.getLogger(GaugeClientDaemonTCP.class);

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
    this.state = OperationState.NOP;
  }


  public OperationState getState() {
    return state;
  }


  public GaugeClientDaemonTCP login(final User user) {
    Exchange exchange = new Exchange() {
      public Packet request() {
        if (user.getUsername() == null || user.getPassword() == null) {
          return null;
        }
        return new Packet("LOGIN", user.toJSONWithPassword().toString());
      }

      public void response(Packet p) {
        log.debug(p.toString());
      }
    };
    queueExchange(exchange);
    return this;
  }

  //TODO: Implement a list retrieval method here







}
