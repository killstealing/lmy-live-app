package org.lmy.live.living.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.living.provider.service.ILivingRoomService;

@DubboService
public class ILivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService iLivingRoomService;

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.closeLivingRoom(livingRoomReqDTO);
    }

    @Override
    public LivingRoomReqDTO queryByRoomId(Integer roomId) {
        return iLivingRoomService.queryByRoomId(roomId);
    }
}
