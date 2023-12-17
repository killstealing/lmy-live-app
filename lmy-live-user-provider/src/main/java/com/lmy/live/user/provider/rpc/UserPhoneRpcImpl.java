package com.lmy.live.user.provider.rpc;

import com.lmy.live.user.dto.UserLoginDTO;
import com.lmy.live.user.dto.UserPhoneDTO;
import com.lmy.live.user.interfaces.IUserPhoneRpc;
import com.lmy.live.user.provider.service.IUserPhoneService;
import jakarta.annotation.Resource;

import java.util.List;

public class UserPhoneRpcImpl implements IUserPhoneRpc {

    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }
}
