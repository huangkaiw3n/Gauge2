package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerClientTest {

  static final Logger log = Logger.getLogger(ServerClientTest.class);

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

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

    Thread.sleep(2000);
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

    client.login(new User("jhtong", "123"));  // should pass
    client.login(new User("jhtong", "abc"));  // should fail
    client.login(new User("jhtong", "122"));  // should fail

    Thread.sleep(2000);
    client.stop();
    server.stop();
  }
}