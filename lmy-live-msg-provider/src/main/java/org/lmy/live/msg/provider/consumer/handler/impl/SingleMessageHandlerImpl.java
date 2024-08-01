package org.lmy.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.msg.dto.MessageDTO;
import org.lmy.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.lmy.live.msg.provider.consumer.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {

    @DubboReference
    private ImRouterRpc routerRpc;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Override
    public void onReceive(ImMsgBodyDTO imMsgBodyDTO) {
        int bizCode = imMsgBodyDTO.getBizCode();
        //直播间聊天消息
        if(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()==bizCode){
            //一个人发送 n个人接收
            //根据roomId,appId去调用rpc方法，获取对应的直播间内的userId
            //创建一个list的imMsgBody对象.
            //直播间im消息
            MessageDTO messageDTO = JSON.parseObject(imMsgBodyDTO.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO livingRoomReqDTO=new LivingRoomReqDTO();
            livingRoomReqDTO.setRoomId(roomId);
            livingRoomReqDTO.setAppId(imMsgBodyDTO.getAppId());
            //自己不用发
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO).stream().filter(x->!x.equals(imMsgBodyDTO.getUserId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(userIdList)){
                return;
            }
            List<ImMsgBodyDTO> imMsgBodyDTOList=new ArrayList<>();
            userIdList.forEach(userId->{
                ImMsgBodyDTO respBody=new ImMsgBodyDTO();
                respBody.setUserId(userId);
                respBody.setAppId(imMsgBodyDTO.getAppId());
                respBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respBody.setData(JSON.toJSONString(messageDTO));
                imMsgBodyDTOList.add(respBody);
            });
            routerRpc.batchSendMsg(imMsgBodyDTOList);
        }
    }
}
