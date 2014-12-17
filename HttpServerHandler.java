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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.traffic.TrafficCounter;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private Long counter;
    private Date date;
    private TrafficCounter trafficCounter;
    private Statistics statistics;
    private FullHttpResponse response;
    private String trafficURL;

    public HttpServerHandler(TrafficCounter trafficCounter, Statistics statistics) {
        this.trafficCounter = trafficCounter;
        this.statistics = statistics;
    }
    
    public HttpServerHandler(Long counter, Date date) {
        this.counter = counter;
        this.date = date;
    }
    
    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        statistics.addCurrentConnection();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        statistics.removeCurrentConnection();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            statistics.addUniqueRequest(getIP(ctx));
            statistics.addNewRequestByIP(getIP(ctx));
            statistics.addToAllRequests();

            HttpRequest req = (HttpRequest) msg;
            trafficURL = req.getUri();

            String uri;
            if (req.getUri().startsWith("/redirect?url=")){
                uri = "/redirect";
            }else {
                uri = req.getUri();
            }

            switch (uri){
                case "/hello":
                    helloResponse(ctx, req);
                    break;
                case "/status":
                    statusResponse(ctx, req);
                    break;
                case "/redirect":
                    StringBuilder url = new StringBuilder(new QueryStringDecoder(req.getUri()).parameters().get("url").get(0));
                    if (!url.toString().startsWith("http://")){
                        url.insert(0,"http://");
                    }
                    statistics.addRedirects(url.toString());
                    redirectResponse(ctx, url.toString());
                    break;
                default:
                    defResponse(ctx, req);
                    break;
            }
        }else {
            ctx.flush();
        }
        setTraffic(ctx);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void defResponse(ChannelHandlerContext ctx, HttpRequest request){

        String STR = "<center><big><big>Что-то пошло не так!!!</big></big></center>";
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(STR.getBytes()));
        response.headers().set(CONTENT_TYPE, "text/html");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        sendResponse(ctx, request, response);
    }
    private void helloResponse(ChannelHandlerContext ctx, HttpRequest request){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("Hello World!".getBytes()));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        sendResponse(ctx, request, response);
        System.out.println();
    }

    private void statusResponse(ChannelHandlerContext ctx, HttpRequest request){
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(statistics.toString().getBytes()));
        response.headers().set(CONTENT_TYPE, "text/html");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        sendResponse(ctx, request, response);
    }

    private void redirectResponse(ChannelHandlerContext ctx, String url){
        response = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY);
        response.headers().set(LOCATION, url);
        ctx.write(response);
        setTraffic(ctx);
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpRequest request, HttpResponse httpResponse) {
        if (!isKeepAlive(request)) {
            ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(httpResponse);
        }
    }

    private String getIP(ChannelHandlerContext ctx) {
        String ip = ctx.channel().remoteAddress().toString();
        return ip.substring(1, ip.lastIndexOf(":"));
    }

    private void setTraffic(ChannelHandlerContext ctx){
        trafficCounter.stop();
        statistics.addTraffic(getIP(ctx), trafficURL, new Date(), trafficCounter.cumulativeWrittenBytes(), trafficCounter.cumulativeReadBytes(), trafficCounter.lastWriteThroughput());
        trafficCounter.resetCumulativeTime();
    }

}
