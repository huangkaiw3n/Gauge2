package org.gauge;

/**
 * Created by joel on 3/14/15.
 */


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerClientTests {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testCanConnectToServer() throws Exception {
    Server server = new Server(9000);
    server.start();
    Thread.sleep(2000);
    server.stop();
  }
}
