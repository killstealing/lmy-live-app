package org.idea.lmy.live.api.service.impl;

import com.lmy.live.user.dto.UserLoginDTO;
import com.lmy.live.user.interfaces.IUserPhoneRpc;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IUserLoginService;
import org.idea.lmy.live.api.vo.UserLoginVO;
import org.lmy.live.account.interfaces.IAccountTokenRPC;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.msg.dto.MsgCheckDTO;
import org.lmy.live.msg.enums.MsgSendResultEnum;
import org.lmy.live.msg.interfaces.ISmsRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserLoginServiceImpl implements IUserLoginService {
    private static final Logger logger= LoggerFactory.getLogger(UserLoginServiceImpl.class);
    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    @DubboReference
    private ISmsRpc smsRpc;

    @DubboReference
    private IUserPhoneRpc userPhoneRpc;

    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        if(StringUtils.isEmpty(phone)){
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if(!Pattern.matches(PHONE_REG,phone)){
            return WebResponseVO.errorParam("手机号格式异常");
        }
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendMessage(phone);
        if(msgSendResultEnum==MsgSendResultEnum.SEND_SUCCESS){
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        if(StringUtils.isEmpty(phone)){
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if(!Pattern.matches(PHONE_REG,phone)){
            return WebResponseVO.errorParam("手机号格式异常");
        }
        if(code==null||code<1000){
            return WebResponseVO.errorParam("验证码格式异常");
        }
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if(!msgCheckDTO.isCheckStatus()){
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        //验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRpc.login(phone);
        if(!userLoginDTO.isIfLoginSuccess()){
            logger.error("login has error, phone is {}",phone);
            //极低概率发生，如果真有问题，提示系统异常
            return WebResponseVO.sysError();
        }
        String token=accountTokenRPC.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie=new Cookie("lmytk",token);
        //http://app.qiyu.live.com/html/qiyu_live_list_room.html
        //http://api.qiyu.live.com/live/api/userLogin/sendLoginCode
        cookie.setDomain("lmy.live.com");
        cookie.setPath("/");
        //cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30*24*3600);
        ////加上它，不然web浏览器不会将cookie自动记录下
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
