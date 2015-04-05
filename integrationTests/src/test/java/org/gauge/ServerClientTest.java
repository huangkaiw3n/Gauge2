package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerClientTest {

  static final Logger log = Logger.getLogger(ServerClientTest.class);

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
    Thread.sleep(200);

  }

  @Test
  public void testCanPing() throws Exception {
    Server server = new Server(1832);
    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);

    server.start();
    client.start();

    client.ping();
    client.ping();
    client.ping();

    Thread.sleep(200);
    client.stop();
    server.stop();
  }

  @Test
  public void testCanLogin() throws Exception {
    Server server = new Server(1832);

    // for mocking purposes
    server.db.add("jhtong", new User("jhtong", "123"), false);
    server.db.add("mary", new User("mary", "abc"), false);

    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);


    server.start();
    client.start();

    client.ping();

    client.login(new User("jhtong", "123","", "192.168.0.1"));  // should pass
    client.login(new User("jhtong", "abc", "", "192.168.0.2"));  // should fail
    client.login(new User("jhtong", "122", "", "192.168.0.3"));  // should fail

    Thread.sleep(200);

    assertEquals(1, server.statusDb.size());

    server.statusDb.print();

    client.stop();
    server.stop();
  }


  @Test
  public void testCanSendList() throws Exception {
    log.debug("-------------------------------------------------");
    Server server = new Server(1833);

    // for mocking purposes
    server.db.add("jhtong", new User("jhtong", "123"), false);
    server.db.add("mary", new User("mary", "abc"), false);

    // set up client
    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1833);
    UserStatusDB db = new UserStatusDB();
    client.setUserlistReference(db);

    client.usersDBRef.print();

    server.start();
    client.start();


    client.login(new User("jhtong", "123","", "192.168.0.1"));  // should pass
    Thread.sleep(200);
//    assertEquals(1, server.statusDb.size());

    client.getUsers();
    Thread.sleep(200);
//    assertEquals(1, client.usersDBRef.size());

//    client.usersDBRef.print();

    client.stop();
    server.stop();
    log.debug("-------------------------------------------------");

  }


  @Test
  public void testRejectInvalidUser() throws Exception {
    Server server = new Server(1832);

    // for mocking purposes
    server.db.add("jhtong", new User("jhtong", "123"), false);
    server.db.add("mary", new User("mary", "abc"), false);

    GaugeClientDaemonTCP client = new GaugeClientDaemonTCP("localhost", 1832);

    server.start();
    client.start();

    client.ping();

    client.login(new User("jhtong", "abc", "", "192.168.0.2"));  // should fail

    Thread.sleep(200);

    assertEquals(0, server.statusDb.size());

    server.statusDb.print();

    client.stop();
    server.stop();

  }
}