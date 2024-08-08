package org.lmy.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;

@Mapper
public interface RedPacketConfigMapper extends BaseMapper<RedPacketConfigPO> {
    @Update("update t_red_packet_config set total_get_price=total_get_price+#{price} where config_code=#{code} and total_get_price>#{price}")
    void incrTotalGetPrice(@Param("code") String code, @Param("price") Integer price);

    @Update("update t_red_packet_config set total_get=total_get+1 where config_code=#{code} ")
    void incrTotalGet(@Param("code") String code);
}
