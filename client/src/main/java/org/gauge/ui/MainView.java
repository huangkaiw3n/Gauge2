package org.gauge.ui;

import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by joel on 3/14/15.
 */
public class MainView {
  private JPanel panel1;
    private JTextField DisplayMessage;
    private JTextArea MessageByUser;
    private JList Rooms;
    private JList ActiveUsers;
    private JButton leaveButton;
    private JButton Leave;
    private JButton Join;
    private JButton Create;
    private JButton Refresh;
    private JButton SendButton;
    private JList list1;
    private JFrame mainFrame;
  private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public MainView() {
        runForm();
    }

    public void runForm() {

    JFrame frame = new JFrame("MainView");
    frame.setContentPane(panel1);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.pack();
    frame.setVisible(true);

    }
}
