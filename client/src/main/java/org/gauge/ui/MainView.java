package org.gauge.ui;

import javax.swing.*;

/**
 * Created by joel on 3/14/15.
 */
public class MainView {
  private JPanel panel1;
  private JTextArea screenInput;
  private JEditorPane screenMain;
  private JScrollPane pane1;
  private JSplitPane pane0;

  public static void runForm(String[] args) {
    JFrame frame = new JFrame("MainView");
    frame.setContentPane(new MainView().panel1);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
