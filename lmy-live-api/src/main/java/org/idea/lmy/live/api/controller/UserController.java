package org.idea.lmy.live.api.controller;

import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping(value = "/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        UserDTO userDTO = userRpc.getByUserId(userId);
        logger.info("[getUserInfo] userDTO is {}",userDTO);
        return userDTO;
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
