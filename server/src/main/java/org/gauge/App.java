package org.gauge;

import org.apache.log4j.Logger;

/**
 * Hello world!
 */
public class App {

  static final Logger log = Logger.getLogger(Misc.class);

  public static void main(String[] args) throws InterruptedException {
    log.debug("Starting server..");
    Server server = new Server(9000);
    server.start();
    server.stop();
  }
}
