package org.gauge.ui;

import com.intellij.uiDesigner.core.GridLayoutManager;
import org.gauge.App;
import org.gauge.ChatroomDB;
import org.gauge.User;
import org.gauge.UserStatusDB;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * Created by joel on 3/14/15.
 */
public class MainView extends JPanel {
    private JPanel panel1;
    private JTextField DisplayMessage;
    private JTextArea MessageByUser;

    private JList Rooms;
    private JList ActiveUsers;
    private JList ChatRoomJoined;

    private JButton leaveButton;
    private JButton Leave;
    private JButton Join;
    private JButton Create;
    private JButton Refresh;
    private JButton SendButton;

    private JFrame mainFrame;

    private JLabel headerLabel;
    private JLabel statusLabel;

    private String chatRoomId;

    private String [] users = {"anli","kaiwen","joel" };
    private String [] users2 = {"daniel","wy","lionel"};

    private DefaultListModel listModel = new DefaultListModel();

    public MainView(final User user1) {

        ActiveUsers.setListData(users);

        JFrame frame = new JFrame("MainView");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        Refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            //refreshes Rooms Chatrooms Joined and Users (Online)
                try {

                } catch (Exception e1) {
                    DisplayMessage.setText("No Users are currently online!");
                }
            }

        });

        Create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Creates a chatroom with user in it

               App.client.create("talk to me baby",user1);
            }
        });

        Leave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Leaves a chat room the user is in
                App.client.leave(chatRoomId);
            }
        });

        SendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Sends whatever message that is in MessageByUser
                String input = MessageByUser.getText();
                try{
                    App.client.message(chatRoomId, input);
                    MessageByUser.setText("");
                }catch(Exception e2){
                    DisplayMessage.setText("Please select user/chat room first");
                }


            }
        });

        Rooms.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                chatRoomId = Rooms.getSelectedValue().toString();
            }
        });

        ActiveUsers.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ActiveUsers.getSelectedValue();
            }
        });

        ChatRoomJoined.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                chatRoomId = Rooms.getSelectedValue().toString();

            }
        });
    }
}
