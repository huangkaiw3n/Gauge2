package org.gauge.ui;

import org.gauge.App;
import org.gauge.ChatroomDB;
import org.gauge.UserStatusDB;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private JList ChatRoomJoined;
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private UserStatusDB usdb [];
    private ChatroomDB cdb [];
    private ChatroomDB jr [];
    private String chatRoomId;
    public MainView() {

        Rooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ActiveUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ChatRoomJoined.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usdb[0] = App.client.getUserList();
        cdb[0] = App.client.getAllChatrooms();
        jr[0] = App.client.getActiveChatrooms();

        Rooms.setListData(cdb);
        ActiveUsers.setListData(usdb);
        ChatRoomJoined.setListData(jr);

        //DisplayMessage.setText(App.client.getInbox().poll().toString());
        JFrame frame = new JFrame("MainView");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);



        Refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //refreshes Rooms Chatrooms Joined and Users (Online)

            }

        });

        Create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Creates a chatroom with user in it

               // App.client.create("",);
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
                App.client.message(chatRoomId, input);
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
