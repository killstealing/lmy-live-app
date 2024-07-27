package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.resp.GiftConfigVO;

import java.util.List;

public interface IGiftService {

    /**
     * 展示礼物列表
     *
     * @return
     */
    List<GiftConfigVO> listGift();
}
