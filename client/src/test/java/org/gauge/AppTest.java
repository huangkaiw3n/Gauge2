package org.gauge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

  static final Logger log = Logger.getLogger(Misc.class);

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public AppTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(AppTest.class);
  }

  /**
   * Rigourous Test :-)
   */
  public void testApp() {
    log.info("Ran client test");
    assertTrue(true);
  }

  public void setUp() throws Exception {
    super.setUp();

  }

  public void tearDown() throws Exception {

  }

  public void testMain() throws Exception {

  }

  public void testExampleUseCoreUtility() throws Exception {
    App.exampleUseCoreUtility();
  }
}
