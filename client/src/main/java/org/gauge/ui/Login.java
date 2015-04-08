package org.gauge.ui;

import org.gauge.App;
import org.gauge.Client;
import org.gauge.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

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
    boolean actionLogin = true;
    JFrame frame;

    public Login() {
        frame = new JFrame("Login");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


            loginButton.addActionListener(new ActionListener() {
                char[] pwd;
                String user;

                public void actionPerformed(ActionEvent e) {
                    user = UserName.getText();
                    pwd = Password.getPassword();
                    User user1 = new User();
                    user1.setUsername(user);
                    user1.setPassword(new String(pwd));

                    try{
                        App.client.login(user1);
                    }catch(Exception e1){

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e1) {

                    }
                    //actionLogin = false;
                    if(App.client.isLoggedIn()) {
                        frame.setVisible(false);
                        //frame.dispose();
                        MainView mv = new MainView(user1);

                    }
                    else
                        textPane1.setText("Either you entered wrong credentials or we are currently suffering from network congestion");

                }
            });
    }
}
