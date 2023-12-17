package com.lmy.live.user.provider.service;

import com.lmy.live.user.dto.UserLoginDTO;
import com.lmy.live.user.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneService {
    /**
     * 手机号登录
     *
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);

    /**
     * 更具用户 id 查询手机信息
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);

    /**
     * 根据手机号查询
     *
     * @param phone
     * @return
     */
    UserPhoneDTO queryByPhone(String phone);
}
