/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.nettyweb;

/**
 *
 * @author Пользователь
 */

import java.util.Date;

public class TrafficCount {
    private String ip;
    private String uri;
    private Date date;
    private long sendBytes;
    private long receivedBytes;
    private long speed;

    public TrafficCount(String ip, String uri, Date date, long sendBytes, long receivedBytes, long speed) {
        this.ip = ip;
        this.uri = uri;
        this.date = date;
        this.sendBytes = sendBytes;
        this.receivedBytes = receivedBytes;
        this.speed = speed;
    }

    public TrafficCount() {
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSendBytes() {
        return sendBytes;
    }

    public void setSendBytes(long sendBytes) {
        this.sendBytes = sendBytes;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return ip + " " + uri + " " + date + " " + sendBytes + " " + receivedBytes + " " + speed;
    }
}
