package org.idea.lmy.live.api.service.impl;

import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.error.ApiErrorEnum;
import org.idea.lmy.live.api.service.ILivingRoomService;
import org.idea.lmy.live.api.vo.LivingRoomInitVO;
import org.idea.lmy.live.api.vo.req.LivingRoomReqVO;
import org.idea.lmy.live.api.vo.req.OnlinePkReqVO;
import org.idea.lmy.live.api.vo.resp.RedPacketReceiveVO;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigRespDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;
import org.lmy.live.gift.interfaces.rpc.IRedPacketConfigRpc;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.living.interfaces.dto.LivingPkRespDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.lmy.live.web.starter.error.LmyErrorException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ILivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private ILivingRoomRpc iLivingRoomRpc;

    @DubboReference
    private IUserRpc iUserRpc;

    @DubboReference
    private IRedPacketConfigRpc redPacketConfigRpc;

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
        LivingRoomRespDTO respDTO = iLivingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO,ApiErrorEnum.LIVING_ROOM_END);
        Map<Long,UserDTO> userDTOMap = iUserRpc.batchQueryUserInfo(Arrays.asList(respDTO.getAnchorId(),userId).stream().distinct().collect(Collectors.toList()));
        UserDTO anchor = userDTOMap.get(respDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        //给定一个默认的头像
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar())?"https://q7.itc.cn/q_70/images03/20240430/79ff85fc980544e7aa538f24b11fb2da.jpeg":anchor.getAvatar());
        respVO.setWatcherAvatar(watcher.getAvatar());
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            //这种就是属于直播间已经不存在的情况了
            respVO.setRoomId(-1);
            return respVO;
        }
        boolean ifAnchor = respDTO.getAnchorId().equals(userId);
        respVO.setRoomId(respDTO.getId());
        respVO.setAnchorId(respDTO.getAnchorId());
        respVO.setAnchor(ifAnchor);
        respVO.setDefaultBgImg("https://c-https://q7.itc.cn/q_70/images03/20240430/79ff85fc980544e7aa538f24b11fb2da.jpeg");
        if(ifAnchor){
            RedPacketConfigRespDTO redPacketConfigRespDTO = redPacketConfigRpc.queryByAnchorId(userId);
            if(redPacketConfigRespDTO!=null){
                respVO.setRedPacketConfigCode(redPacketConfigRespDTO.getConfigCode());
            }
        }
        return respVO;
    }

    @Override
    public boolean onlinePk(OnlinePkReqVO onlinePkReqVO) {
        LivingRoomReqDTO livingRoomReqDTO = ConvertBeanUtils.convert(onlinePkReqVO, LivingRoomReqDTO.class);
        livingRoomReqDTO.setPkObjId(LmyRequestContext.getUserId());
        livingRoomReqDTO.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
        LivingPkRespDTO tryOnlineStatus = iLivingRoomRpc.onlinePk(livingRoomReqDTO);
        ErrorAssert.isTure(tryOnlineStatus.isOnlineStatus(), new LmyErrorException(-1,tryOnlineStatus.getMsg()));
        return true;
    }

    @Override
    public Boolean prepareRedPacket(Long userId, Integer roomId) {
        LivingRoomRespDTO respDTO = iLivingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(respDTO.getAnchorId().equals(userId), BizBaseErrorEnum.PARAM_ERROR);
        return redPacketConfigRpc.prepareRedPacket(userId);
    }

    @Override
    public Boolean startRedPacket(Long userId, String code) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        LivingRoomRespDTO respDTO = iLivingRoomRpc.queryByAnchorId(userId);
        ErrorAssert.isNotNull(respDTO,BizBaseErrorEnum.PARAM_ERROR);
        reqDTO.setRoomId(respDTO.getId());
        return redPacketConfigRpc.startRedPacket(reqDTO);
    }

    @Override
    public RedPacketReceiveVO getRedPacket(Long userId, String code) {
        RedPacketConfigReqDTO reqDTO=new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        RedPacketReceiveDTO redPacketReceiveDTO = redPacketConfigRpc.receiveRedPacket(reqDTO);
        RedPacketReceiveVO redPacketReceiveVO=new RedPacketReceiveVO();
        if(redPacketReceiveDTO==null){
            redPacketReceiveVO.setMsg("红包已派发完毕");
        }else{
            redPacketReceiveVO.setPrice(redPacketReceiveDTO.getPrice());
            redPacketReceiveVO.setMsg(redPacketReceiveDTO.getNotifyMsg());
        }
        return redPacketReceiveVO;
    }
}
