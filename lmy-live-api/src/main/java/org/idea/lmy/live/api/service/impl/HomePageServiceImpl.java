package org.idea.lmy.live.api.service.impl;

import com.lmy.live.user.constants.UserTagsEnum;
import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.interfaces.IUserRpc;
import com.lmy.live.user.interfaces.IUserTagRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IHomePageService;
import org.idea.lmy.live.api.vo.HomePageVO;
import org.springframework.stereotype.Service;

@Service
public class HomePageServiceImpl implements IHomePageService {

    @DubboReference
    private IUserRpc iUserRpc;

    @DubboReference
    private IUserTagRpc userTagRpc;

    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = iUserRpc.getByUserId(userId);
        HomePageVO homePageVO=new HomePageVO();
        if(userDTO!=null){
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userDTO.getUserId());
            homePageVO.setNickName(userDTO.getNickName());
            //vip 用户才有权利开播
            homePageVO.setShowStartLivingBtn(userTagRpc.containTag(userId, UserTagsEnum.IS_VIP));
        }
        return homePageVO;
    }
}
