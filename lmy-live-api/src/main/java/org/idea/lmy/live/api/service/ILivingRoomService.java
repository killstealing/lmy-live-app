package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.LivingRoomInitVO;

public interface ILivingRoomService {

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
}
