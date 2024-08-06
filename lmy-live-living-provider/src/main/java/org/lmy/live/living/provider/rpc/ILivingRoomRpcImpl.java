package org.lmy.live.living.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.living.interfaces.dto.LivingPkRespDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.living.provider.service.ILivingRoomService;

import java.util.List;

@DubboService
public class ILivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService iLivingRoomService;

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.list(livingRoomReqDTO);
    }

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.closeLivingRoom(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        return iLivingRoomService.queryByRoomId(roomId);
    }

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.queryUserIdByRoomId(livingRoomReqDTO);
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.onlinePk(livingRoomReqDTO);
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        return iLivingRoomService.queryOnlinePkUserId(roomId);
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return iLivingRoomService.offlinePk(livingRoomReqDTO);
    }

}
