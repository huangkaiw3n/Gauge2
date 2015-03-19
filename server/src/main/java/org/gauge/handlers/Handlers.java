package org.gauge.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.gauge.middleware.BodyParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by joel on 3/19/15.
 */
public class Handlers {

  public static class Ping_GET implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {
      String response = "Welcome to my page!";
      Headers h = httpExchange.getResponseHeaders();
      h.set("Content-Type", "text/html; charset=iso-8859-1");
      httpExchange.sendResponseHeaders(200, response.length());
      OutputStream os = httpExchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  private static JSONObject createMockJsonResponse() {

    JSONObject response = null;
    try {
      response = new JSONObject(
              "    {\n" +
              "        \"user\": \"jhtong\",\n" +
              "        \"ip\": \"127.0.0.1\"\n" +
              "    }\n"
              );
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return response;
  }


  public static class ping_GET implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {
      String response = "Welcome to my page!";
      Headers h = httpExchange.getResponseHeaders();
      h.set("Content-Type", "text/html; charset=iso-8859-1");
      httpExchange.sendResponseHeaders(200, response.length());
      OutputStream os = httpExchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }


  public static class User_GET extends GetHandler {
    public void handle(HttpExchange t) throws IOException {
      JSONObject response = createMockJsonResponse();
      JSONObject params;
      try {
        params = getParams(t);
        System.out.println(params.toString());
      } catch (JSONException e) {
        e.printStackTrace();
      } finally {
        sendJSONResponse(t, 200, response);
      }
    }
  }


  public static class User_POST extends PostHandler {
    public void handle(HttpExchange httpExchange) throws IOException {
      String query;
      JSONObject formData;
      JSONArray response;

      query = getPOSTBody(httpExchange.getRequestBody());
      try {
        formData = BodyParser.parseJSONObject(query);
        System.out.println(formData.toString());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        sendJSONResponse(httpExchange, 200, new JSONObject("{" +
                "\"status\":\"done\"" +
                "}"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }


  public static class Auth_GET implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {

    }
  }

  public static class AUTH_POST implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {

    }
  }
}
