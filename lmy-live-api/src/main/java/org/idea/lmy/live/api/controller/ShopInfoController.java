package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.IShopInfoService;
import org.idea.lmy.live.api.vo.req.SkuInfoReqVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author idea
 * @Date: Created in 20:14 2023/10/3
 * @Description 带货相关控制器
 */
@RestController
@RequestMapping("/shop")
public class ShopInfoController {

    @Resource
    private IShopInfoService shopInfoService;

    @PostMapping("/listSkuInfo")
    public WebResponseVO listSkuInfo(Integer roomId) {
        return WebResponseVO.success(shopInfoService.queryByRoomId(roomId));
    }

    @PostMapping("/detail")
    public WebResponseVO detail(SkuInfoReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.detail(reqVO));
    }

}
