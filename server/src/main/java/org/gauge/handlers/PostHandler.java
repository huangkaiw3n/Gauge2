package org.gauge.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by joel on 3/19/15.
 */
public abstract class PostHandler extends BasicHandler {

  /**
   * Gets the post body from a stream
   *
   * @param in
   * @return
   * @throws IOException
   */
  public String getPOSTBody(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buff = new byte[4096];
    for (int n = in.read(buff); n > 0; n = in.read(buff)) {
      out.write(buff, 0, n);
    }
    return new String(out.toByteArray(), "UTF-8");
  }



}
