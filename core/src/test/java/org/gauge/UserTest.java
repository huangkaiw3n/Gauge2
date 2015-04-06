package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testEquals() throws Exception {

    User a,b,c;
    a = new User("john", "abc", "bla@123.com", "localhost");
    b = new User("john", "123", "bla2@123.com", "192.168.7.2");
    c = new User("mary", "123", "bla2@123.com", "192.168.7.2");

    assertTrue(a.equals(b));
    assertFalse(a.equals(c));

  }
}