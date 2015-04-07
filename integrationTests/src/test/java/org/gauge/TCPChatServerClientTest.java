package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TCPChatServerClientTest {

  static final Logger log = Logger.getLogger(TCPChatServerClientTest.class);

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
    Thread.sleep(200);

  }

  @Test
  public void testCanPing() throws Exception {
    ChatServer chatServer = new ChatServer(1832);
    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);

    chatServer.start();
    client.start();

    client.ping();
    client.ping();
    client.ping();

    Thread.sleep(200);
    client.stop();
    chatServer.stop();
  }

  @Test
  public void testCanLogin() throws Exception {
    ChatServer chatServer = new ChatServer(1832);

    // for mocking purposes
    chatServer.db.add("jhtong", new User("jhtong", "123"), false);
    chatServer.db.add("mary", new User("mary", "abc"), false);

    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);


    chatServer.start();
    client.start();

    client.ping();

    client.login(new User("jhtong", "123","", "192.168.0.1"));  // should pass
    client.login(new User("jhtong", "abc", "", "192.168.0.2"));  // should fail
    client.login(new User("jhtong", "122", "", "192.168.0.3"));  // should fail

    Thread.sleep(200);

    assertEquals(1, chatServer.statusDb.size());

    chatServer.statusDb.print();

    client.stop();
    chatServer.stop();
  }


  @Test
  public void testCanSendUserlist() throws Exception {
    ChatServer chatServer = new ChatServer(1833);

    // for mocking purposes
    chatServer.db.add("jhtong", new User("jhtong", "123"), false);
    chatServer.db.add("mary", new User("mary", "abc"), false);

    // set up client
    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1833);
    UserStatusDB db = new UserStatusDB();
    client.setUserlistReference(db);

    client.usersDBRef.print();

    chatServer.start();
    client.start();


    client.login(new User("jhtong", "123","", "192.168.0.1"));  // should pass
    Thread.sleep(200);
//    assertEquals(1, chatServer.statusDb.size());

    client.getUsers();
    Thread.sleep(200);
//    assertEquals(1, client.usersDBRef.size());

//    client.usersDBRef.print();

    client.stop();
    chatServer.stop();

  }


  @Test
  public void testCanSendChatroomList() throws Exception {
    ChatServer chatServer = new ChatServer(1833);

    // for mocking purposes
    User jhtong, mary;
    jhtong = new User("jhtong", "123");
    mary = new User("mary", "abc");
    User[] users = {jhtong, mary};
    chatServer.db.add("jhtong", jhtong, false);
    chatServer.db.add("mary", mary, false);
    chatServer.chatroomDB.add(new Chatroom("Grapes", users));  // mock and assume there are users in chatroom

    // set up client
    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1833);
    UserStatusDB db = new UserStatusDB();
    client.setUserlistReference(db);
    client.setChatroomsReference(new ChatroomDB());

    chatServer.start();
    client.start();

    client.login(new User("jhtong", "123","", "192.168.0.1"));  // should pass
    Thread.sleep(200);
//    assertEquals(1, chatServer.statusDb.size());

    client.getChatrooms();
    Thread.sleep(200);
//    assertEquals(1, client.usersDBRef.size());

//    client.usersDBRef.print();

    client.stop();
    chatServer.stop();

  }


  @Test
  public void testRejectInvalidUser() throws Exception {
    ChatServer chatServer = new ChatServer(1832);

    // for mocking purposes
    chatServer.db.add("jhtong", new User("jhtong", "123"), false);
    chatServer.db.add("mary", new User("mary", "abc"), false);

    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);

    chatServer.start();
    client.start();

    client.ping();

    client.login(new User("jhtong", "abc", "", "192.168.0.2"));  // should fail

    Thread.sleep(200);

    assertEquals(0, chatServer.statusDb.size());

    chatServer.statusDb.print();

    client.stop();
    chatServer.stop();

  }
}