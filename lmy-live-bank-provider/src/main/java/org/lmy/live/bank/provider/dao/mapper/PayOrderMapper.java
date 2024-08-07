package org.lmy.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lmy.live.bank.provider.dao.po.PayOrderPO;

@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrderPO> {
}
