package org.gauge;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import org.gauge.handlers.Handlers;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by joel on 3/19/15.
 */
public class SimpleHttpServer {

  static final Logger log = Logger.getLogger(SimpleHttpServer.class);

  private HttpServer server;
  private int port;

  public SimpleHttpServer(int port) {
    this.port = port;
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.setExecutor(null);
      attachHandlers();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void attachHandlers() {
    server.createContext("/test", new Handlers.Ping_GET());

    /** API **/
    server.createContext("/user/get", new Handlers.User_GET());
    server.createContext("/user/post", new Handlers.User_POST());
    server.createContext("/auth/get", new Handlers.Auth_GET());
    server.createContext("/auth/post", new Handlers.AUTH_POST());
  }


  public SimpleHttpServer start() {
    server.start();
    log.debug("===Gauge ChatServer Started!===");
    return this;
  }


  public SimpleHttpServer stop() {
    server.stop(0);
    log.debug("===Gauge ChatServer Stopped!===");
    return this;
  }

  public static void main(String[] args) {
    SimpleHttpServer s = new SimpleHttpServer(9000);
    s.start();
  }

}
