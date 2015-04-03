package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

public class PeerDaemonTest {

  static final Logger log = Logger.getLogger(PeerDaemonTest.class);

  private PeerDaemon a, b, c;


  /**
   *
   * Sets up daemons A, B, C with user IDs.  A and B are in an active chatroom with each other.
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    User userA, userB, userC;
    userA = new User("john", "", "", "localhost", 14000);
    userB = new User("mary", "", "", "localhost", 14001);
    userC = new User("david", "", "", "localhost", 14002);

    a = new PeerDaemon(userA, 14000);
    b = new PeerDaemon(userB, 14001);
    c = new PeerDaemon(userC, 14002);
  }

  @After
  public void tearDown() throws Exception {
    a.stop();
    b.stop();
    c.stop();
    a = b = c = null;
    Thread.sleep(200);
  }

  @Test
  public void testSetChatroomsList() throws Exception {

  }

  @Test
  public void testJoin() throws Exception {
    a.start();
    b.start();
    c.start();

    assertEquals(0, a.chatroomsAll.size());
    assertEquals(0, b.chatroomsAll.size());
    assertEquals(0, c.chatroomsAll.size());

    // create chatroom and join
    a.create("Cats and Dogs", b.getUser());

    Thread.sleep(200);
    a.printChatroomActive();
    b.printChatroomActive();
    c.printChatroomActive();

    // hack to set chatroomsAll to same value; in reality, this should
    // be sent and retransmitted by central server.
    c.chatroomsAll = b.chatroomsAll = a.chatroomsAll;

    assertEquals(a.chatroomsActive.size(), b.chatroomsActive.size());
    assertEquals(1, a.chatroomsActive.size());
    assertEquals(1, b.chatroomsActive.size());
    assertEquals(0, c.chatroomsActive.size());

    assertEquals(1, a.chatroomsAll.size());
    assertEquals(1, b.chatroomsAll.size());
    assertEquals(1, c.chatroomsAll.size());

    //--------------------------------------------------
    // let b leave the chatroom.  Chatroom should close.
    // there is only 1 chatroom.  Hence this returns the ID of this chatroom.
    String chatroomId = a.chatroomsAll.keySet().iterator().next();
    c.join(chatroomId);
    Thread.sleep(200);

    log.debug("--------------------------------------------------------");

    a.printChatroomActive();
    b.printChatroomActive();
    c.printChatroomActive();

    assertEquals(1, a.chatroomsActive.size());
    assertEquals(1, b.chatroomsActive.size());
    assertEquals(1, c.chatroomsActive.size());

    assertEquals(1, a.chatroomsAll.size());
    assertEquals(1, b.chatroomsAll.size());
    assertEquals(1, c.chatroomsAll.size());

  }


  @Test
  public void testLeave() throws Exception {
    a.start();
    b.start();
    c.start();

    // create chatroom and join
    a.create("Cats and Dogs", b.getUser());

    Thread.sleep(200);
    a.printChatroomActive();
    b.printChatroomActive();

    assertEquals(a.chatroomsActive.size(), b.chatroomsActive.size());
    assertEquals(1, a.chatroomsActive.size());
    assertEquals(1, b.chatroomsActive.size());

    //--------------------------------------------------
    // let b leave the chatroom.  Chatroom should close.
    // there is only 1 chatroom.  Hence this returns the ID of this chatroom.
    String chatroomId = a.chatroomsActive.keySet().iterator().next();
    a.leave(chatroomId);
    Thread.sleep(200);

    a.printChatroomActive();
    b.printChatroomActive();

    assertEquals(0, a.chatroomsActive.size());
    assertEquals(0, b.chatroomsActive.size());

  }

  @Test
  public void testLeaveMultiple() throws Exception {
    a.start();
    b.start();
    c.start();

    // create chatroom and join
    a.create("Cats and Dogs", b.getUser());

    Thread.sleep(200);
    a.printChatroomActive();
    b.printChatroomActive();

    assertEquals(a.chatroomsActive.size(), b.chatroomsActive.size());
    assertEquals(1, a.chatroomsActive.size());
    assertEquals(1, b.chatroomsActive.size());

    //--------------------------------------------------
    // let b leave the chatroom.  Chatroom should close.
    // there is only 1 chatroom.  Hence this returns the ID of this chatroom.
    String chatroomId = a.chatroomsActive.keySet().iterator().next();
    a.leave(chatroomId);
    Thread.sleep(200);

    a.printChatroomActive();
    b.printChatroomActive();

    assertEquals(0, a.chatroomsActive.size());
    assertEquals(0, b.chatroomsActive.size());

  }

  @Test
  public void testEnqueSend() throws Exception {

  }

  @Test
  public void testInbox() throws Exception {

  }

  @Test
  public void testStart() throws Exception {
  }

  @Test
  public void testStop() throws Exception {

  }

  @Test
  public void testCreate() throws Exception {
    a.start();
    b.start();
    c.start();

    a.create("Hobbies", b.getUser());

    a.printChatroomActive();
    Thread.sleep(200);
    b.printChatroomActive();

    assertEquals(a.chatroomsActive.size(), b.chatroomsActive.size());
  }

  @Test
  public void testCreateMany() throws Exception {
    a.start();
    b.start();
    c.start();

    User[] users = {b.getUser(), c.getUser()};

    a.create("Hobbies", users);

    a.printChatroomActive();
    Thread.sleep(200);
    b.printChatroomActive();
    c.printChatroomActive();

    assertEquals(a.chatroomsActive.size(), b.chatroomsActive.size());
    assertEquals(a.chatroomsActive.size(), c.chatroomsActive.size());
  }
}