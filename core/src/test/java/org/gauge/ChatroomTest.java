package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatroomTest {

  static final Logger log = Logger.getLogger(ChatroomTest.class);

  private Chatroom c;

  @Before
  public void setUp() throws Exception {
    c = new Chatroom("bla");
    c.add(new User("jhtong", "123"));
    c.add(new User("mary", "abc"));
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testAdd() throws Exception {
    assertEquals(2, c.size());
    c.add(new User("Fred", "789"));
    assertEquals(3, c.size());
  }

  @Test
  public void testRemove() throws Exception {
    assertEquals(2, c.size());
    c.add(new User("Fred", "789"));
    assertEquals(3, c.size());
    c.remove("Fred");
    assertEquals(2, c.size());
  }

  @Test
  public void testToJSON() throws Exception {
    log.debug(c.toJSON().toString());
  }

  @Test
  public void testFromJSON() throws Exception {
    Chatroom d = new Chatroom(c.toJSON());
    assertTrue(d.toString().equals(c.toString()));
  }

  @Test
  public void testToString() throws Exception {
    log.debug(c.toString());
  }

  @Test
  public void testGet() throws Exception {
    User user = c.get("mary");
    assertNotNull(user);
    assertTrue(user.getUsername().equals("mary"));

    User user2 = c.get("zzz");
    assertNull(user2);
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(2, c.size());
  }
}