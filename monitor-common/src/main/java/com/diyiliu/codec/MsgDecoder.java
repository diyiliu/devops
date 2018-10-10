package com.diyiliu.codec;

import com.diyiliu.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Description: MsgDecoder
 * Author: DIYILIU
 * Update: 2018-10-08 17:15
 */

@Slf4j
public class MsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if (in.readableBytes() < 12) {

            return;
        }
        in.markReaderIndex();

        int header1 = in.readUnsignedByte();
        int header2 = in.readUnsignedByte();
        if (header1 != 0xBD || header1 != header2) {
            log.error("消息头异常, 关闭连接!");
            ctx.close();

            return;
        }

        // IP(4) + 操作系统(1) + 指令ID(1)
        in.readBytes(new byte[6]);
        // 消息长度
        int length = in.readUnsignedShort();
        if (in.readableBytes() < length + 2) {
            in.resetReaderIndex();

            return;
        }
        in.readBytes(new byte[length]);
        byte check = in.readByte();

        byte end = in.readByte();
        if (end != 0x0A) {
            log.error("数据结束位错误, 关闭连接!");
            ctx.close();

            return;
        }
        in.resetReaderIndex();

        byte[] bytes = new byte[2 + 8 + length];
        in.readBytes(bytes);

        byte checkNow = CommonUtil.getCheck(bytes);
        if (check != checkNow) {
            log.error("校验位错误, 关闭连接!");
            ctx.close();

            return;
        }
        // 校验位 + 结束位
        in.readShort();

        out.add(Unpooled.copiedBuffer(bytes, 2, 8 + length));
    }
}
