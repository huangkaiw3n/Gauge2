package org.gauge.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joel on 3/19/15.
 */
public abstract class GetHandler extends BasicHandler {

  public GetHandler() {
    super();
  }

  protected JSONObject getParams(HttpExchange t) throws JSONException {
    String raw = t.getRequestURI().getQuery();
    JSONObject result = new JSONObject();
    for (String param : raw.split("&")) {
      String pair[] = param.split("=");
      if (pair.length == 2) {
        result.append(pair[0], (String) pair[1]);
      }
    }
    return result;
  }

}
