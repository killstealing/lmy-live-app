package org.idea.lmy.live.api.service.impl;

import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.ILivingRoomService;
import org.idea.lmy.live.api.vo.LivingRoomInitVO;
import org.idea.lmy.live.api.vo.req.LivingRoomReqVO;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.web.starter.LmyRequestContext;
import org.springframework.stereotype.Service;

@Service
public class ILivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private ILivingRoomRpc iLivingRoomRpc;

    @DubboReference
    private IUserRpc iUserRpc;

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqVO livingRoomReqVO) {
        LivingRoomReqDTO livingRoomReqDTO = ConvertBeanUtils.convert(livingRoomReqVO, LivingRoomReqDTO.class);
        return iLivingRoomRpc.list(livingRoomReqDTO);
    }

    @Override
    public Integer startingLiving(Integer type) {
        Long userId = LmyRequestContext.getUserId();
        UserDTO userDTO=iUserRpc.getByUserId(userId);
        LivingRoomReqDTO livingRoomReqDTO=new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(userId);
        livingRoomReqDTO.setRoomName("主播-"+userId+"的直播间");
        livingRoomReqDTO.setType(type);
        return iLivingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO=new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(LmyRequestContext.getUserId());
        return iLivingRoomRpc.closeLivingRoom(livingRoomReqDTO);
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        LivingRoomReqDTO respDTO = iLivingRoomRpc.queryByRoomId(roomId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            respVO.setAnchor(false);
        } else {
            respVO.setAnchor(respDTO.getAnchorId().equals(userId));
        }
        return respVO;
    }
}
