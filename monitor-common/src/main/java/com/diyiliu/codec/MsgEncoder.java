package com.diyiliu.codec;

import com.diyiliu.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: MsgEncoder
 * Author: DIYILIU
 * Update: 2018-10-08 17:16
 */

@Slf4j
public class MsgEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        ByteBuf buf = (ByteBuf) msg;
        int length = buf.readableBytes();
        if (length < 8){

            return;
        }

        // 消息内容
        byte[] content = buf.array();

        byte[] bytes = new byte[length + 2];
        bytes[0] = (byte) 0xBD;
        bytes[1] = (byte) 0xBD;
        System.arraycopy(content, 0, bytes, 2, length);
        // 校验位
        byte check = CommonUtil.getCheck(bytes);

        out.writeBytes(bytes);
        out.writeByte(check);
        // 结束位
        out.writeByte(0x0A);
    }
}
