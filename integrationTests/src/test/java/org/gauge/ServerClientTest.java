package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerClientTest {

  static final Logger log = Logger.getLogger(ServerClientTest.class);

  private Client client;
  private Server server;

  private final String ADDR = "localhost";
  private final int PORT_SERVER = 14203;
  private final int PORT_CLIENT = 5067;

  // users
  private User mary, john;


  @Before
  public void setUp() throws Exception {
    server = new Server(PORT_SERVER);
    client = new Client(ADDR, PORT_SERVER, PORT_CLIENT);

    // for mocking purposes
    mary = new User("mary", "abc", "bla@bla.com", "localhost", PORT_CLIENT);
    server.db.add("mary", mary, false);

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
    log.debug("------------------------------------<<<<<<<<");
    assertEquals(0, client.getUserList().size());
    client.login(mary);
    client.loadUserlist();
    Thread.sleep(1000);
    assertEquals(1, client.getUserList().size());
    log.debug("------------------------------------<<<<<<<<");
  }

  @Test
  public void testCreate() throws Exception {

  }

  @Test
  public void testCreate1() throws Exception {

  }

  @Test
  public void testLeave() throws Exception {

  }
}