package org.gauge;

import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by joel on 3/14/15.
 */
public class ClientDaemon {

  static final Logger log = Logger.getLogger(ClientDaemon.class);
  private String hostname;

  private volatile boolean isRunning;

  private int port;
  private volatile Socket sock;

  public ClientDaemon(String hostname, int port) {
    isRunning = false;
    this.hostname = hostname;
    this.port = port;
  }

  public ClientDaemon() {
    isRunning = false;
    this.hostname = "localhost";
    this.port = 9000;
  }


  public ClientDaemon start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    connectToServer(hostname, port);

    isRunning = true;
    Runnable daemon = new Runnable() {
      public void run() {
        while (isRunning) {
        }
        log.info("ClientDaemon stopped.");
      }
    };

    new Thread(daemon).start();
    try {
      Thread.sleep(500);
      log.info("ClientDaemon started.");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Reached.");
    return this;
  }


  public ClientDaemon ping() {
    sendMessage("Ping!!\n");
    return this;
  }


  private void sendMessage(String message) {
    log.debug("Sending message: " + message);
    DataOutputStream dos;

    try {
      dos = new DataOutputStream(sock.getOutputStream());
      byte[] buffer = message.getBytes("UTF-8");
      int length = buffer.length;
      dos.writeInt(length);
      dos.write(buffer, 0, length);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void connectToServer(String hostname, int port) {
    try {
      sock = new Socket(hostname, port);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }


  public ClientDaemon stop() {
    isRunning = false;
    return this;
  }


}
