package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    a.start();
    b.start();
    c.start();

    a.create("Hobbies", b.getUser());

  }

  @Test
  public void testStop() throws Exception {

  }
}