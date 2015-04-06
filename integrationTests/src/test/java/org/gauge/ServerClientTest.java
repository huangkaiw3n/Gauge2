package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerClientTest {

  static final Logger log = Logger.getLogger(ServerClientTest.class);

  private Client client, client2;
  private Server server;

  private final String ADDR = "localhost";
  private final int PORT_SERVER = 14203;
  private final int PORT_CLIENT = 5067;
  private final int PORT_CLIENT2 = 5068;

  // users
  private User mary, john;


  @Before
  public void setUp() throws Exception {
    server = new Server(PORT_SERVER);
    client = new Client(ADDR, PORT_SERVER, PORT_CLIENT);

    // for mocking purposes
    mary = new User("mary", "abc", "bla@bla.com", "localhost", PORT_CLIENT);
    john = new User("john", "123", "bunny@bla.com", "localhost", PORT_CLIENT2);
    server.db.add("mary", mary, false);
    server.db.add("john", john, false);

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

    // verify that retrieve list of ALL chatrooms work

    log.debug("------------------------------------<<<<<<<<");

    // assert client 2 does not have server chatroom list loaded
    assertEquals(0, client2.getAllChatrooms().size());
    assertEquals(0, client2.udpDaemon.getChatroomsAll().size());

    // load the chatroom list
    client2.loadChatroomList();
    Thread.sleep(200);

    // assert client 2 now has server chatroom list loaded
    client2.chatroomDb.print();
    client2.udpDaemon.getChatroomsAll().print();
    log.debug("------------------------------------<<<<<<<<");

    assertEquals(1, client2.getAllChatrooms().size());
    assertEquals(1, client2.udpDaemon.getChatroomsAll().size());
    assertEquals(1, client2.chatroomDb.size());

    client2.stop();

  }

  @Test
  public void testCreate1() throws Exception {

  }

  @Test
  public void testLeave() throws Exception {

  }
}