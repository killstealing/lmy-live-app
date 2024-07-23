package org.lmy.live.living.interfaces.rpc;

import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;

public interface ILivingRoomRpc {

    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 开启直播间
     * @param livingRoomReqDTO
     * @return
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 关闭直播间
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据用户id查询是否正在开播
     *
     * @param roomId
     * @return
     */
    LivingRoomReqDTO queryByRoomId(Integer roomId);

}
