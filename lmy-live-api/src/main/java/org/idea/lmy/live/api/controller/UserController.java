package org.idea.lmy.live.api.controller;

import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping(value = "/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        return userRpc.getByUserId(userId);
    }

    @PostMapping(value="updateUserInfo")
    public boolean updateUserInfo(@RequestBody UserDTO userDTO){
        return userRpc.updateUserInfo(userDTO);
    }

    @PostMapping("/insertUserInfo")
    public boolean insertUserInfo(@RequestBody UserDTO userDTO){
        return userRpc.insertUserInfo(userDTO);
    }

    @GetMapping(value = "/bacthGetUserInfo")
    public Map<Long, UserDTO> getUserInfo(String userId){
        return userRpc.batchQueryUserInfo(Arrays.stream(userId.split(" ")).map(Long::parseLong).toList());
    }

}
