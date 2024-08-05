package org.lmy.live.living.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.lmy.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.provider.dao.mapper.LivingRoomMapper;
import org.lmy.live.living.provider.dao.mapper.LivingRoomRecordMapper;
import org.lmy.live.living.provider.dao.po.LivingRoomPO;
import org.lmy.live.living.provider.dao.po.LivingRoomRecordPO;
import org.lmy.live.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ILivingRoomServiceImpl implements ILivingRoomService {
    private static final Logger logger= LoggerFactory.getLogger(ILivingRoomServiceImpl.class);
    @Resource
    private LivingRoomMapper livingRoomMapper;
    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder livingProviderCacheKeyBuilder;

    @Resource
    private ImRouterRpc imRouterRpc;

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStartTime(new Date());
        livingRoomPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomMapper.insert(livingRoomPO);
        Integer id = livingRoomPO.getId();
        logger.info("[ILivingRoomServiceImpl] id is {}",id);
        //防止之前有空值缓存，这里做移除操作
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomObj(livingRoomPO.getId());
        redisTemplate.delete(cacheKey);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean closeLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(livingRoomReqDTO.getRoomId());
        if(livingRoomPO==null){
            return false;
        }
        if(!livingRoomPO.getAnchorId().equals(livingRoomReqDTO.getAnchorId())){
            return false;
        }
        LivingRoomRecordPO livingRoomRecordPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordPO.setStatus(CommonStatusEum.INVALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        livingRoomMapper.deleteById(livingRoomReqDTO.getRoomId());
        //移除掉直播间cache
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomObj(livingRoomReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomObj(roomId);
        LivingRoomRespDTO queryResult = (LivingRoomRespDTO) redisTemplate.opsForValue().get(cacheKey);
        if(queryResult!=null){
            //空值缓存
            if(queryResult.getId()==null){
                return null;
            }
            return queryResult;
        }

        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(LivingRoomPO::getId, roomId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        queryResult= ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper), LivingRoomRespDTO.class);
        if(queryResult==null){
            //防止缓存击穿
            redisTemplate.opsForValue().set(cacheKey,new LivingRoomRespDTO(),1, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey,queryResult,30,TimeUnit.MINUTES);
        return queryResult;
    }

    @Override
    public List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(LivingRoomPO::getType,type);
        queryWrapper.eq(LivingRoomPO::getStatus,CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.orderByDesc(LivingRoomPO::getId);
        queryWrapper.last("limit 1000");
        List<LivingRoomPO> livingRoomPOS = livingRoomMapper.selectList(queryWrapper);
        List<LivingRoomRespDTO> livingRoomRespDTOS = ConvertBeanUtils.convertList(livingRoomPOS, LivingRoomRespDTO.class);
        return livingRoomRespDTOS;
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomList(livingRoomReqDTO.getType());
        int page = livingRoomReqDTO.getPage();
        int pageSize = livingRoomReqDTO.getPageSize();
        Long size = redisTemplate.opsForList().size(cacheKey);
        List<Object> resultList = redisTemplate.opsForList().range(cacheKey, (page - 1) * pageSize, (page * pageSize));
        PageWrapper<LivingRoomRespDTO> pageWrapper=new PageWrapper<>();
        if(CollectionUtils.isEmpty(resultList)){
            pageWrapper.setList(Collections.emptyList());
            pageWrapper.setHasNext(false);
        }else{
            pageWrapper.setList(ConvertBeanUtils.convertList(resultList,LivingRoomRespDTO.class));
            pageWrapper.setHasNext(page*pageSize<size);
        }
        return pageWrapper;
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        String cacheKey= livingProviderCacheKeyBuilder.buildLivingRoomUserSet(imOnlineDTO.getRoomId(), imOnlineDTO.getAppId());
        redisTemplate.opsForSet().add(cacheKey,imOnlineDTO.getUserId());
        redisTemplate.expire(cacheKey,12,TimeUnit.HOURS);
    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        String cacheKey= livingProviderCacheKeyBuilder.buildLivingRoomUserSet(imOfflineDTO.getRoomId(), imOfflineDTO.getAppId());
        redisTemplate.opsForSet().remove(cacheKey,imOfflineDTO.getUserId());
        //监听pk主播下线行为
        LivingRoomReqDTO roomReqDTO = new LivingRoomReqDTO();
        roomReqDTO.setRoomId(imOfflineDTO.getRoomId());
        roomReqDTO.setPkObjId(imOfflineDTO.getUserId());
        this.offlinePk(roomReqDTO);
    }

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        String cacheKey= livingProviderCacheKeyBuilder.buildLivingRoomUserSet(livingRoomReqDTO.getRoomId(), livingRoomReqDTO.getAppId());
        Cursor<Object> scan = redisTemplate.opsForSet().scan(cacheKey, ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList=new ArrayList<>();
        while (scan.hasNext()){
            Integer userId= (Integer) scan.next();
            userIdList.add(userId.longValue());
        }
        return userIdList;
    }

    @Override
    public boolean onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        String pkRoomUserCacheKey = livingProviderCacheKeyBuilder.buildPKRoomUserCacheKey(livingRoomReqDTO.getRoomId());
        Boolean tryOnline = redisTemplate.opsForValue().setIfAbsent(pkRoomUserCacheKey, livingRoomReqDTO.getPkObjId(), 30, TimeUnit.HOURS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pkObjId",livingRoomReqDTO.getPkObjId());
        List<Long> userIdList = this.queryUserIdByRoomId(livingRoomReqDTO);
        batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_ONLINE.getCode(),jsonObject);
        return tryOnline;
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        String pkRoomUserCacheKey = livingProviderCacheKeyBuilder.buildPKRoomUserCacheKey(roomId);
        Object userId = redisTemplate.opsForValue().get(pkRoomUserCacheKey);
        return userId!=null? (Long) userId :null;
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        String pkRoomUserCacheKey = livingProviderCacheKeyBuilder.buildPKRoomUserCacheKey(livingRoomReqDTO.getRoomId());
        redisTemplate.delete(pkRoomUserCacheKey);
        return true;
    }

    private void batchSendImMsg(List<Long> userIdList, int bizCode, JSONObject jsonObject) {
        List<ImMsgBodyDTO> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBodyDTO imMsgBody = new ImMsgBodyDTO();
            imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodies);
    }
}
