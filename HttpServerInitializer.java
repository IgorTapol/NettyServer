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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;


public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private TrafficCounter trafficCounter;
    private Statistics statistics = new Statistics();
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(ch.eventLoop());
        trafficCounter = globalTrafficShapingHandler.trafficCounter();
        trafficCounter.start();

        p.addLast(globalTrafficShapingHandler);
        p.addLast("codec", new HttpServerCodec());
        p.addLast("handler", new HttpServerHandler(trafficCounter, statistics));
    }
}
