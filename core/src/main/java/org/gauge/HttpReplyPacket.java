package org.gauge;

import javax.swing.text.AbstractDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by AdminNUS on 2/4/2015.
 */
public class HttpReplyPacket {
    private String HttpVersion = "HTTP/1.1";
    private String Status = null;
    private String StatusNo = null;
    private String Date = null;
    private String Content_Type = null;
    private String Content_Length = null;
    private String PayLoad = null;

    public HttpReplyPacket (String status,String StatusNo, String Content_Type, String Content_Length) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm;ss");
        Calendar cal = Calendar.getInstance();
        this.Date = dateFormat.format(cal.getTime());
        this.HttpVersion = "HTTP/1.1";
        this.Status = status;
        this.StatusNo = StatusNo;
        this.Content_Type = Content_Type;
        this.Content_Length = Content_Length;

    }

    public void setHttpVersion() {
        this.HttpVersion = "HTTP/1.1";
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public void setStatusNo(String statusNo) {
        this.StatusNo = statusNo;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm;ss");
        Calendar cal = Calendar.getInstance();
        this.Date = dateFormat.format(cal.getTime());
    }

    public void setContent_Type(String ct) {
        this.Content_Type = ct;
    }

    public void setContent_Length(String cl) {
        this.Content_Length = cl;
    }

    public void setPayLoad(String payload) {
        this.PayLoad = payload;
    }

    public String getHttpVersion() {
        return this.HttpVersion;
    }

    public String getStatus() {
        return this.Status;
    }

    public String getStatusNo() {
        return this.StatusNo;
    }

    public String getDate() {
        return this.Date;
    }

    public String getContent_Type() {
        return this.Content_Type;
    }

    public String getContent_Length() {
        return this.Content_Length;
    }

    public String getPayLoad() {
        return this.PayLoad;
    }

    public HttpReplyPacket toPacket(InputStreamReader isr){
        HttpReplyPacket packet = null;
        BufferedReader br = new BufferedReader(isr);
        try{
            String input = br.readLine();
            StringTokenizer st = new StringTokenizer(input);
            st.nextToken();
            packet.setHttpVersion();
            packet.setStatus(st.nextToken());
            packet.setStatusNo(st.nextToken());
            input = br.readLine();
            input = input.substring(6);
            packet.setDate(input);
            input = br.readLine();
            input = input.substring(14);
            packet.setContent_Type(input);
            input = br.readLine();
            input = input.substring(16);
            packet.setContent_Length(input);
            if(Integer.getInteger(packet.getContent_Length()) > 0){
                packet.setPayLoad("");
            }
            else
                return packet;
        }catch(IOException e){
            return null;
        }

        return packet;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HttpVersion +" " + Status + " " + StatusNo + "\r\n");
        sb.append("Date: " + Date + "\r\n");
        sb.append("Content_Type: " + Content_Type + "\r\n" + "Content_Length: " + Content_Length + "\r\n");
        sb.append(PayLoad + "\r\n \r\n");
        return sb.toString();
    }

}
