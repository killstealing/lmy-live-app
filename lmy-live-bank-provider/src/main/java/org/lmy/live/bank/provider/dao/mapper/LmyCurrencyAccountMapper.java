package org.lmy.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyAccountPO;

@Mapper
public interface LmyCurrencyAccountMapper extends BaseMapper<LmyCurrencyAccountPO> {

    @Update("update t_lmy_currency_account set current_balance=current_balance+#{num} where user_id=#{userId}")
    void incr(@Param("userId") long userId, @Param("num") int num);

    @Select("select current_balance from t_lmy_currency_account where user_id=#{userId}")
    int getBalance(@Param("userId") long userId);

    @Update("update t_lmy_currency_account set current_balance=current_balance-#{num} where user_id=#{userId}")
    void decr(@Param("userId") long userId, @Param("num") int num);
}
