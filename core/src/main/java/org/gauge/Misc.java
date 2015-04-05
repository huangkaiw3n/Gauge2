package org.gauge;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by joel on 3/14/15.
 *
 * This is a miscellaneous test class.
 *
 */
public class Misc {

  static final Logger log = Logger.getLogger(Misc.class);

  /**
   * Prints out a string.
   * @param arg
   */
  public static void printOut(String arg) {
    log.debug("Hello world");
    System.out.println(arg);
  }

  static InetAddress getInetAddress(String address) throws MalformedURLException, UnknownHostException {
    InetAddress result = null;
    try {
      result = InetAddress.getByName(address);
    } catch (UnknownHostException e) {
      result = InetAddress.getByName(new URL(address).getHost());
    }
    return result;
  }
}
