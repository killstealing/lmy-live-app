package imclient;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger= LoggerFactory.getLogger(ClientHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg= (ImMsg) msg;
        ImMsgBodyDTO imMsgBodyDTO = JSON.parseObject(new String(imMsg.getBody()), ImMsgBodyDTO.class);
        if(imMsg.getCode()== ImMsgCodeEnum.IM_BIZ_MSG.getCode()){
            ImMsgBodyDTO ackMsgBody=new ImMsgBodyDTO();
            ackMsgBody.setAppId(imMsgBodyDTO.getAppId());
            ackMsgBody.setUserId(imMsgBodyDTO.getUserId());
            ackMsgBody.setMsgId(imMsgBodyDTO.getMsgId());
            ImMsg ackMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackMsgBody));
            ctx.writeAndFlush(ackMsg);
        }
        logger.info("【服务端相应数据】 imMsg is {} msgBody is {}",imMsg,new String(imMsg.getBody()));
    }
}
