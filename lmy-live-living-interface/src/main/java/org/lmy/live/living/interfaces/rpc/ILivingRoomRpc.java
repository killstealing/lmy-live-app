package org.lmy.live.living.interfaces.rpc;

import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;

public interface ILivingRoomRpc {

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
