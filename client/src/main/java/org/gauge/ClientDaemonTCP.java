package org.gauge;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 3/18/15.
 */
public class ClientDaemonTCP {

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


  void poll() {
    Exchange curr;
    while(!exchanges.isEmpty()) {
      curr = exchanges.poll();

      try {
        Packet reqP, recvP;
        Socket sock = new Socket(hostname, port);
        reqP = curr.request();
        sendPacket(sock, reqP);
        recvP = recvPacket(sock);
        curr.response(recvP);
        sock.close();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private Packet recvPacket(Socket sock) {
    // TODO: Implement
    return null;
  }

  private void sendPacket(Socket sock, Packet p) {
    // TODO: Implement

  }


  private ClientDaemonTCP login() {
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
