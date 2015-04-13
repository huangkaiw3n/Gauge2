package org.gauge.ui;

import org.gauge.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;
import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

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

    private volatile String chatRoomId, previousRoomId;
    private String selectedUser;
    private ConcurrentHashMap<String, String> roomMessages;

    private Object [] activeUsers;
    private Object [] RoomsJoined;
    private Object [] RoomsAvailable;

    public MainView(final User user1) {
//        previousRoomId = "";
//        previousRoomId.concat(chatRoomId);
        try{
            App.client.loadUserlist();
            Thread.sleep(200);
            App.client.loadChatroomList();
            Thread.sleep(200);

        }catch(Exception e3){
            e3.printStackTrace();
        }

        roomMessages = new ConcurrentHashMap<String, String>();

        activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
        ActiveUsers.setListData(activeUsers);

        RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
        ChatRoomJoined.setListData(RoomsJoined);

        RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
        Rooms.setListData(RoomsAvailable);

        DefaultCaret caret = (DefaultCaret)DisplayMessage.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JFrame frame = new JFrame("MainView");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        Runnable autoUpdate = new Runnable(){
            public void run(){
                try{
                    while(true){
                        Thread.sleep(4000);
                        App.client.loadUserlist();
                        App.client.loadChatroomList();
                        activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
                        ActiveUsers.setListData(activeUsers);

                        RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
                        ChatRoomJoined.setListData(RoomsJoined);

                        RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
                        Rooms.setListData(RoomsAvailable);
                    }
                }catch(InterruptedException e9){
                    e9.printStackTrace();
                }
            }
        };
        Runnable pollInbox = new Runnable() {
            public void run() {
                LinkedBlockingQueue<Packet> inbox = null;
                StringBuilder sb = new StringBuilder();
                String payload, username, message;
                JSONObject jsonMessage, jsonUser;
                while(true){
                    try{ Thread.sleep(700);}catch (InterruptedException e8){}
                    if(chatRoomId != null)
                        inbox = App.client.udpDaemon.recvQueue.get(chatRoomId);
                    if(inbox != null)
                        while(!inbox.isEmpty()) {
                            payload = inbox.poll().getPayload();
                            message = username = null;
                            try {
                                jsonMessage = new JSONObject(payload);
                                jsonUser = new JSONObject(jsonMessage.get("user").toString());
                                message = jsonMessage.get("body").toString();
                                username = jsonUser.get("username").toString();
                                if (message != null && username != null) {
                                    sb.append(username);
                                    sb.append(":\n");
                                    sb.append(message);
                                    sb.append("\n");
                                }
                            } catch (JSONException e3) {
                                e3.printStackTrace();
                            }
                            DisplayMessage.append(sb.toString() + "\n");
                            sb.setLength(0);
                        }
                }
            }
        };

        new Thread(pollInbox).start();
        new Thread(autoUpdate).start();

        MessageByUser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    StringBuilder textInDM = new StringBuilder();
                    String input = MessageByUser.getText();
                    try{
                        App.client.message(chatRoomId, input);
                        MessageByUser.setText("");
                        textInDM.append(user1.getUsername() + ": \n" + input + "\n\n");
                        DisplayMessage.append(textInDM.toString());
                    }catch(Exception e2){
                        DisplayMessage.setText("Please select user/chat room first");
                    }
                    textInDM.setLength(0);
                }
        });

        Rooms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                try {
                    chatRoomId = Rooms.getSelectedValue().toString();
                }catch(NullPointerException e2){
                    DisplayMessage.setText("Choose your peer!");
                }
            }
        });
        JoinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                //Leaves a chat room the user is in
                try{
                    App.client.join(chatRoomId);
                    App.client.loadChatroomList();
                    App.client.loadUserlist();
                    activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
                    ActiveUsers.setListData(activeUsers);

                    RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
                    ChatRoomJoined.setListData(RoomsJoined);

                    RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
                    Rooms.setListData(RoomsAvailable);

                }catch(Exception e6){
                    DisplayMessage.setText("You are already in the room!");
                }
            }
        });
        Create.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                //Creates a chatroom with user in it
                //App.client.join();
                User createWith = App.client.getUserList().users.get(selectedUser);
                App.client.create("Default", createWith);
            }
        });
//        Refresh.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
//                //refreshes Rooms Chatrooms Joined and Users (Online)
//                try{
//                    App.client.loadUserlist();
//                    Thread.sleep(400);
//                    App.client.loadChatroomList();
//                    Thread.sleep(400);
//                }catch(Exception e3){
//                    e3.printStackTrace();
//                }
//                try {
//                    activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
//                    ActiveUsers.setListData(activeUsers);
//
//                    RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
//                    ChatRoomJoined.setListData(RoomsJoined);
//
//                    RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
//                    Rooms.setListData(RoomsAvailable);
//                } catch (Exception e1) {
//                    DisplayMessage.setText("No Users are currently online!");
//                    e1.printStackTrace();
//                }
//            }
//        });
        leaveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                try{
                    App.client.leave(chatRoomId);
                    Thread.sleep(400);
                    App.client.loadChatroomList();
                    App.client.loadUserlist();
                    activeUsers = App.client.getUserList().users.keySet().toArray(new String[0]);
                    ActiveUsers.setListData(activeUsers);

                    RoomsJoined = App.client.getActiveChatrooms().chatrooms.keySet().toArray(new String[0]);
                    ChatRoomJoined.setListData(RoomsJoined);

                    RoomsAvailable = App.client.getAllChatrooms().chatrooms.keySet().toArray(new String [0]);
                    Rooms.setListData(RoomsAvailable);

                }catch(Exception e6){
                    DisplayMessage.setText("No chat room selected");
                }
            }
        });
        ChatRoomJoined.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                try{
                    chatRoomId = ChatRoomJoined.getSelectedValue().toString();
                }catch(NullPointerException e7){
                    DisplayMessage.append(chatRoomId + " is unavailable\n");
                }
            }
        });
        ActiveUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                try {
                    selectedUser = ActiveUsers.getSelectedValue().toString();
                }catch(Exception e6){

                }
            }
        });

        MessageByUser.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                MessageByUser.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                MessageByUser.setText("Enter a message");
            }
        });
    }
}