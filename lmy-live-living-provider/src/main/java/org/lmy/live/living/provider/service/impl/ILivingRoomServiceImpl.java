package org.lmy.live.living.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.dto.PageWrapper;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.provider.dao.mapper.LivingRoomMapper;
import org.lmy.live.living.provider.dao.mapper.LivingRoomRecordMapper;
import org.lmy.live.living.provider.dao.po.LivingRoomPO;
import org.lmy.live.living.provider.dao.po.LivingRoomRecordPO;
import org.lmy.live.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ILivingRoomServiceImpl implements ILivingRoomService {
    private static final Logger logger= LoggerFactory.getLogger(ILivingRoomServiceImpl.class);

    @Resource
    private LivingRoomMapper livingRoomMapper;

    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStartTime(new Date());
        livingRoomPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomMapper.insert(livingRoomPO);
        Integer id = livingRoomPO.getId();
        logger.info("[ILivingRoomServiceImpl] id is {}",id);
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
        return true;
    }

    @Override
    public LivingRoomReqDTO queryByRoomId(Integer roomId) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(LivingRoomPO::getId, roomId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper), LivingRoomReqDTO.class);
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(LivingRoomPO::getType,livingRoomReqDTO.getType());
        queryWrapper.eq(LivingRoomPO::getStatus,CommonStatusEum.VALID_STATUS.getCode());
        Page<LivingRoomPO> pageResult=livingRoomMapper.selectPage(new Page<>(livingRoomReqDTO.getPage(),livingRoomReqDTO.getPageSize()),queryWrapper);
        PageWrapper<LivingRoomRespDTO> pageWrapper=new PageWrapper<>();
        pageWrapper.setList(ConvertBeanUtils.convertList(pageResult.getRecords(),LivingRoomRespDTO.class));
        pageWrapper.setHasNext(livingRoomReqDTO.getPage()*livingRoomReqDTO.getPageSize()<pageResult.getTotal());
        return pageWrapper;
    }
}
