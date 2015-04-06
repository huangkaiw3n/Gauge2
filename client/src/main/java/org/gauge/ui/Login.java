package org.gauge.ui;

import org.gauge.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by AdminNUS on 6/4/2015.
 */
public class Login {
    private JTextField UserName;
    private JPanel panel1;
    private JPasswordField Password;
    private JTextField ServerName;
    private JButton loginButton;
    private JTextPane textPane1;

    public Login() {
        JFrame frame = new JFrame("Login");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginButton.addActionListener(new ActionListener() {
            char[] pwd;
            String user;

            public void actionPerformed(ActionEvent e) {
                boolean
                        user = UserName.getText();
                pwd = Password.getPassword();
                User user1 = new User();
                user1.setUsername(user);
                user1.setPassword(pwd.toString());
                if () {
                    textPane1.setText(user1.toString());
                }
                else{
                    textPane1.setText("Success!");
                }


            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
