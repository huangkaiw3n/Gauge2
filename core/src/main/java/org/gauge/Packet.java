package org.gauge;

import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by joel on 3/18/15.
 */
public class Packet {

  private String header;
  private String payload;

  private String destId;    // only used for group chat

  public Packet() {
    this.header = "";
    this.payload = "";
    this.destId = "";
  }

  public Packet(String header, String payload) {
    this.header = header;
    this.payload = payload;
    this.destId = "";
  }

  public Packet(String header, String payload, String destId) {
    this.header = header;
    this.payload = payload;
    this.destId = destId;
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
    byte[] bDestId = destId.getBytes();
    byte[] bPayload = payload.getBytes();
    int size = bHeader.length + bPayload.length + bDestId.length + 4 + 4 + 4;

    ByteBuffer buffer = ByteBuffer.allocate(size);
    buffer.putInt(bHeader.length).putInt(bDestId.length).putInt(bPayload.length);
    buffer.put(bHeader).put(bDestId).put(bPayload);

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

  public String getDestId() {
    return destId;
  }

  public void setDestId(String destId) {
    this.destId = destId;
  }

  private void fromBytes(byte[] buffer) {
    int headerLen, destIdLen, payloadLen;
    byte[] bHeader, bDestId, bPayload;
    ByteBuffer bb = ByteBuffer.wrap(buffer);

    headerLen = bb.getInt();
    destIdLen = bb.getInt();
    payloadLen = bb.getInt();

    bHeader = new byte[headerLen];
    bDestId = new byte[destIdLen];
    bPayload = new byte[payloadLen];

    bb.get(bHeader, bb.arrayOffset(), headerLen);
    bb.get(bDestId, bb.arrayOffset(), destIdLen);
    bb.get(bPayload, bb.arrayOffset(), payloadLen);

    try {
      header = new String(bHeader, "UTF-8");
      destId = new String(bDestId, "UTF-8");
      payload = new String(bPayload, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }


  public boolean equals(Packet b) {
    boolean headerEq, destIdEq, payloadEq;
    headerEq = header.equals(b.getHeader());
    destIdEq = destId.equals(b.getDestId());
    payloadEq = payload.equals(b.getPayload());
    return headerEq && payloadEq;
  }

  @Override
  public String toString() {
    return "<<PACKET>> header=" + header + "destId=" + destId + " payload=" + payload;
  }
}
