package org.gauge;

import org.apache.log4j.Logger;

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

}
