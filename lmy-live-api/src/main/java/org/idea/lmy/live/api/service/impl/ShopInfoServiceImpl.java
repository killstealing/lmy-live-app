package org.idea.lmy.live.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IShopInfoService;
import org.idea.lmy.live.api.vo.PrepareOrderVO;
import org.idea.lmy.live.api.vo.req.ShopCarReqVO;
import org.idea.lmy.live.api.vo.req.SkuInfoReqVO;
import org.idea.lmy.live.api.vo.resp.ShopCarRespVO;
import org.idea.lmy.live.api.vo.resp.SkuDetailInfoVO;
import org.idea.lmy.live.api.vo.resp.SkuInfoVO;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.PrepareOrderReqDTO;
import org.lmy.live.gift.interfaces.dto.ShopCarReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuInfoDTO;
import org.lmy.live.gift.interfaces.dto.SkuPrepareOrderInfoDTO;
import org.lmy.live.gift.interfaces.rpc.IShopCarRPC;
import org.lmy.live.gift.interfaces.rpc.ISkuInfoRPC;
import org.lmy.live.gift.interfaces.rpc.ISkuOrderInfoRPC;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:15 2023/10/3
 * @Description
 */
@Service
public class ShopInfoServiceImpl implements IShopInfoService {

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ISkuInfoRPC skuInfoRPC;
    @DubboReference
    private IShopCarRPC shopCarRPC;

    @DubboReference
    private ISkuOrderInfoRPC skuOrderInfoRPC;

    @Override
    public List<SkuInfoVO> queryByRoomId(Integer roomId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        Long anchorId = livingRoomRespDTO.getAnchorId();
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRPC.queryByAnchorId(anchorId);
        ErrorAssert.isTure(CollectionUtils.isNotEmpty(skuInfoDTOS),BizBaseErrorEnum.PARAM_ERROR);
        return ConvertBeanUtils.convertList(skuInfoDTOS,SkuInfoVO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRPC.queryBySkuId(skuInfoReqVO.getSkuId()),SkuDetailInfoVO.class);
    }

    @Override
    public Boolean addCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(LmyRequestContext.getUserId());
        return shopCarRPC.addCar(shopCarReqDTO);
    }

    @Override
    public ShopCarRespVO getCarInfo(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(LmyRequestContext.getUserId());
        return ConvertBeanUtils.convert(shopCarRPC.getCarInfo(shopCarReqDTO),ShopCarRespVO.class);
    }

    @Override
    public Boolean removeFromCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(LmyRequestContext.getUserId());
        return shopCarRPC.removeFromCar(shopCarReqDTO);
    }

    @Override
    public Boolean clearShopCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(LmyRequestContext.getUserId());
        return shopCarRPC.clearShopCar(shopCarReqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO) {
        PrepareOrderReqDTO reqDTO = new PrepareOrderReqDTO();
        reqDTO.setUserId(LmyRequestContext.getUserId());
        reqDTO.setRoomId(prepareOrderVO.getRoomId());
        return skuOrderInfoRPC.prepareOrder(reqDTO);
    }

    @Override
    public boolean payNow(PrepareOrderVO prepareOrderVO) {

        return false;
    }
}
