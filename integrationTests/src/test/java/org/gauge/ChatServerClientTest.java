package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ChatServerClientTest {

  static final Logger log = Logger.getLogger(ChatServerClientTest.class);

  private Client client, client2, client3;
  private ChatServer chatServer;

  private final String ADDR = "localhost";
  private final int PORT_SERVER = 14203;
  private final int PORT_CLIENT = 5067;
  private final int PORT_CLIENT2 = 5068;
  private final int PORT_CLIENT3 = 5069;

  // users
  private User mary, john, mark;


  @Before
  public void setUp() throws Exception {
    chatServer = new ChatServer(PORT_SERVER);
    client = new Client(ADDR, PORT_SERVER, PORT_CLIENT);

    // for mocking purposes
    mary = new User("mary", "abc", "bla@bla.com", "localhost", PORT_CLIENT);
    john = new User("john", "123", "bunny@bla.com", "localhost", PORT_CLIENT2);
    mark = new User("mark", "123", "sunflower@bla.com", "localhost", PORT_CLIENT3);

    chatServer.db.add("mary", mary, false);
    chatServer.db.add("john", john, false);
    chatServer.db.add("mark", mark, false);

    chatServer.start();
    Thread.sleep(200);
    client.start();


  }


  @After
  public void tearDown() throws Exception {
    chatServer.stop();
    client.stop();
    Thread.sleep(200);
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
    client.login(mary);
    assertEquals(0, client.getUserList().size());
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

    // assert client 2 does not have chatServer chatroom list loaded
    assertEquals(0, client2.getAllChatrooms().size());
    assertEquals(0, client2.udpDaemon.getChatroomsAll().size());

    // load the chatroom list
    client2.loadChatroomList();
    Thread.sleep(200);

    // assert client 2 now has chatServer chatroom list loaded
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

    // chatServer assertions
    assertEquals(1, chatServer.chatroomDB.size()); // assert 1 active chatroom now
    assertEquals(3, chatServer.chatroomDB.get(chatroomId).size()); // assert 3 users now

    client2.stop();
    client3.stop();
  }


  @Test
  public void testMessage() throws Exception {
    log.debug("------------------------------<<<<<<<<");
    // create a chatroom with all 3 clients in it ///
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
    ////////////////////////////////////////////////

    // hack to get the chatroom ID
    String chatroomId = client2.getActiveChatrooms().chatrooms.keySet().iterator().next();

    // send a message and give it some time to propagate
    client3.message(chatroomId, "Hello world this is a test and hello again 603#21@ haha.");
    Thread.sleep(200);

    // do assertions
    assertNull(client3.getInbox(chatroomId)); // this is null as client3 is sender, hence no inbox created yet
    assertEquals(1, client.getInbox(chatroomId).size());
    assertEquals(1, client2.getInbox(chatroomId).size());

    // send a message and give it some time to propagate.  Now clients should have an inbox.
    client.message(chatroomId, "This is another msg hahaha lol :D :D :) =) ");
    Thread.sleep(200);

    assertEquals(1, client.getInbox(chatroomId).size());  // 1 as it has sent the message
    assertEquals(2, client2.getInbox(chatroomId).size()); // 2 as it has sent no meesage
    assertEquals(1, client3.getInbox(chatroomId).size()); // 1 as it has sent an earlier message and
                                                          // box is now created


    client2.stop();
    client3.stop();
    log.debug("------------------------------<<<<<<<<");
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

    // assert that #users client active chatrooms and chatServer are both 2.
//    client2.chatroomDb.print();
    assertEquals(2, client2.udpDaemon.chatroomsActive.get(chatroomId).size());
    assertEquals(2, chatServer.chatroomDB.get(chatroomId).size());

    // refresh chatroom list with that of chatServer
    client2.loadChatroomList();
    Thread.sleep(200);

    // now assert that #users client all chatrooms and chatServer are both 2.
    assertEquals(2, client2.udpDaemon.chatroomsAll.get(chatroomId).size());
    assertEquals(2, chatServer.chatroomDB.get(chatroomId).size());

    client2.stop();
    client3.stop();

  }
}