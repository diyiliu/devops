package com.diyiliu.client.netty;

import com.diyiliu.codec.MsgDecoder;
import com.diyiliu.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: MonitorClient
 * Author: DIYILIU
 * Update: 2018-10-09 09:04
 */

@Slf4j
public class MonitorClient extends Thread {
    private String host;
    private int port;

    public void init() {

        this.start();
    }

    @Override
    public void run() {
        connectServer(host, port);
    }

    public void connectServer(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(0, 10, 0))
                                .addLast(new MsgEncoder())
                                .addLast(new MsgDecoder())
                                .addLast(new ClientHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("客户端连接成功 ...");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("连接异常！{}", e.getMessage());
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("客户端, 尝试重连 ...");
            connectServer(host, port);
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
