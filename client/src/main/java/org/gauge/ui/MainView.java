package org.gauge.ui;

import org.gauge.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

/**
 * Created by joel on 3/14/15.
 */
public class MainView extends JPanel {
    private JPanel panel1;

    private JTextArea DisplayMessage;

    private JTextField MessageByUser;

    private JList Rooms;
    private JList ActiveUsers;
    private JList ChatRoomJoined;

    private JButton leaveButton;
    private JButton JoinButton;
    private JButton Join;
    private JButton Create;
    private JButton Refresh;
    private JButton SendButton;
    private JButton logoutButton;

    private JFrame mainFrame;

    private JLabel headerLabel;
    private JLabel statusLabel;

    private String chatRoomId, selectedUser;

//    private String [] users = {"anli","kaiwen","joel" };
//    private String [] users2 = {"daniel","wy","lionel"};
    private Object [] activeUsers;
    private Object [] RoomsJoined;
    private Object [] RoomsAvailable;

    public MainView(final User user1) {

        try{
            App.client.loadUserlist();
            Thread.sleep(400);
            App.client.loadChatroomList();
            Thread.sleep(400);
        }catch(Exception e3){

        }
//        System.out.println("Number of Active Users: " + App.client.getUserList().users.size());
//        ActiveUsers.setListData(users);
        activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
        ActiveUsers.setListData(activeUsers);

        RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
        ChatRoomJoined.setListData(RoomsJoined);

        RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
        Rooms.setListData(RoomsAvailable);

        JFrame frame = new JFrame("MainView");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);



        Refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            //refreshes Rooms Chatrooms Joined and Users (Online)
                try{
                    App.client.loadUserlist();
                    Thread.sleep(400);
                    App.client.loadChatroomList();
                    Thread.sleep(400);
                }catch(Exception e3){

                }
                try {
                    activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
                    ActiveUsers.setListData(activeUsers);

                    RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
                    ChatRoomJoined.setListData(RoomsJoined);

                    RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
                    Rooms.setListData(RoomsAvailable);

//                    System.out.println("Refreshing... : "+ App.client.loadUserlist().users.size());
//                    ActiveUsers.setListData(users2);
                } catch (Exception e1) {
                    DisplayMessage.setText("No Users are currently online!");
                }
            }

        });

        Create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Creates a chatroom with user in it
                //App.client.join();
                User createWith = App.client.getUserList().users.get(selectedUser);
                App.client.create("Default", createWith);
            }
        });

        JoinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Leaves a chat room the user is in

            }
        });

        SendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Sends whatever message that is in MessageByUser
                StringBuilder textInDM = new StringBuilder();
                String input = MessageByUser.getText();
                try{
                    App.client.message(chatRoomId, input);
                    MessageByUser.setText("");
                    textInDM.append(DisplayMessage.getText() + "\n");
                    textInDM.append(user1.getUsername() + ": \n" + input + "\n");
                    DisplayMessage.setText(textInDM.toString());
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
                selectedUser = ActiveUsers.getSelectedValue().toString();
            }
        });

        ChatRoomJoined.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                StringBuilder sb = new StringBuilder();
                String payload, username, message;
                JSONObject jsonMessage, jsonUser;
                chatRoomId = ChatRoomJoined.getSelectedValue().toString();
                DisplayMessage.setText("");
                while(App.client.getInbox(chatRoomId).size() > 0){
                    payload = App.client.getInbox(chatRoomId).poll().getPayload();
                    message = username = null;
                    try{
                        jsonMessage = new JSONObject(payload);
                        jsonUser = new JSONObject(jsonMessage.get("user"));
                        message = jsonMessage.get("body").toString();
                        username = jsonUser.get("username").toString();
                        if(message != null && username != null) {
                            sb.append(username);
                            sb.append(":\r\n");
                            sb.append(message);
                            sb.append("\r\n\r\n");
                        }
                    }catch(JSONException e3){
                        e3.printStackTrace();
                    }

                }
                DisplayMessage.setText(sb.toString());
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //App.client.
            }
        });
        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    App.client.leave(chatRoomId);
                }catch(Exception e5){
                    DisplayMessage.setText("No chat room selected");
                }

            }
        });

        MessageByUser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    StringBuilder textInDM = new StringBuilder();
                    String input = MessageByUser.getText();
                    try{
                        App.client.message(chatRoomId, input);
                        MessageByUser.setText("");
                        textInDM.append(DisplayMessage.getText() + "\n");
                        textInDM.append(user1.getUsername() + ": \n" + input + "\n");
                        DisplayMessage.setText(textInDM.toString());
                    }catch(Exception e2){
                        DisplayMessage.setText("Please select user/chat room first");
                    }
                }
        });

    }
}
