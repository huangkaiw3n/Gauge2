package org.gauge;

import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

  static final Logger log = Logger.getLogger(Misc.class);

  public static void main(String[] args) throws InterruptedException {
      log.debug("Starting server..");
      Scanner s = new Scanner(System.in);
      WebServer s1 = new WebServer(9000, "userDB.csv");  //default csv path @ project root

      s1.start();
      while(true){
          if(s.next().compareTo("q") == 0){// enter q to stop server
              s1.stop();
              break;
          }
      }
      while(true){}
  }
}
