package org.gauge;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 4/4/15.
 */
public class PeerServerDaemon {

  private final int PORT_INCOMING = 9000;

  static final Logger log = Logger.getLogger(PeerServerDaemon.class);
  private volatile boolean isRunning;
  protected int port;

  protected volatile DatagramSocket incoming, outgoing;
  protected volatile ConcurrentHashMap<String, Chatroom> chatroomsAll; // use ID as key

  private ConcurrentHashMap<String, User> users;

  /**
   * Interface for a callback function.
   */
  protected interface Callback {
    public void execute();
  }

  protected class ChatroomPacket {
    public String username;
    public Packet packet;
    public Callback callback;

    public ChatroomPacket(String username, Packet packet) {
      this.packet = packet;
      this.username = username;
      this.callback = null;
    }

    public ChatroomPacket(String username, Packet packet, Callback callback) {
      this.packet = packet;
      this.username = username;
      this.callback = callback;
    }
  }

  // this is needed between threads to not corrupt buffer
  protected volatile LinkedBlockingQueue<ChatroomPacket> sendQueue;
  // a Map of linked-list message queues, separated by rooms
  // the main key refers to the main message queue, intended for daemon.
  protected volatile LinkedBlockingQueue<Packet> recvQueue;

  /**
   * Returns a linked list of messages for a given chatroom.
   *
   * @return
   */
  public LinkedBlockingQueue<Packet> inbox() {
    return recvQueue;
  }


  private void poll() {
    while (!inbox().isEmpty()) {
      Packet packet = inbox().poll();
      // TODO add process packet code here
    }
  }


  protected void enqueRecv(Packet packet) {
    recvQueue.offer(packet);
  }


  /**
   *
   * Helper function to send a packet.  If packet cannot be sent,
   * returns false.  True otherwise.
   *
   * @param user
   * @param packet
   * @return
   */
  protected boolean sendPacket(User user, Packet packet) {
    if (user == null || packet == null) return false;
    byte[] data = packet.toBytes();
    try {
      InetAddress ip = Misc.getInetAddress(user.getIp());
      DatagramPacket datagram = new DatagramPacket(data, data.length, ip, user.getPort());
      outgoing.send(datagram);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return false;
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  /**
   * Internal function deques packets and broadcasts them to relevant chatroom.
   */
  protected void executeSend() {
    while (sendQueue.size() > 0) {
      ChatroomPacket cp;
      Packet packet;
      User user;
      boolean isSent;

      cp = sendQueue.remove();
      packet = cp.packet;
      user = users.get(cp.username);

     sendPacket(user, packet);

      // execute callback if any
      if (cp.callback != null) {
        cp.callback.execute();
      }
    }
  }


  /**
   * Starts the daemon.
   *
   * @return
   */
  public PeerServerDaemon start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    isRunning = true;
    Runnable incomingDaemon = new Runnable() {
      public void run() {
        try {
          incoming = new DatagramSocket(null);
          incoming.setReuseAddress(true);
          incoming.bind(new InetSocketAddress(port));
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (isRunning) {
          try {
            // get packet and put into queue
            byte[] buffer = new byte[2048];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            incoming.receive(datagram);
            Packet p = new Packet(Arrays.copyOf(buffer, datagram.getLength()));
            log.debug(prettyUsername() + " Packet received=" + p.toString());
            enqueRecv(p); // put packet into correct mailbox

          } catch (IOException e) {
            e.printStackTrace();
          }

        }
        if (incoming != null) incoming.close();
      }
    };


    Runnable outgoingDaemon = new Runnable() {
      public void run() {
        try {
          outgoing = new DatagramSocket();
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (isRunning) {
          // deque send queue and broacast to relevant chatroomsActive
          executeSend();
        }
        if (outgoing != null) outgoing.close();
      }
    };


    Runnable processRequests = new Runnable() {
      public void run() {
        while (isRunning) {
          poll();
        }
      }
    };

    new Thread(incomingDaemon).start();
    log.debug(prettyUsername() + " Starting incoming thread.");
    new Thread(outgoingDaemon).start();
    log.debug(prettyUsername() + " Starting outgoing thread.");
    new Thread(processRequests).start();
    log.debug(prettyUsername() + " Starting processRequests thread.");
    try {
      Thread.sleep(500);
      log.info("PeerDaemon started.");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return this;
  }

  private String prettyUsername() {
    return "[ SERVER ]";
  }

  /**
   * Stops the daemon.
   *
   * @return
   */
  public PeerServerDaemon stop() {
    isRunning = false;
    log.info("PeerDaemon stopped.");
    return this;
  }
}
