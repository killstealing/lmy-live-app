package org.lmy.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.lmy.live.gift.provider.dao.po.SkuStockInfoPO;

/**
 * @Author idea
 * @Date: Created in 19:58 2023/10/3
 * @Description
 */
@Mapper
public interface SkuStockInfoMapper extends BaseMapper<SkuStockInfoPO> {
    //事务场景，隔离级别是rr，不同的事务会话所看到的stock_num值相同的情况，
    // A事务 -》 stock_num=10 -10
    // B事务 -》 stock_num=10 -10
    // 加乐观锁 需要提前得知当前的version版本号，循环（3次～5次），从db读取出来version值，然后进行更新
    // 加悲观锁 for update 行锁, in share mode
    // 加分布式锁 应用层，使用redis加分布式锁
    @Update("update t_sku_stock_info set stock_num=stock_num-#{num} where sku_id=#{skuId} and stock_num-#{num}>0 and version=#{version}")
    int dcrStockNumBySkuId(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("version")Integer version);
}
