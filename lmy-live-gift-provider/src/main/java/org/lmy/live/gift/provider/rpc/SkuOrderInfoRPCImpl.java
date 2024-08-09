package org.lmy.live.gift.provider.rpc;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.constants.SkuOrderInfoEnum;
import org.lmy.live.gift.interfaces.dto.*;
import org.lmy.live.gift.interfaces.rpc.ISkuOrderInfoRPC;
import org.lmy.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.lmy.live.gift.provider.service.IShopCarService;
import org.lmy.live.gift.provider.service.ISkuOrderInfoService;
import org.lmy.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author idea
 * @Date: Created in 07:11 2023/10/16
 * @Description
 */
@DubboService
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private MQProducer mqProducer;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId,roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.insertOne(skuOrderInfoReqDTO)!=null;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
    }

    @Override
    public boolean prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(prepareOrderReqDTO, ShopCarReqDTO.class);
        ShopCarRespDTO shopCarRespDTO = shopCarService.getCarInfo(shopCarReqDTO);
        List<ShopCarItemRespDTO> carItemList = shopCarRespDTO.getShopCarItemRespDTOS();
        if (CollectionUtils.isEmpty(carItemList)) {
            return false;
        }
        List<Long> skuIdList = carItemList.stream().map(item -> item.getSkuInfoDTO().getSkuId()).collect(Collectors.toList());
        //核心的知识点 库存回滚
        //10个skuId 前5个扣减成功了，后边5个有问题
        boolean status = false;
        for (Long skuId : skuIdList) {
            //todo
            status = skuStockInfoService.decrStockNumBySkuIdV2(skuId, 1);
        }

        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setSkuIdList(skuIdList);
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.PREPARE_PAY.getCode());
        skuOrderInfoReqDTO.setRoomId(prepareOrderReqDTO.getRoomId());
        skuOrderInfoReqDTO.setUserId(prepareOrderReqDTO.getUserId());
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);
        //库存回滚的mq延迟消息发送
        stockRollBackHandler(skuOrderInfoPO.getUserId(),skuOrderInfoPO.getId());
        //订单超时的概念，21：00，21：30分订单会自动关闭，21：25分的时候会有订单提醒功能
        //1.定时任务 select * from t_sku_order_info where status=1 and create_time<'2023-10-16 23:00' ,指定好索引，数据量非常高，扫描表的sql会很耗时
        //2.redis的过期回调 key过期之后，会有一个回调通知，orderid:1001 ttl到期之后会回调到订阅方, redis key 存起来，回调并不是高可靠的，可能回丢失
        //3.rocketmq 延迟消息，时间轮去做的
        //4.将扣减库存的信息 利用rmq发送出去，在延迟回调处进行校验，
        shopCarService.removeFromCar(shopCarReqDTO);
        return true;
    }

    private void stockRollBackHandler(Long userId,Long orderId) {
        RollBackStockDTO rollBackStockDTO = new RollBackStockDTO();
        rollBackStockDTO.setUserId(userId);
        rollBackStockDTO.setOrderId(orderId);
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.ROLL_BACK_STOCK);
        message.setBody(JSON.toJSONBytes(rollBackStockDTO));
        //messageDelayLevel=1s 5s 10s(3) 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        message.setDelayTimeLevel(16);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
