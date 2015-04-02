package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

public class PeerDaemonTest {

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

  }

  @Test
  public void testLeave() throws Exception {

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