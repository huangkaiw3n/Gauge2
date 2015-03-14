package org.gauge;

import org.gauge.ui.MainView;

/**
 * Hello world!
 */
public class App {

  public App() {

  }

  public static void exampleUseCoreUtility() {
    Misc.printOut("Saying hello using core dependency!");

  }

  public static void main(String[] args) {
    exampleUseCoreUtility();
    MainView.runForm(args);
    System.out.println("Hello World!");
  }
}
