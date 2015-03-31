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
  private PacketQueue pq;

  private int port;
  private volatile Socket sock;


  public ClientDaemon(String hostname, int port) {
    init(hostname, port);
  }


  public ClientDaemon() {
    String hostname = "localhost";
    int port = 9000;
    init(hostname, port);
  }


  private void init(String hostname, int port) {
    isRunning = false;
    this.hostname = hostname;
    this.port = port;
    this.pq = new PacketQueue();
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
          if (pq != null) {
            sendPacket(pq.dequeSend());
          }
//          log.info("PACKET: " + p.toString());
//          if (p != null) {
//            sendPacket(p);
//          }
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
    send(new Packet("PING", "123"));
    return this;
  }


  /**
   * Public function to send packets.
   * <p/>
   * This implementation uses a queue, so that multi-threaded servers
   * can be used in future iterations.
   *
   * @param packet
   * @return
   */
  public ClientDaemon send(Packet packet) {
    if (pq != null) {
      pq.enqueueSend(packet);
    }
    return this;
  }


  /**
   * Internal function to send packets.
   *
   * @param packet
   */
  private void sendPacket(Packet packet) {
    if (packet == null) {
      return;  // do not block
    }
    log.debug("Sending message: " + packet.toString());
    DataOutputStream dos;

    try {
      dos = new DataOutputStream(sock.getOutputStream());
      byte[] buffer = packet.toBytes();
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


  /**
   * Stops the client raw daemon,
   *
   * @return
   */
  public ClientDaemon stop() {
    isRunning = false;
    return this;
  }


}
