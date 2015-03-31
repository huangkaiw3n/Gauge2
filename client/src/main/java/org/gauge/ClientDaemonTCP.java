package org.gauge;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by joel on 3/14/15.
 */
public class ClientDaemonTCP {

  static final Logger log = Logger.getLogger(ClientDaemonTCP.class);
  private String hostname;

  private volatile boolean isRunning;
  private volatile PacketQueue pq;

  private volatile Socket sock;
  private int port;


  public ClientDaemonTCP(String hostname, int port) {
    init(hostname, port);
  }


  public ClientDaemonTCP() {
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


  public ClientDaemonTCP start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    connectToServer(hostname, port);

    isRunning = true;
    Runnable daemon = new Runnable() {
      public void run() {
        while (isRunning) {
          sendPacket(pq.dequeSend());
          recvPacket();
          processPackets();
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


  public ClientDaemonTCP ping() {
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
  public ClientDaemonTCP send(Packet packet) {
    pq.enqueueSend(packet);
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


  /**
   * Non-blocking way to retrieve a packet
   *
   * @return
   */
  private Packet getPacket() {
    DataInputStream dis;
    Packet packet = null;
    try {
      dis = new DataInputStream(sock.getInputStream());
      if (dis.available() == 0) {
        return null;
      }
      int size = dis.readInt();
      byte[] buffer = new byte[size];
      dis.read(buffer);
      packet = new Packet(buffer);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return packet;
  }


  private void recvPacket() {
    Packet p = getPacket();
    if (p != null) {
      pq.enqueueRecv(p);
    }
  }


  private void processPackets() {
    while(pq.recvQueueSize() > 0) {
      Packet p = pq.dequeRecv();
      log.debug("Client packet received: " + p.toString());
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
  public ClientDaemonTCP stop() {
    isRunning = false;
    return this;
  }


}
