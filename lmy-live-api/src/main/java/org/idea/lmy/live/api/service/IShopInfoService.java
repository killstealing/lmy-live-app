package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.req.ShopCarReqVO;
import org.idea.lmy.live.api.vo.req.SkuInfoReqVO;
import org.idea.lmy.live.api.vo.resp.SkuDetailInfoVO;
import org.idea.lmy.live.api.vo.resp.SkuInfoVO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:15 2023/10/3
 * @Description
 */
public interface IShopInfoService {

    /**
     * 根据直播间id查询商品信息
     *
     * @param roomId
     */
    List<SkuInfoVO> queryByRoomId(Integer roomId);

    /**
     * 查询商品详情
     *
     * @param skuInfoReqVO
     */
    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);

    /**
     * 添加商品到购物车
     *
     * @param shopCarReqVO
     */
    Boolean addCar(ShopCarReqVO shopCarReqVO);
}
