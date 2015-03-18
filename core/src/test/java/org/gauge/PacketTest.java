package org.gauge;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PacketTest {

  static final Logger log = Logger.getLogger(PacketTest.class);

  private Packet p;
  private final String HEADER = "USER";
  private final String PAYLOAD = "some text here!@!# Hello world!";

  @Before
  public void setUp() throws Exception {
    p = new Packet(HEADER, PAYLOAD);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void canConvertToBytesAndRebuildFromBytes() throws Exception {
    byte[] byteDump = p.toBytes();
    Packet a = new Packet(byteDump);
    assertTrue(a.equals(p));
  }

  @Test
  public void testGetPayload() throws Exception {
    assertTrue(PAYLOAD.equals(p.getPayload()));

  }

  @Test
  public void testSetPayload() throws Exception {
  }

  @Test
  public void testGetHeader() throws Exception {
    assertTrue(HEADER.equals(p.getHeader()));
  }

  @Test
  public void testSetHeader() throws Exception {

  }

  @Test
  public void testToString() throws Exception {
    assertTrue(p.toString().length() > 0);
    log.debug(p.toString());
  }
}