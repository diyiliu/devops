package com.diyiliu.server.netty;

import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.model.ProcessInfo;
import com.diyiliu.plugin.util.CommonUtil;
import com.diyiliu.plugin.util.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: ServerHandler
 * Author: DIYILIU
 * Update: 2018-10-08 17:19
 */

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");

        log.info("[{}]建立连接...", host);
        // 断开连接
        ctx.channel().closeFuture().addListener(
                (ChannelFuture future) -> log.info("[{}]断开连接...", host)
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        byte[] ipBytes = new byte[4];
        buf.readBytes(ipBytes);
        String ip = CommonUtil.bytesToIp(ipBytes);
        int os = buf.readByte();

        int cmd = buf.readByte();
        int length = buf.readUnsignedShort();

        // 主机监控
        if (0x01 == cmd) {
            int cpuCore = buf.readByte();
            int cpuUsage = buf.readByte();
            int memUsage = buf.readByte();
            int totalMemory = buf.readInt();

            int processCount = buf.readByte();
            List<ProcessInfo> processInfoList = new ArrayList();
            for (int i = 0; i < processCount; i++) {
                int nameLength = buf.readShort();
                byte[] nameBytes = new byte[nameLength];
                buf.readBytes(nameBytes);

                String name = new String(nameBytes);
                int pid = buf.readUnsignedShort();
                int memPercent = buf.readByte();

                ProcessInfo processInfo = new ProcessInfo();
                processInfo.setName(name);
                processInfo.setPid(pid);
                processInfo.setMemUsage(memPercent / 100d);
                processInfoList.add(processInfo);
            }

            int diskCount = buf.readByte();
            List<DiskInfo> diskInfoList = new ArrayList();
            for (int i = 0; i < diskCount; i++) {
                int nameLength = buf.readShort();
                byte[] nameBytes = new byte[nameLength];
                buf.readBytes(nameBytes);

                String name = new String(nameBytes);
                int usage = buf.readByte();
                int totalSpace = buf.readInt();

                DiskInfo diskInfo = new DiskInfo();
                diskInfo.setName(name);
                diskInfo.setDiskUsage(usage / 100d);
                diskInfo.setTotalSpace(totalSpace);
                diskInfoList.add(diskInfo);
            }

            MonitorInfo monitorInfo = new MonitorInfo();
            monitorInfo.setOs(os == 1 ? "Windows" : "Linux");
            monitorInfo.setCpuCore(cpuCore);
            monitorInfo.setCpuLoad(cpuUsage / 100d);
            monitorInfo.setMemUsage(memUsage / 100d);
            monitorInfo.setTotalMemory(totalMemory);

            monitorInfo.setProcessInfos(processInfoList);
            monitorInfo.setDiskInfos(diskInfoList);

            log.info("接收 [{}]: {}", ip, JacksonUtil.toJson(monitorInfo));
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器发生错误, [{}]", cause.getMessage());
        ctx.close();
    }
}
