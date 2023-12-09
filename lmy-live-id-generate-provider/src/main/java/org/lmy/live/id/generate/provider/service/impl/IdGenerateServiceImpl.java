package org.lmy.live.id.generate.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.id.generate.provider.bo.LocalSeqIdBO;
import org.lmy.live.id.generate.provider.bo.LocalUnSeqIdBO;
import org.lmy.live.id.generate.provider.dao.mapper.IdGenerateMapper;
import org.lmy.live.id.generate.provider.dao.po.IdGeneratePO;
import org.lmy.live.id.generate.provider.service.IdGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {
    private final static Map<Integer, LocalSeqIdBO> localSeqIdMap=new ConcurrentHashMap<>();
    private final static Map<Integer, LocalUnSeqIdBO> localUnSeqIdBOMap=new ConcurrentHashMap<>();

    private final static Map<Integer,Semaphore> semaphoreMap=new ConcurrentHashMap<>();
    private final static Logger logger= LoggerFactory.getLogger(IdGenerateServiceImpl.class);
    private final static ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            r -> {
                Thread thread=new Thread(r);
                thread.setName("id-generate-thread-"+ThreadLocalRandom.current().nextInt(1000));
                return thread;
            });
    private static final float UPDATE_RATE=0.75f;

    @Resource
    private IdGenerateMapper idGenerateMapper;

    @Override
    public Long getSeqId(Integer id) {
        if(id==null){
            logger.error("getSeqId error, id is {}",id);
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(id);
        if(localSeqIdBO==null){
            logger.error("[getSeqId] localSeqIdBO is null, id is {}",id);
            return null;
        }
        this.refreshLocalSeqId(localSeqIdBO);
        long andIncrement = localSeqIdBO.getCurrentValue().getAndIncrement();
        if(localSeqIdBO.getCurrentValue().get()>localSeqIdBO.getNextThreshold()){
            logger.error("[getSeqId] id is over limit, id is {}",id);
            return null;
        }
        return andIncrement;
    }

    @Override
    public Long getUnSeqId(Integer id) {
        if(id==null){
            logger.error("[getUnSeqId] error, id is {}",id);
            return null;
        }
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdBOMap.get(id);
        if(localUnSeqIdBO==null){
            logger.error("[getUnSeqId] localUnSeqIdBO is null, id is {}",id);
            return null;
        }
        this.refreshLocalUnSeqId(localUnSeqIdBO);
        return localUnSeqIdBO.getIdQueue().poll();
    }

    /**
     * 刷新本地有序id段
     */
    private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
        long step=localSeqIdBO.getNextThreshold()-localSeqIdBO.getCurrentStart();
        if(localSeqIdBO.getCurrentValue().get()-localSeqIdBO.getCurrentStart()>step*UPDATE_RATE){
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            boolean acquireStatus = semaphore.tryAcquire();
            if(acquireStatus){
                logger.info("开始尝试进行本地有序id的同步操作");
                //异步进行同步id段操作
                threadPoolExecutor.execute(() -> {
                    try {
                        IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
                        tryUpdateMySqlRecord(idGeneratePO);
                    }catch (Exception e){
                        logger.error("[refreshLocalSeqId] has exception e: {}",e.getMessage());
                    }finally {
                        semaphore.release();
                        logger.info("本地有序id段的同步完成");
                    }
                });
            }
        }
    }

    private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO) {
        Long currentStart = localUnSeqIdBO.getCurrentStart();
        Long nextThreshold = localUnSeqIdBO.getNextThreshold();
        ConcurrentLinkedQueue<Long> idQueue = localUnSeqIdBO.getIdQueue();
        if((nextThreshold-currentStart)*(1-UPDATE_RATE)>idQueue.size()){
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBO.getId());
            boolean acquireStatus = semaphore.tryAcquire();
            if(acquireStatus){
                logger.info("开始尝试进行本地无序id的同步操作");
                //异步进行同步id段操作
                threadPoolExecutor.execute(() -> {
                    try {
                        IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
                        tryUpdateMySqlRecord(idGeneratePO);
                    }catch (Exception e){
                        logger.error("[refreshLocalUnSeqId] has exception e:{}",e.getMessage());
                    }finally {
                        semaphore.release();
                        logger.info("本地无序id段的同步完成");
                    }
                });
            }
        }
    }
    @Override
    public void afterPropertiesSet(){
        logger.info("开始初始化localSeqIdMap");
        List<IdGeneratePO> idGeneratePOS = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO:idGeneratePOS){
            tryUpdateMySqlRecord(idGeneratePO);
            semaphoreMap.put(idGeneratePO.getId(),new Semaphore(1));
        }
        logger.info("初始化localSeqIdMap完成");
    }
    private void tryUpdateMySqlRecord(IdGeneratePO idGeneratePO){
        int updateResult = idGenerateMapper.updateNewIdAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
        if(updateResult>0){
            localIdBOHandler(idGeneratePO);
            return;
        }
        for (int i = 0; i < 3; i++) {
            idGeneratePO = idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult = idGenerateMapper.updateNewIdAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
            if(updateResult>0){
                localIdBOHandler(idGeneratePO);
                return;
            }
        }
        throw new RuntimeException("表ID段占用失败，竞争过于激烈 id is :"+idGeneratePO.getId());
    }
    private void localIdBOHandler(IdGeneratePO idGeneratePO){
        long currentStart = idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        if(idGeneratePO.getIsSeq()==1){
            LocalSeqIdBO localSeqIdBO=new LocalSeqIdBO();
            AtomicLong atomicLong=new AtomicLong(currentStart);
            localSeqIdBO.setId(idGeneratePO.getId());
            localSeqIdBO.setCurrentValue(atomicLong);
            localSeqIdBO.setNextThreshold(nextThreshold);
            localSeqIdBO.setCurrentStart(currentStart);
            localSeqIdMap.put(idGeneratePO.getId(),localSeqIdBO);
        }else{
            LocalUnSeqIdBO localUnSeqIdBO=new LocalUnSeqIdBO();
            localUnSeqIdBO.setId(idGeneratePO.getId());
            localUnSeqIdBO.setNextThreshold(nextThreshold);
            localUnSeqIdBO.setCurrentStart(currentStart);
            List<Long> idList=new ArrayList<>();
            for (long i = currentStart; i < nextThreshold; i++) {
                idList.add(i);
            }
            Collections.shuffle(idList);
            ConcurrentLinkedQueue<Long> longConcurrentLinkedQueue = new ConcurrentLinkedQueue<>(idList);
            localUnSeqIdBO.setIdQueue(longConcurrentLinkedQueue);
            localUnSeqIdBOMap.put(idGeneratePO.getId(),localUnSeqIdBO);
        }
    }
}
