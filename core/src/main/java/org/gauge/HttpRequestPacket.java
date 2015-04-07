package org.gauge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by AdminNUS on 2/4/2015.
 */
public class HttpRequestPacket {
    private String Filename = null;
    private final String Request = "GET";
    private String HttpVersion = null;
    private String Host = null;

    public HttpRequestPacket() {

    }
    public HttpRequestPacket (String fn, String HttpVersion, String Host) {
        this.Filename = fn;
        this.HttpVersion = HttpVersion;
        this.Host = Host;
    }

    public void setFilename(String fn) {
        this.Filename = fn;
    }

    public void setHost(String Host){
        this.Host = Host;
    }

    public void setHttpVersion(String version) {
        this.HttpVersion = version;
    }

    public String getFilename (){
        return this.Filename;
    }

    public String getRequest () {
        return this.Request;
    }

    public String getHttpVersion(){
        return this.HttpVersion;
    }

    public String getHost() {
        return this.Host;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Request + " " + Filename + " " + HttpVersion + "\r\n");
        sb.append("Host: " + Host + "\r\n\n");
        return sb.toString();
    }

    public static HttpRequestPacket toPacket(InputStreamReader isr) {
        BufferedReader br = new BufferedReader(isr);
        HttpRequestPacket hrp = new HttpRequestPacket();
        try{
            String input = br.readLine();
            if(input == null)
                return null;
            StringTokenizer st = new StringTokenizer(input);
            if(st.nextToken().equals("GET")){
                String filename = st.nextToken();
                hrp.setFilename(filename);
                filename = st.nextToken();
                hrp.setHttpVersion(filename);
                input = br.readLine();
                hrp.setHost(input);
            }
        }catch(IOException e){
            return null;
        }

        return hrp;
    }

    public String processLogin(HttpRequestPacket packet) {
        String username = "username: ";
        String password = "password: ";
        return "";
    }
}
