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

  }

  @Test
  public void testCanConnectToServer() throws Exception {
//    log.info("Ran a test");
    Server server = new Server(1832);
    ClientDaemon client = new ClientDaemon("localhost", 1832);

    server.start();
    client.start();

    client.ping();

    Thread.sleep(2000);
    server.stop();
  }
}