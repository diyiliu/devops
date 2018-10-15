package com.diyiliu.server.netty;

import com.diyiliu.codec.MsgDecoder;
import com.diyiliu.codec.MsgEncoder;
import com.diyiliu.server.support.IMsgObserver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: MonitorServer
 * Author: DIYILIU
 * Update: 2018-10-08 17:18
 */

@Slf4j
public class MonitorServer extends Thread{
    private int port;

    private IMsgObserver observer;

    public void init() {

        this.start();
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new MsgEncoder())
                                    .addLast(new MsgDecoder())
                                    .addLast(new ServerHandler(observer));
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            log.info("监控服务启动, 端口[{}]...", port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setObserver(IMsgObserver observer) {
        this.observer = observer;
    }
}
