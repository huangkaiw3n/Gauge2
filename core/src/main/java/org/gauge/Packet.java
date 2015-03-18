package org.gauge;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by joel on 3/18/15.
 */
public class Packet {

  private String header;
  private String payload;

  public Packet() {
    this.header = "";
    this.payload = "";
  }

  public Packet(String header, String payload) {
    this.header = header;
    this.payload = payload;
  }

  /**
   * Constructor to construct a packet from bytes
   *
   * @param buffer Buffer to construct from
   */
  public Packet(byte[] buffer) {
    super();
    fromBytes(buffer);
  }


  /**
   * Converts to packet to bytes of the form
   * [header.length][payload.length][header][payload]
   *
   * @return the byte dump.
   */
  public byte[] toBytes() {
    byte[] bHeader = header.getBytes();
    byte[] bPayload = payload.getBytes();
    int size = bHeader.length + bPayload.length + 4 + 4;

    ByteBuffer buffer = ByteBuffer.allocate(size);
    buffer.putInt(bHeader.length).putInt(bPayload.length);
    buffer.put(bHeader).put(bPayload);

    return buffer.array();
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  private void fromBytes(byte[] buffer) {
    int headerLen, payloadLen;
    byte[] bHeader, bPayload;
    ByteBuffer bb = ByteBuffer.wrap(buffer);

    headerLen = bb.getInt();
    payloadLen = bb.getInt();

    bHeader = new byte[headerLen];
    bPayload = new byte[payloadLen];

    bb.get(bHeader, bb.arrayOffset(), headerLen);
    bb.get(bPayload, bb.arrayOffset(), payloadLen);

    try {
      header = new String(bHeader, "UTF-8");
      payload = new String(bPayload, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }


  public boolean equals(Packet b) {
    boolean headerEq, payloadEq;
    headerEq = header.equals(b.getHeader());
    payloadEq = payload.equals(b.getPayload());
    return headerEq && payloadEq;
  }

  @Override
  public String toString() {
    return "header=" + header + " payload=" + payload;
  }
}
