package org.lmy.live.bank.provider.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyTradePO;

@Mapper
public interface LmyCurrencyTradeMapper extends BaseMapper<LmyCurrencyTradePO> {

    @Insert("insert into ")
    void insertOne(@Param("userId") long userId, @Param("num") int num);
}
