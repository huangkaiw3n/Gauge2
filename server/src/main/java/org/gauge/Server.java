package org.gauge;

import org.apache.log4j.Logger;

/**
 * Created by joel on 3/14/15.
 */
public class Server {

  private volatile boolean isRunning;
  static final Logger log = Logger.getLogger(Server.class);

  public Server(int port) {
    isRunning = false;
  }

  public Server start() {
    if (isRunning) {
      // exit and return if already running
      return this;
    }
    isRunning = true;
    Runnable daemon = new Runnable() {
      public void run() {
        while(isRunning) {}
        log.info("Server stopped.");
      }
    };

    new Thread(daemon).start();
    log.info("Server started.");
    return this;
  }

  public Server stop() {
    isRunning = false;
    return this;
  }

}
