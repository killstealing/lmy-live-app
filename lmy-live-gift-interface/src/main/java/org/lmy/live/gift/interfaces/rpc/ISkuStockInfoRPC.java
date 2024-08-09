package org.lmy.live.gift.interfaces.rpc;

/**
 * @Author idea
 * @Date: Created in 09:11 2023/10/5
 * @Description
 */
public interface ISkuStockInfoRPC {

    /**
     * 根据skuId更新库存值
     * @param skuId
     * @param num
     */
    boolean dcrStockNumBySkuId(Long skuId,Integer num);

    //库存值从mysql预热加载到redis中

    /**
     * 预热库存信息
     *
     * @param anchorId
     */
    boolean prepareStockInfo(Long anchorId);

    //提供基础的缓存查询接口

    /**
     * 基础的缓存查询接口
     * @param skuId
     */
    Integer queryStockNum(Long skuId);
    //设计一个接口用于同步redis值到mysql中（定时任务执行，本地定时任务去完成同步行为）

    /**
     * 同步库存数据到MySQL
     * @param anchorId
     */
    boolean syncStockNumToMySQL(Long anchorId);

    //库存扣减要设计lua脚本

    //库存扣减成功后，生成待支付订单（MQ）
}
