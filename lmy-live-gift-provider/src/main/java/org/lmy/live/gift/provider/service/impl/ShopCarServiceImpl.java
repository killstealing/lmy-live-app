package org.lmy.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.gift.interfaces.dto.ShopCarReqDTO;
import org.lmy.live.gift.interfaces.dto.ShopCarRespDTO;
import org.lmy.live.gift.provider.service.IShopCarService;
import org.lmy.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author idea
 * @Date: Created in 16:27 2023/10/4
 * @Description
 */
@Service
public class ShopCarServiceImpl implements IShopCarService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ISkuInfoService skuInfoService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(),shopCarReqDTO.getRoomId());
        Cursor<Map.Entry<Object,Object>> allCarData  = redisTemplate.opsForHash().scan(cacheKey, ScanOptions.scanOptions().match("*").build());
        while (allCarData.hasNext()){
            Map.Entry<Object,Object> entry = allCarData.next();
        }
        return null;
    }

    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(),shopCarReqDTO.getRoomId());
        //一个用户 多个商品
        //读取所有商品的数据
        //每个商品都有数量（目前的业务场景中，没有体现）
        // string （对象，对象里面关联上商品的数据信息）
        // set / list
        // map （k,v） key是skuId，value是商品的数量
        redisTemplate.opsForHash().put(cacheKey,shopCarReqDTO.getSkuId(),1);
        return true;
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(),shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().delete(cacheKey,shopCarReqDTO.getSkuId());
        return true;
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(),shopCarReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }



    @Override
    public Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO) {
        //可以去自行测试下
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(),shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().increment(cacheKey, shopCarReqDTO.getSkuId(),1);
        return true;
    }
}
