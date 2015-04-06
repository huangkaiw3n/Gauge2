package org.gauge;

import org.apache.log4j.Logger;
import org.gauge.ui.Login;
import org.gauge.ui.MainView;

/**
 * Hello world!
 */
public class App {
    private
  static final Logger log = Logger.getLogger(Misc.class);

  public App() {

  }

  public static void exampleUseCoreUtility() {
    Misc.printOut("Saying hello using core dependency!");

  }


  public static void main(final String[] args) {
    log.info("Starting client..");

    // GUI
      /*
    Runnable runnableGui = new Runnable() {
      public void run() {
        MainView.runForm(args);
      }
    };
    */
      Runnable runnableGui = new Runnable() {
          public void run() {
              Login lg = new Login();
          }
      };



    // Launch threads
    new Thread(runnableGui).start();

    // misc
    exampleUseCoreUtility();
  }
}
