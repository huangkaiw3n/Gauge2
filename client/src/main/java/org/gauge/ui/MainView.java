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

    public MainView(){
        prepareGUI();
    }
    public static void main() {
        MainView mv = new MainView();
        mv.runForm();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("EE3031 Messenger");
        mainFrame.setSize(400, 400);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);

        statusLabel.setSize(200,100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private static ImageIcon createImageIcon (String path, String description) {
        java.net.URL imgURL = MainView.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public void runForm() {
      /*
    JFrame frame = new JFrame("MainView");
    frame.setContentPane(new MainView().panel1);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.pack();
    frame.setVisible(true);
    */
        headerLabel.setText("EE3031 Messenger");

        ImageIcon icon = createImageIcon("/resources/java_icon.png", "Java");

        JButton okButton = new JButton("OK");
        JButton SubmitButton = new JButton("Submit", icon);
        JButton CancelButton = new JButton("Cancel", icon);
        CancelButton.setHorizontalTextPosition(SwingConstants.WEST);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Ok Button Clicked!");
            }
        });

        SubmitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Submit Button Clicked!");
            }
        });

        CancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Cancel Button Clicked!");
            }
        });

        controlPanel.add(okButton);
        controlPanel.add(SubmitButton);
        controlPanel.add(CancelButton);

        mainFrame.setVisible(true);
  }
}
