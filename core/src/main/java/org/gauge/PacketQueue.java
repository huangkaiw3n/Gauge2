package org.gauge;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joel on 3/18/15.
 */
public class PacketQueue {

  //NOTE: This Queue implementation MUST be thread-safe!
  private volatile Queue<Packet> sendQueue, recvQueue;

  public PacketQueue() {
    sendQueue = new LinkedBlockingQueue<Packet>();
    recvQueue = new LinkedBlockingQueue<Packet>();
  }


  public void enqueueSend(Packet p) {
    sendQueue.offer(p);
  }

  public Packet dequeSend() {
    return sendQueue.poll();
  }

  public void enqueueRecv(Packet p) {
    recvQueue.offer(p);
  }

  public Packet dequeRecv() {
    return recvQueue.poll();
  }

  public int recvQueueSize() {
    return recvQueue.size();
  }

  public int sendQueueSize() {
    return sendQueue.size();
  }

}
