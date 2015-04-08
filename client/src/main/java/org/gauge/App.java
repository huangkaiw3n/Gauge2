package org.gauge;

import org.apache.log4j.Logger;
import org.gauge.ui.Login;
import org.gauge.ui.MainView;

/**
 * Hello world!
 */
public class App {
    public static Client client;
    static final Logger log = Logger.getLogger(Misc.class);

    public App() {

    }

    public static void exampleUseCoreUtility() {
        Misc.printOut("Saying hello using core dependency!");

    }


    public static void main(final String[] args) {

        //client = new Client("127.0.0.1", 9000, 9060);
        //client.start();

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
