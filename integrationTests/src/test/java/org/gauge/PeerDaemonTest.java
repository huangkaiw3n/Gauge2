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
    userA = new User("john", "", "", "localhost");
    userB = new User("mary", "", "", "localhost");
    userC = new User("david", "", "", "localhost");

    User[] users = {
            userA, userB
    };

    Chatroom chatroom = new Chatroom("holidays", users);

    a = new PeerDaemon(userA, 7000);
    b = new PeerDaemon(userB, 7001);
    c = new PeerDaemon(userC, 7002);

    ConcurrentHashMap<String, Chatroom> listActive = new ConcurrentHashMap<String, Chatroom>();
    listActive.put(chatroom.getTitle(), chatroom);

    ConcurrentHashMap<String, Chatroom> listAll = new ConcurrentHashMap<String, Chatroom>();
    listAll.put(chatroom.getTitle(), chatroom);

    a.setChatroomsList(listAll);
    b.setChatroomsList(listAll);
    c.setChatroomsList(listAll);

    a.setActiveChatroomsList(listActive);
    b.setActiveChatroomsList(listActive);
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

  }

  @Test
  public void testStop() throws Exception {

  }
}