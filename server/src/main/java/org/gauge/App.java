package org.gauge;

import org.apache.log4j.Logger;

/**
 * Hello world!
 */
public class App {

  static final Logger log = Logger.getLogger(Misc.class);


  /**
   *
   * Starts the server.  If port is specified, the specified inbound
   * port will be used.
   *
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    log.debug("Starting chatServer..");
    ChatServer chatServer;
    if (args.length == 1) {
      chatServer = new ChatServer(Integer.parseInt(args[0]));
    } else {
      chatServer = new ChatServer(9000);
    }
    chatServer.start();
    chatServer.stop();
  }
}
