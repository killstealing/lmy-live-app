package org.lmy.live.id.generate.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.id.generate.provider.bo.LocalSeqIdBO;
import org.lmy.live.id.generate.provider.dao.mapper.IdGenerateMapper;
import org.lmy.live.id.generate.provider.dao.po.IdGeneratePO;
import org.lmy.live.id.generate.provider.service.IdGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {
    private final static Map<Long, LocalSeqIdBO> localSeqIdMap=new ConcurrentHashMap<>();
    private Logger logger= LoggerFactory.getLogger(IdGenerateServiceImpl.class);

    @Resource
    private IdGenerateMapper idGenerateMapper;

    @Override
    public Long getSeqId(Integer id) {
        if(id==null){
            logger.error("getSeqId error, id is {}",id);
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(id);
        long andIncrement = localSeqIdBO.getCurrentValue().getAndIncrement();
        return andIncrement;
    }

    @Override
    public Long getUnSeqId(Integer code) {
        return null;
    }

    @Override
    public void afterPropertiesSet(){
        List<IdGeneratePO> idGeneratePOS = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO:idGeneratePOS){
            LocalSeqIdBO localSeqIdBO=new LocalSeqIdBO();
            localSeqIdBO.setId(idGeneratePO.getId());
            localSeqIdBO.setCurrentStart(idGeneratePO.getCurrentStart());
            localSeqIdMap.put(idGeneratePO.getCurrentStart(),localSeqIdBO);
        }
    }
}
