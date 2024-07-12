package org.lmy.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.msg.dto.MessageDTO;
import org.lmy.live.msg.enums.ImMsgBizCodeEnum;
import org.lmy.live.msg.provider.consumer.handler.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {

    @Resource
    private ImRouterRpc routerRpc;

    @Override
    public void onReceive(ImMsgBodyDTO imMsgBodyDTO) {
        int bizCode = imMsgBodyDTO.getBizCode();
        if(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()==bizCode){
            //直播间im消息
            MessageDTO messageDTO = JSON.parseObject(imMsgBodyDTO.getData(), MessageDTO.class);
            //暂时不做过多的处理
            ImMsgBodyDTO respMsg=new ImMsgBodyDTO();
            respMsg.setUserId(messageDTO.getObjectId());
            respMsg.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
            respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
            respMsg.setData("hello  test1!!!!!!!");
            routerRpc.sendMsg(respMsg);
        }
    }
}
