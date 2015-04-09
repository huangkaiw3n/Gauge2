package org.gauge;

import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

  public static ChatServer cs;
  static final Logger log = Logger.getLogger(Misc.class);

  public static void main(String[] args) throws InterruptedException {
      log.debug("Starting server..");
      Scanner s = new Scanner(System.in);
      WebServer ws = new WebServer(80, "userDB.csv");  //default csv path @ project root
      cs = new ChatServer(9000, ws.getDb());

      ws.start();
      cs.start();
      while(true){
          if(s.next().compareTo("q") == 0){// enter q to stop server
              ws.stop();
              cs.stop();
              break;
          }
      }
  }
}
