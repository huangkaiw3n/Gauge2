package org.gauge.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by joel on 3/19/15.
 */
public abstract class BasicHandler implements HttpHandler {

  public void sendJSONResponse(HttpExchange t, int opcode, JSONObject response) throws IOException {
    Headers headers = t.getResponseHeaders();
    headers.set("Content-Type", "application/json; charset=iso-8859-1");
    t.sendResponseHeaders(200, response.toString().length());
    OutputStream os = t.getResponseBody();
    os.write(response.toString().getBytes());
    os.close();
  }

}
