package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerClientTest {

  static final Logger log = Logger.getLogger(ServerClientTest.class);

  private Client client, client2, client3;
  private Server server;

  private final String ADDR = "localhost";
  private final int PORT_SERVER = 14203;
  private final int PORT_CLIENT = 5067;
  private final int PORT_CLIENT2 = 5068;
  private final int PORT_CLIENT3 = 5069;

  // users
  private User mary, john, mark;


  @Before
  public void setUp() throws Exception {
    server = new Server(PORT_SERVER);
    client = new Client(ADDR, PORT_SERVER, PORT_CLIENT);

    // for mocking purposes
    mary = new User("mary", "abc", "bla@bla.com", "localhost", PORT_CLIENT);
    john = new User("john", "123", "bunny@bla.com", "localhost", PORT_CLIENT2);
    mark = new User("mark", "123", "sunflower@bla.com", "localhost", PORT_CLIENT3);

    server.db.add("mary", mary, false);
    server.db.add("john", john, false);
    server.db.add("mark", mark, false);

    server.start();
    Thread.sleep(200);
    client.start();


  }


  @After
  public void tearDown() throws Exception {
    server.stop();
    client.stop();
  }


  @Test
  public void testLogin() throws Exception {
    client.login(mary);
    assertTrue(client.isLoggedIn());
  }

  @Test
  public void testLogout() throws Exception {

  }

  @Test
  public void testLoadUserlist() throws Exception {
    assertEquals(0, client.getUserList().size());
    client.login(mary);
    client.loadUserlist();
    Thread.sleep(200);
    assertEquals(1, client.getUserList().size());
  }


  @Test
  public void testCreateAndLoadChatroomList() throws Exception {
    // create new client 2
    client2 = new Client(ADDR, PORT_SERVER, PORT_CLIENT2);
    client2.start();

    client.login(mary);
    client2.login(john);

    assertEquals(0, client.getActiveChatrooms().size());
    assertEquals(0, client2.getActiveChatrooms().size());

    // create a new chatroom by mary with john
    client.create("Food", john);

    Thread.sleep(200);

    assertEquals(1, client.getActiveChatrooms().size());
    assertEquals(1, client2.getActiveChatrooms().size());
//    client.getActiveChatrooms().print();
//    client2.getActiveChatrooms().print();

    // verify that retrieve list of ALL chatrooms work

    // assert client 2 does not have server chatroom list loaded
    assertEquals(0, client2.getAllChatrooms().size());
    assertEquals(0, client2.udpDaemon.getChatroomsAll().size());

    // load the chatroom list
    client2.loadChatroomList();
    Thread.sleep(200);

    // assert client 2 now has server chatroom list loaded
    client2.chatroomDb.print();
    client2.udpDaemon.getChatroomsAll().print();

    assertEquals(1, client2.getAllChatrooms().size());
    assertEquals(1, client2.udpDaemon.getChatroomsAll().size());
    assertEquals(1, client2.chatroomDb.size());

    client2.stop();

  }


  @Test
  public void testJoin() throws Exception {
    client.login(mary);

    // create new client 2
    client2 = new Client(ADDR, PORT_SERVER, PORT_CLIENT2);
    client2.start();
    client2.login(john);

    // create new client 3
    client3 = new Client(ADDR, PORT_SERVER, PORT_CLIENT3);
    client3.start();
    client3.login(mark);

    // create a new chatroom with mary and john
    client.create("Food", john);

    Thread.sleep(200);

    assertEquals(1, client.getActiveChatrooms().size());
    assertEquals(1, client2.getActiveChatrooms().size());
    assertEquals(0, client3.getActiveChatrooms().size());

    // Retrieve user list for Mark and let him join a chatroom
    client3.loadChatroomList();
    Thread.sleep(200);
    assertEquals(1, client3.getAllChatrooms().size());

    // Get the ID of the chatroom that client and client2 are in (John and Mary).
    String chatroomId = client3.getAllChatrooms().chatrooms.keySet().iterator().next();
    // join chatroom
    client3.join(chatroomId);
    Thread.sleep(200);

    // client assertions
    assertEquals(1, client3.getActiveChatrooms().size()); // assert 1 active chatroom now
    assertEquals(3, client3.getActiveChatrooms().get(chatroomId).size()); // assert 3 users now

    // server assertions
    assertEquals(1, server.chatroomDB.size()); // assert 1 active chatroom now
    assertEquals(3, server.chatroomDB.get(chatroomId).size()); // assert 3 users now

    client2.stop();
    client3.stop();
  }


  @Test
  public void testLeave() throws Exception {
    client.login(mary);

    // create new client 2
    client2 = new Client(ADDR, PORT_SERVER, PORT_CLIENT2);
    client2.start();
    client2.login(john);

    // create new client 3
    client3 = new Client(ADDR, PORT_SERVER, PORT_CLIENT3);
    client3.start();
    client3.login(mark);

    // create a new chatroom with mark, john and mark
    User[] users = {john, mark};
    client.create("Food", users);
    Thread.sleep(200);

    // load all chatroom lists
    client.loadChatroomList();
    client2.loadChatroomList();
    client3.loadChatroomList();

    assertEquals(1, client2.getActiveChatrooms().size());  // assert 1 chatroom
    String chatroomId = client2.getActiveChatrooms().chatrooms.keySet().iterator().next();
    assertEquals(3, client2.getActiveChatrooms().get(chatroomId).size()); // assert 3 people in chatroom
    client2.leave(chatroomId);
    Thread.sleep(200);

    // assert that #users client active chatrooms and server are both 2.
//    client2.chatroomDb.print();
    assertEquals(2, client2.udpDaemon.chatroomsActive.get(chatroomId).size());
    assertEquals(2, server.chatroomDB.get(chatroomId).size());

    // refresh chatroom list with that of server
    client2.loadChatroomList();
    Thread.sleep(200);

    // now assert that #users client all chatrooms and server are both 2.
    assertEquals(2, client2.udpDaemon.chatroomsAll.get(chatroomId).size());
    assertEquals(2, server.chatroomDB.get(chatroomId).size());

    client2.stop();
    client3.stop();

  }
}