package org.gauge;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 3/18/15.
 */
public class ClientDaemonTCP {

  static final Logger log = Logger.getLogger(ClientDaemonTCP.class);
  private String hostname;

  private volatile boolean isRunning;
  private volatile PacketQueue pq;

  private volatile Socket sock;
  private int port;

  private enum OperationState {
    NOP, LOGIN, LIST, PING
  }

  private OperationState state;

  private LinkedBlockingQueue<Exchange> exchanges;

  public interface Exchange {
    public Packet request();

    public void response(Packet p);
  }

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
    this.state = OperationState.NOP;
    this.exchanges = new LinkedBlockingQueue<Exchange>();
  }


  public ClientDaemonTCP start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }


    isRunning = true;
    Runnable daemon = new Runnable() {
      public void run() {
        while (isRunning) {
          poll();
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


  private void processExchange(Exchange exchange) {
    try {
      Packet reqP = null, recvP = null;
      Socket sock = null;
      sock = new Socket(hostname, port);
      reqP = exchange.request();
      sendPacket(sock, reqP);
      recvP = recvPacketBlocking(sock);
      exchange.response(recvP);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Packet recvPacketBlocking(Socket sock) {
    Packet recvP = null;
    while (recvP == null) {
      recvP = recvPacket(sock);
    }
    return recvP;
  }


  void poll() {
    Exchange curr;
    while (!exchanges.isEmpty()) {
      curr = exchanges.poll();
      processExchange(curr);
    }
  }


  /**
   * Non-blocking way to retrieve a packet
   *
   * @return
   */
  private Packet recvPacket(Socket sock) {
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


  /**
   * Internal function to send packets.
   *
   * @param packet
   */
  private void sendPacket(Socket sock, Packet packet) {
    if (packet == null) {
      return;  // do not block
    }
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


  public ClientDaemonTCP ping() {
    exchanges.offer(new Exchange() {
      public Packet request() {
        log.debug("SENT A PING PACKET");
        return new Packet("PING", "oo");
      }

      public void response(Packet p) {
        log.debug("Packet: " + p.toString());
        log.debug("RECEIVED A PING PACKET");
      }
    });
    return this;
  }


  public ClientDaemonTCP login() {
    exchanges.offer(new Exchange() {
      public Packet request() {
        //TODO IMPLEMENT
        return new Packet("LOGIN", "username password and change this");
      }

      public void response(Packet p) {
        // TODO IMPLEMENT
      }
    });
    return this;
  }

  //TODO: Implement a list retrieval method here


}
