package org.lmy.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.service.IMsgAckCheckService;
import org.lmy.live.im.core.server.service.IRouterHandlerService;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService iMsgAckCheckService;

    @Override
    public void onReceive(ImMsgBodyDTO imMsgBodyDTO) {
        if(this.sendMessageToClient(imMsgBodyDTO)){
            iMsgAckCheckService.recordMsgAck(imMsgBodyDTO,1);
            iMsgAckCheckService.sendDelayMsg(imMsgBodyDTO);
        }
    }

    @Override
    public boolean sendMessageToClient(ImMsgBodyDTO imMsgBodyDTO) {
        Long userId = imMsgBodyDTO.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if(ctx!=null){
            String msgId = UUID.randomUUID().toString();
            imMsgBodyDTO.setMsgId(msgId);
            ImMsg imMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
            ctx.writeAndFlush(imMsg);
            return true;
        }
        return false;
    }
}
