package org.gauge;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

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
      s  = socket.accept();
      log.info("New Connection from: " + socket.getInetAddress());

      String payload = getPayload(s);
      process(payload);

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  private String getPayload(Socket s) throws IOException {
    String payload;
    DataInputStream dis;
    int length = 0;
    byte[] buffer;
    dis = new DataInputStream(s.getInputStream());
    length = dis.readInt();
    buffer = new byte[length];
    dis.read(buffer, 0, length);

    payload = new String(buffer, "UTF-8");
    return payload;
  }

  private void process(String s) {
    log.info("Got message: " + s);
  }


  public Server start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    try {
      socket = new ServerSocket(port);
      Thread.sleep(1000); // give server time to start
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.debug("Waiting for connection on " + port + ".");

    isRunning = true;

    Runnable daemon = new Runnable() {
      public void run() {
        while(isRunning) {
          pollConnection();
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
