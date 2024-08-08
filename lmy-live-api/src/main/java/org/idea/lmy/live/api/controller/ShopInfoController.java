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


    //用户进入直播间，查看到商品列表
    //用户查看商品详情
    //用户把感兴趣的商品，加入待支付的购物车中（购物车的概念）-> 购物车的基本存储结构（按照直播间为维度去设计购物车）
    //直播间的购物车是独立的，不会存在数据跨直播间存在的情况
    //购物车的添加，移除
    //购物车的内容展示
    //购物车的清空

    /**
     * 往购物车添加商品
     */
    @PostMapping("/addCar")
    public WebResponseVO addCar(){

    }

    /**
     * 从购物车移除商品
     */
    @PostMapping("/removeFromCar")
    public WebResponseVO removeFromCar() {

    }

    /**
     * 查看购物车信息
     */
    @PostMapping("/getCarInfo")
    public WebResponseVO getCarInfo() {

    }

    /**
     * 清空购物车信息
     */
    @PostMapping("/clearCar")
    public WebResponseVO clearCar() {

    }


    //购物车以及塞满了，下边的逻辑是怎样的？
    //预下单，（手机产品100台，库存的预锁定操作）
    //如果下单成功（库存就正常扣减了）
    //如果到达一定时间限制没有下单（100台手机，100台库存锁定，不支付，支付倒计时， 库存回滚，订单状态会变成支付超时状态）


}
