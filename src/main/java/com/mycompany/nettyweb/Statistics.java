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

import java.util.*;

public class Statistics {
    private Long allRequests = 0L;
    private Long currentConnections = 0L;
    private Set<String> uniqueIPSet = Collections.synchronizedSet(new HashSet());
    private Map<String, HttpServerHandler> requestsByIP = Collections.synchronizedMap(new HashMap<String, HttpServerHandler>());
    private Map<String, Long> redirects = Collections.synchronizedMap(new HashMap<String, Long>());
    public List<TrafficCount> trafficList = Collections.synchronizedList(new ArrayList());

    public Statistics() {
    }

    public Long getAllRequests() {
        return allRequests;
    }

    public void setAllRequests(Long allRequests) {
        this.allRequests = allRequests;
    }

    public Long getCurrentConnections() {
        return currentConnections;
    }

    public void setCurrentConnections(Long currentConnections) {
        this.currentConnections = currentConnections;
    }

    public void setUniqueIPSet(Set<String> uniqueIPSet) {
        this.uniqueIPSet = uniqueIPSet;
    }

    public void setRequestsByIP(Map<String, HttpServerHandler> requestsByIP) {
        this.requestsByIP = requestsByIP;
    }

    public Map<String, Long> getRedirects() {
        return redirects;
    }

    public void setRedirects(Map<String, Long> redirects) {
        this.redirects = redirects;
    }

    public List<TrafficCount> getTrafficList() {
        return trafficList;
    }

    public void setTrafficList(List<TrafficCount> trafficList) {
        this.trafficList = trafficList;
    }

    public Set<String> getUniqueIPSet() {
        return uniqueIPSet;
    }


    public Map<String, HttpServerHandler> getRequestsByIP() {
        return requestsByIP;
    }

    public void addToAllRequests(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (allRequests){
                    allRequests++;
                }
            }
        };
        runnable.run();
    }

    public void addCurrentConnection(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (currentConnections){
                    currentConnections++;
                }
            }
        };
        runnable.run();
    }

    public void removeCurrentConnection(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (currentConnections){
                    currentConnections--;
                }
            }
        };
        runnable.run();
    }

    public void addUniqueRequest(String ip) {
        uniqueIPSet.add(ip);
    }

    public void addNewRequestByIP(final String ip) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (requestsByIP){
                    if (requestsByIP.containsKey(ip)){
                        requestsByIP.put(ip, new HttpServerHandler(requestsByIP.get(ip).getCounter() + 1L, new Date()));
                    }else {
                        requestsByIP.put(ip, new HttpServerHandler(1L, new Date()));
                    }
                }
            }
        };
        runnable.run();
    }

    public void addRedirects (final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (requestsByIP){
                    if (redirects.containsKey(url)){
                        redirects.put(url, redirects.get(url)+1L);
                    } else {
                        redirects.put(url, 1L);
                    }
                }
            }
        };
        runnable.run();
    }

    public void addTraffic(final String ip, final String uri, final Date date, final long sendBytes, final long receivedBytes, final long speed){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (trafficList){
                    trafficList.add(new TrafficCount(ip, uri, date, sendBytes, receivedBytes, speed));
                }
            }
        };
        runnable.run();
    }

    public String toString() {
        StringBuilder pageBuilder = new StringBuilder();
            pageBuilder.append("<center><big><big>Статистика</big></big></center>\n");

            pageBuilder.append("<b>Всего запросов:</b> ");
            pageBuilder.append(allRequests + "<br><br>");

            pageBuilder.append("<b>Уникальные запросы: </b>");
            pageBuilder.append(uniqueIPSet.size() + "<br><br>");

            pageBuilder.append("<b>Запросы: </b><br>\n");
            pageBuilder.append("<table width=\"35%\" border=\"2\" cellspacing=\"0\" cellpadding=\"4\" cols=\"3\">");
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"20%\"; style=\"font-size: 100%;color: #123456\"> <b> IP-адрес </b> </td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\"> <b> Запросы </b> </td>");
            pageBuilder.append("<td width=\"70%\"; style=\"font-size: 100%;color: #123456\"> <b> Дата </b> </td>");
            pageBuilder.append("</tr>");
        for (String ip: uniqueIPSet){
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"20%\"; style=\"font-size: 100%;color: #123456\">"+ ip +"</td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\">"+ requestsByIP.get(ip).getCounter() +"</td>");
            pageBuilder.append("<td width=\"70%\"; style=\"font-size: 100%;color: #123456\">"+ requestsByIP.get(ip).getDate() +"</td>");
            pageBuilder.append("</tr>");
        }
            pageBuilder.append("</table><br><br>");

            pageBuilder.append("<b>Переадресации: </b><br>\n");
            pageBuilder.append("<table width=\"35%\" border=\"2\" cellspacing=\"0\" cellpadding=\"4\" cols=\"2\">");
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"80%\"; style=\"font-size: 100%;color: #123456\"> <b> URL </b> </td>");
            pageBuilder.append("<td width=\"20%\"; style=\"font-size: 100%;color: #123456\"> <b> Количество </b> </td>");
            pageBuilder.append("</tr>");
        for (String url: redirects.keySet()){
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"80%\"; style=\"font-size: 100%;color: #123456\">" + url + "</td>");
            pageBuilder.append("<td width=\"20%\"; style=\"font-size: 100%;color: #123456\">" +  redirects.get(url) + "</td>");
            pageBuilder.append("</tr>");
        }
            pageBuilder.append("</table><br><br>");

            pageBuilder.append("<b>Текущие соединения:</b> ");
            pageBuilder.append(currentConnections + "<br><br> ");

            pageBuilder.append("<b>Таблица последних 16 подключений: </b><br>\n");
            pageBuilder.append("<table width=\"100%\" border=\"2\" cellspacing=\"0\" cellpadding=\"4\" cols=\"6\">");
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\"> <b> IP-адрес </b> </td>");
            pageBuilder.append("<td width=\"16%\"; style=\"font-size: 100%;color: #123456\"> <b> URL-адрес </b> </td>");
            pageBuilder.append("<td width=\"30%\"; style=\"font-size: 100%;color: #123456\"> <b> Дата </b> </td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\"> <b> Отправлено байт </b> </td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\"> <b> Принято байт </b> </td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\"> <b> Скорость б/сек </b> </td>");
            pageBuilder.append("</tr>");
        for (int i = trafficList.size()-1; i > trafficList.size()-17 && i > 0; i--){
            pageBuilder.append("<tr align=\"center\">");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getIP() +"</td>");
            pageBuilder.append("<td width=\"16%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getUri() +"</td>");
            pageBuilder.append("<td width=\"30%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getDate() +"</td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getSendBytes() +"</td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getReceivedBytes() +"</td>");
            pageBuilder.append("<td width=\"10%\"; style=\"font-size: 100%;color: #123456\">"+ trafficList.get(i).getSpeed() +"</td>");
            pageBuilder.append("</tr>");
        }
            pageBuilder.append("</table><br><br>");

        return pageBuilder.toString();
    }
}
