package org.idea.lmy.live.api.controller;


import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.IGiftService;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gift")
public class GiftController {

    @Resource
    private IGiftService giftService;

    /**
     * 获取礼物列表
     * @return
     */
    @PostMapping("/listGift")
    public WebResponseVO listGift(){
        return WebResponseVO.success(giftService.listGift());
    }

    /**
     * 发送礼物方法
     * 具体实现在后边的章节会深入讲解
     *
     * @return
     */
    @PostMapping("/send")
    public WebResponseVO send() {
        return WebResponseVO.success();
    }

}
