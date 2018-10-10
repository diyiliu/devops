package com.diyiliu.client.netty;

import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.model.ProcessInfo;
import com.diyiliu.plugin.util.CommonUtil;
import com.diyiliu.plugin.util.JacksonUtil;
import com.diyiliu.plugin.util.SpringUtil;
import com.diyiliu.util.OsMonitor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.List;

/**
 * Description: ClientHandler
 * Author: DIYILIU
 * Update: 2018-10-09 09:04
 */

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器发生错误, [{}]", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //String key = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");

        // 心跳处理
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == event.state()) {
                //log.warn("读超时 ... [{}]", key);

            } else if (IdleState.WRITER_IDLE == event.state()) {
                log.info("发送系统监控信息 ...");

                // 本机 IP
                String ip = InetAddress.getLocalHost().getHostAddress();
                // 本机 操作系统
                String osName = System.getProperty("os.name");
                int os = osName.toLowerCase().indexOf("windows") > -1 ? 1 : 0;
                // 系统监控信息
                byte[] monitorBytes = monitorInfo();
                int length = monitorBytes.length;

                ByteBuf buf = Unpooled.buffer(8 + length);
                buf.writeBytes(CommonUtil.ipToBytes(ip));
                buf.writeByte(os);
                buf.writeByte(0x01);
                buf.writeShort(length);
                buf.writeBytes(monitorBytes);
                ctx.writeAndFlush(buf);
            } else if (IdleState.ALL_IDLE == event.state()) {

                //log.warn("读/写超时...");
            }
        }
    }

    private byte[] monitorInfo() {
        OsMonitor monitor = SpringUtil.getBean("monitor");
        MonitorInfo monitorInfo = monitor.osHealth();

        //log.info("发送: " + JacksonUtil.toJson(monitorInfo));

        int cpuUsage = new BigDecimal(monitorInfo.getCpuLoad() * 100).intValue();
        int memUsage = new BigDecimal(monitorInfo.getMemUsage() * 100).intValue();
        int totalMemory = monitorInfo.getTotalMemory();
        List<ProcessInfo> processInfoList = monitorInfo.getProcessInfos();
        List<DiskInfo> diskInfoList = monitorInfo.getDiskInfos();

        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(cpuUsage);
        buf.writeByte(memUsage);
        buf.writeInt(totalMemory);

        // 进程
        buf.writeByte(processInfoList.size());
        for (ProcessInfo processInfo : processInfoList) {
            String name = processInfo.getName();
            byte[] nameArray = name.getBytes();
            int usage = new BigDecimal(processInfo.getMemUsage() * 100).intValue();

            buf.writeShort(nameArray.length);
            buf.writeBytes(nameArray);
            buf.writeShort(processInfo.getPid());
            buf.writeByte(usage);
        }

        // 磁盘
        buf.writeByte(diskInfoList.size());
        for (DiskInfo diskInfo : diskInfoList) {
            String name = diskInfo.getName();
            byte[] nameArray = name.getBytes();
            int usage = new BigDecimal(diskInfo.getDiskUsage() * 100).intValue();
            int totalSpace = diskInfo.getTotalSpace();

            buf.writeShort(nameArray.length);
            buf.writeBytes(nameArray);
            buf.writeByte(usage);
            buf.writeInt(totalSpace);
        }

        int index = buf.writerIndex();
        byte[] bytes = new byte[index + 1];
        buf.getBytes(0, bytes);

        return bytes;
    }
}
