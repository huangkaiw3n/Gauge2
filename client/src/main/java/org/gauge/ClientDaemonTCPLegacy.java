package org.gauge;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by joel on 3/14/16.
 *
 * TODO: REWRITE THIS WHOLE CLASS TO SUPPORT SINGLE REQUEST-RESPONSE QUERY
 *
 */
public class ClientDaemonTCPLegacy {

  static final Logger log = Logger.getLogger(ClientDaemonTCPLegacy.class);
  private String hostname;

  private volatile boolean isRunning;
  private volatile PacketQueue pq;

  private volatile Socket sock;
  private int port;

  private enum OperationState {
    NOP, LOGIN, LIST, PING
  }
  private OperationState state;

  public ClientDaemonTCPLegacy(String hostname, int port) {
    init(hostname, port);
  }


  public ClientDaemonTCPLegacy() {
    String hostname = "localhost";
    int port = 9000;
    init(hostname, port);
  }


  private void init(String hostname, int port) {
    isRunning = false;
    this.hostname = hostname;
    this.port = port;
    this.pq = new PacketQueue();
    this.state = OperationState.NOP;
  }


  public ClientDaemonTCPLegacy start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }


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


  private void blockTillReady() {
    while(state != OperationState.NOP);
  }


  private void changeState(OperationState state) {
    this.state = state;
  }


  public ClientDaemonTCPLegacy ping() {
    blockTillReady();
    changeState(OperationState.PING);
    send(new Packet("PING", "123"));
    return this;
  }


  public ClientDaemonTCPLegacy login(User user) {
    blockTillReady();
    if (user.getPassword() == null) {
      return null;
    }
    changeState(OperationState.LOGIN);
    send(new Packet("LOGIN", user.toJSON().toString()));

    return null;
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
  public ClientDaemonTCPLegacy send(Packet packet) {
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
    connectToServer(hostname, port);
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
    while (pq.recvQueueSize() > 0) {
      Packet p = pq.dequeRecv();
      if (state == OperationState.NOP) {

      } else if (state == OperationState.PING) {


      } else if (state == OperationState.LOGIN) {

      }
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
  public ClientDaemonTCPLegacy stop() {
    isRunning = false;
    return this;
  }


}
