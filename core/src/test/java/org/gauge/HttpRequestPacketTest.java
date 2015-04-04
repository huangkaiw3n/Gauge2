package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpRequestPacketTest {
    private final String Filename = "/root/Registration.html";
    private final String Request = "GET";
    private final String HttpVersion = "HTTP/1.1";
    private final String Host = "localhost";

    private HttpRequestPacket p;

    @Before
    public void setUp() throws Exception {
        p = new HttpRequestPacket(Filename, HttpVersion, Host);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetFilename() throws Exception {
        p.setFilename(Filename);
        assertTrue(p.getFilename().equals(Filename));
    }

    @Test
    public void testSetHost() throws Exception {
        p.setHost(Host);
        assertTrue(p.getHost().equals(Host));
    }

    @Test
    public void testSetHttpVersion() throws Exception {
        p.setHttpVersion(HttpVersion);
        assertTrue(p.getHttpVersion().equals(HttpVersion));
    }

    @Test
    public void testGetFilename() throws Exception {
        assertTrue(p.getFilename().equals(Filename));
    }

    @Test
    public void testGetRequest() throws Exception {
        assertTrue(p.getRequest().equals(Request));
    }

    @Test
    public void testGetHttpVersion() throws Exception {
        assertTrue(p.getHttpVersion().equals(HttpVersion));
    }

    @Test
    public void testGetHost() throws Exception {
        assertTrue(p.getHost().equals(Host));
    }
}