package org.lmy.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.service.IRouterHandlerService;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.springframework.stereotype.Service;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {
    @Override
    public void onReceive(ImMsgBodyDTO imMsgBodyDTO) {
        Long userId = imMsgBodyDTO.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if(ctx!=null){
            ImMsg imMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
            ctx.writeAndFlush(imMsg);
        }
    }
}
