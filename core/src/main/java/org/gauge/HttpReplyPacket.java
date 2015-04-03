package org.gauge;

import javax.swing.text.AbstractDocument;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by AdminNUS on 2/4/2015.
 */
public class HttpReplyPacket {
    private String HttpVersion = null;
    private String Status = null;
    private String Date = null;
    private String Content_Type = null;
    private String Content_Length = null;
    private String PayLoad = null;

    public HttpReplyPacket (String status, String Content_Type, String Content_Length) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm;ss");
        Calendar cal = Calendar.getInstance();
        this.Date = dateFormat.format(cal.getTime());
        this.HttpVersion = "HTTP/1.1 ";
        this.Status = status;
        this.Content_Type = Content_Type;
        this.Content_Length = Content_Length;

    }

    public void setHttpVersion() {
        this.HttpVersion = "HTTP/1.1 ";
    }

    public void setStatus() {
        this.Status = "200 OK";
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

    public String getHttpVersion() {
        return this.HttpVersion;
    }

    public String getStatus() {
        return this.Status;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HttpVersion + Status + "\n");
        sb.append(Date + "\n");
        sb.append(Content_Type + "\n" + Content_Length + "\n");
        sb.append(PayLoad + "\n\n");
        return sb.toString();
    }

}
