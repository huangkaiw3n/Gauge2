package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpReplyPacketTest {
    private HttpReplyPacket p;
    private final String HttpVersion = "HTTP/1.1";
    private final String Status = "OK";
    private final String StatusNo = "200";
    private final String Date = "2015/04/02 14:21:00";
    private final String Content_Type = "text/html";
    private final String Content_Length = null;
    private final String PayLoad = "Hello World\n";

    @Before
    public void setUp() throws Exception {
        p = new HttpReplyPacket(Status, StatusNo, Content_Type,Content_Length);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetHttpVersion() throws Exception {
        p.setHttpVersion();
        assertTrue(p.getHttpVersion().equals(HttpVersion));
    }

    @Test
    public void testSetStatus() throws Exception {
        p.setStatus(Status);
        assertTrue(p.getStatus().equals(Status));
    }

    @Test
    public void testSetStatusNo() throws Exception {
        p.setStatusNo(StatusNo);
        assertTrue(p.getStatusNo().equals(StatusNo));
    }

    @Test
    public void testSetDate() throws Exception {
        p.setDate(Date);
        assertTrue(p.getDate().equals(Date));
    }

    @Test
    public void testSetDate1() throws Exception {

    }

    @Test
    public void testSetContent_Type() throws Exception {
        p.setContent_Type(Content_Type);
        assertTrue(p.getContent_Type().equals(Content_Type));
    }

    @Test
    public void testSetContent_Length() throws Exception {
        p.setContent_Length(Integer.toString(PayLoad.length()));
        assertTrue(p.getContent_Length().equals(Integer.toString(PayLoad.length())));
    }

    @Test
    public void testSetPayLoad() throws Exception {
        p.setPayLoad(PayLoad);
        assertTrue(p.getPayLoad().equals(PayLoad));
    }

    @Test
    public void testGetHttpVersion() throws Exception {
        assertTrue(p.getHttpVersion().equals(HttpVersion));
    }

    @Test
    public void testGetStatus() throws Exception {
        assertTrue(p.getStatus().equals(Status));
    }

    @Test
    public void testGetStatusNo() throws Exception {
        assertTrue(p.getStatusNo().equals(StatusNo));
    }

    @Test
    public void testGetDate() throws Exception {
        p.setDate(Date);
        assertTrue(p.getDate().equals(Date));
    }

    @Test
    public void testGetContent_Type() throws Exception {
        assertTrue(p.getContent_Type().equals(Content_Type));
    }

    @Test
    public void testGetContent_Length() throws Exception {
        p.setContent_Length(Integer.toString(PayLoad.length()));
        assertTrue(p.getContent_Length().equals(Integer.toString(PayLoad.length())));
    }

    @Test
    public void testGetPayLoad() throws Exception {
        p.setPayLoad(PayLoad);
        assertTrue(p.getPayLoad().equals(PayLoad));
    }

    @Ignore
    @Test
    public void testToString() throws Exception {
        String actual = this.toString();
        String expected = "";
        assertTrue(actual.equals(expected));
    }
}