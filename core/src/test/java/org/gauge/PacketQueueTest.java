package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PacketQueueTest {

  private PacketQueue q;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testEnqueueSend() throws Exception {
    q = new PacketQueue();
    q.enqueueSend(new Packet("hello", "world"));
    q.enqueueSend(new Packet("hello", "world"));
    q.enqueueSend(new Packet("hello", "world"));
    q.enqueueSend(new Packet("hello", "world"));
    q.dequeSend();
    q.dequeSend();
    q.dequeSend();
    assertNotNull(q.dequeSend());
    assertNull(q.dequeSend());
  }

  @Test
  public void testDequeSend() throws Exception {

  }

  @Test
  public void testEnqueueRecv() throws Exception {

  }

  @Test
  public void testDequeRecv() throws Exception {

  }
}