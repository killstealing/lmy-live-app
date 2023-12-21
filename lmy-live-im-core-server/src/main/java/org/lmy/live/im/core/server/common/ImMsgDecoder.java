package org.lmy.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.lmy.live.im.interfaces.ImConstants;

import java.util.List;

public class ImMsgDecoder extends ByteToMessageDecoder {
    private static final int BASE_LEN = 2 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf byteBuf, List<Object> out) throws Exception {

        if (byteBuf.readableBytes() >= BASE_LEN) {
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC) {
                chc.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            if (byteBuf.readableBytes()<len){
                chc.close();
                return;
            }
            byte[] body=new byte[len];
            byteBuf.readBytes(body);
            //将bytebuff转成ImMsg
            ImMsg imMsg=new ImMsg();
            imMsg.setCode(code);
            imMsg.setLen(len);
            imMsg.setBody(body);
            out.add(imMsg);
        }
    }
}
