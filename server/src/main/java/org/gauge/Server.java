package org.gauge;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joel on 3/14/15.
 */
public class Server {

  static final Logger log = Logger.getLogger(Server.class);

  private volatile boolean isRunning;
  private volatile ServerSocket socket;

  private int port;

  public Server(int port) {
    isRunning = false;
    this.port = port;
  }

  public Server() {
    isRunning = false;
    this.port = 9000;
  }


  private void pollConnection() {
    Socket s;
    try {
      StringBuilder sb = new StringBuilder();
      String currLine;

      s  = socket.accept();
      log.info("New Connection from: " + socket.getInetAddress());
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

      while((currLine = br.readLine()) != null) {
        sb.append(currLine);
      }

      process(sb.toString());

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  private void process(String s) {

  }


  public Server start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    try {
      socket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.debug("Waiting for connection on " + port + ".");

    isRunning = true;
    Runnable daemon = new Runnable() {
      public void run() {
        while(isRunning) {

        }
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
