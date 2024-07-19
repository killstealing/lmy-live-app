package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.HomePageVO;

public interface IHomePageService {
    /**
     * 初始化页面获取的信息
     *
     * @param userId
     * @return
     */
    HomePageVO initPage(Long userId);
}
