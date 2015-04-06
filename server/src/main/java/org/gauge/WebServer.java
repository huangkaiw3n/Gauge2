package org.gauge;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kaiwen on 3/4/2015.
 */
public class WebServer {

    static final Logger log = Logger.getLogger(WebServer.class);

    private volatile boolean isRunning;
    private volatile ServerSocket socket;
    private int port;

    public UserDB db;

    public WebServer(int port, String csvPath) {
        this.port = port;
        init(csvPath);
    }

    public WebServer(String csvPath) {
        this.port = 9000;
        init(csvPath);
    }

    private void init(String csvPath) {
        isRunning = false;
        db = new UserDB(csvPath);
    }


    public UserDB getDb() {
        return db;
    }


    private void pollConnection() {
        Socket s;
        InputStreamReader isr;
        try {
            s = socket.accept();
            log.info("New Connection from: " + socket.getInetAddress());
            isr = new InputStreamReader(s.getInputStream());

            HttpRequestPacket packet = HttpRequestPacket.toPacket(isr);
            log.info(packet.toString());
            process(s, packet);

            // close the socket when done; other sockets can now connect
            s.close();

        } catch (IOException e) {
            //TODO if socket is closed, socket.accept() should not be sent.  Fail silently.
//      e.printStackTrace();
            return;
        }
    }


    /**
     * Used to poll the socket for the packet.
     * <p/>
     * This adds another layer of abstraction over packet class,
     * number of bytes is receiived and bytes retrieved.  It is then
     * generated into a Packet instance.
     * <p/>
     * In this way, encryption can be added without altering
     * structure of packet.
     *
     * @param s
     * @return
     * @throws IOException
     */
//    private Packet getPacket(Socket s) throws IOException {
//        Packet result;
//        DataInputStream dis;
//        int length = 0;
//        byte[] buffer;
//
//        dis = new DataInputStream(s.getInputStream());
//        length = dis.readInt();
//        buffer = new byte[length];
//        dis.read(buffer, 0, length);
//        result = new Packet(buffer);
//
//        return result;
//    }
//
//
//    private void sendPacket(Socket s, Packet packet) throws IOException {
//        DataOutputStream dos;
//        byte[] buffer = packet.toBytes();
//        int length = buffer.length;
//
//        dos = new DataOutputStream(s.getOutputStream());
//        dos.writeInt(length);
//        dos.write(buffer);
//    }


    private void process(Socket s, HttpRequestPacket packet) throws IOException {
        String path = java.net.URLDecoder.decode(packet.getFilename(), "UTF-8");
        log.info("Filename: " + path);
        boolean status;
        // The next 2 lines create a output stream we can
        // write to.
        OutputStream os = s.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        if(path.toLowerCase().contains("register?") || path.toLowerCase().contains("login?")){
            Pattern pattern = Pattern.compile("=(.*?)&");
            Matcher matcher = pattern.matcher(path);
            User u1 = new User();
            ArrayList<String> data = new ArrayList<String>();
            while (matcher.find()) {
                data.add(matcher.group(1));
            }
            try {
                u1.setUsername(data.get(0));
                u1.setPassword(data.get(1));
                u1.setEmail(data.get(2));
            }catch (Exception e){

            }
            log.info(u1.toString());
            JSONObject resJson = new JSONObject();
            if(u1.getUsername() != null && u1.getPassword() != null) {
                if (path.toLowerCase().contains("register?"))
                    status = db.add(data.get(0), u1, false);
                else
                    status = db.authenticate(u1.getUsername(), u1.getPassword());
            }else status = false;
            try {
                if (status)
                    resJson.put("status", "success");
                else
                    resJson.put("status", "fail");
            } catch (JSONException e) {
            }
            dos.writeBytes(resJson.toString());
        } else {
            try {
                String filename = "assets/html" + path;
                // Open and read the file into buffer
                File f = new File(filename);

                if (f.canRead()) {
                    int size = (int) f.length();

                    //Create a File InputStream to read the File
                    FileInputStream fis = new FileInputStream(filename);
                    byte[] buffer = new byte[size];
                    fis.read(buffer);

                    // Now, write buffer to client
                    // (but, send HTTP response header first)

                    dos.writeBytes("HTTP/1.0 200 Okie \r\n");
                    dos.writeBytes("Content-type: text/html\r\n");
                    dos.writeBytes("\r\n");
                    dos.write(buffer, 0, size);
                } else {
                    // File cannot be read.  Reply with 404 error.
                    dos.writeBytes("HTTP/1.0 404 Not Found\r\n");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Cannot find " + filename + " leh");
                }
            } catch (Exception ex) {
            }
        }
    }


//    private JSONObject makeAuthRes(HttpRequestPacket packet) {
//        String reqString = packet.getFilename();
//        boolean status = authenticate(reqString);
//
//        JSONObject resJson = new JSONObject();
//        try {
//            if (status) {
//                resJson.put("status", "success");
//            } else {
//                resJson.put("status", "fail");
//            }
//        } catch (JSONException e) {
//        }
//        return resJson;
//    }
//
//
//    private boolean authenticate(String reqString) {
//        User userAuth = null;
//        try {
//            userAuth = new User(new JSONObject(reqString));
//        } catch (JSONException e) {
//            return false;
//        }
//
//        if (db.authenticate(userAuth.getUsername(), userAuth.getPassword())) {
//            return true;
//        }
//
//        return false;
//    }


    public WebServer start() {
        // exit and return if already running
        if (isRunning) {
            return this;
        }

        try {
            socket = new ServerSocket(port);
            Thread.sleep(1000); // give server time to start
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("Waiting for connection on " + port + ".");

        isRunning = true;

        Runnable daemon = new Runnable() {
            public void run() {
                while (isRunning) {
                    pollConnection();
                }
                log.info("Server stopped.");
            }
        };

        new Thread(daemon).start();
        log.info("Server started.");
        return this;
    }


    public WebServer stop() {
        isRunning = false;
        try {
            socket.close();
        } catch (IOException e) {
            // fail silently; does not seem important
        }
        return this;
    }
}
