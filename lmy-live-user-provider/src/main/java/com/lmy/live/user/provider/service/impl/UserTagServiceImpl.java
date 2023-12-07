package com.lmy.live.user.provider.service.impl;

import com.lmy.live.user.constants.UserTagFieldNameConstants;
import com.lmy.live.user.constants.UserTagsEnum;
import com.lmy.live.user.provider.dao.mapper.IUserTagMapper;
import com.lmy.live.user.provider.dao.po.UserTagPO;
import com.lmy.live.user.provider.service.IUserTagService;
import com.lmy.live.user.provider.utils.TagInfoUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserTagServiceImpl implements IUserTagService {

    private static final Logger logger= LoggerFactory.getLogger(UserTagServiceImpl.class);

    @Resource
    private IUserTagMapper iUserTagMapper;

    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return iUserTagMapper.setTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) >0;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return iUserTagMapper.cancelTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag())>0;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        if(userId==null){
            logger.error("[containTag] userId is null");
            return false;
        }
        UserTagPO userTagPO = iUserTagMapper.selectById(userId);
        if(UserTagFieldNameConstants.TAG_INFO_01.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagPO.getTagInfo01(),userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_02.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagPO.getTagInfo02(),userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_03.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagPO.getTagInfo03(),userTagsEnum.getTag());
        }
        return false;
    }
}
