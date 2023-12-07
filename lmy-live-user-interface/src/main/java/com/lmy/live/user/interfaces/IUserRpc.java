package com.lmy.live.user.interfaces;

import com.lmy.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserRpc {
    UserDTO getByUserId(Long userId);

    boolean updateUserInfo(UserDTO userDTO);

    boolean insertUserInfo(UserDTO userDTO);

    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);

}
