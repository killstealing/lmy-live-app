package org.lmy.live.id.generate.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lmy.live.id.generate.provider.dao.po.IdGeneratePO;

import java.util.List;

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGeneratePO> {

    @Select("select * from t_id_generate_config")
    List<IdGeneratePO> selectAll();
}
