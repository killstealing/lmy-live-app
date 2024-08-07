package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.LivingRoomInitVO;
import org.idea.lmy.live.api.vo.req.LivingRoomReqVO;
import org.idea.lmy.live.api.vo.req.OnlinePkReqVO;
import org.idea.lmy.live.api.vo.resp.RedPacketReceiveVO;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;

public interface ILivingRoomService {
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqVO livingRoomReqVO);

    /**
     * 开启直播间
     *
     * @param type
     */
    Integer startingLiving(Integer type);

    /**
     * 关闭直播间
     *
     * @param roomId
     * @return
     */
    boolean closeLiving(Integer roomId);

    /**
     * 根据用户id返回当前直播间相关信息
     *
     * @param userId
     * @param roomId
     * @return
     */
    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 用户在pk直播间中，连上线请求
     *
     * @param onlinePkReqVO
     * @return
     */
    boolean onlinePk(OnlinePkReqVO onlinePkReqVO);
    /**
     * 初始化红包数据
     *
     * @param userId
     */
    Boolean prepareRedPacket(Long userId,Integer roomId);

    /**
     * 开始红包雨
     *
     * @param userId
     * @param code
     */
    Boolean startRedPacket(Long userId,String code);

    /**
     * 领取红包
     *
     * @param userId
     * @param code
     */
    RedPacketReceiveVO getRedPacket(Long userId, String code);
}
