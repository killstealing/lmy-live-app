package org.idea.lmy.live.api.error;

import org.lmy.live.web.starter.constants.ErrorAppIdEnum;
import org.lmy.live.web.starter.error.LmyBaseError;

/**
 * @Author idea
 * @Date: Created in 15:41 2023/8/2
 * @Description
 */
public enum ApiErrorEnum implements LmyBaseError {

    PHONE_IS_EMPTY(1, "手机号不能为空"),
    PHONE_IN_VALID(2,"手机号格式异常"),
    SMS_CODE_ERROR(3,"验证码格式异常"),
    USER_LOGIN_ERROR(4,"用户登录失败"),
    GIFT_CONFIG_ERROR(5,"礼物信息异常"),
    SEND_GIFT_ERROR(6,"送礼失败");

    private String errorMsg;
    private int errorCode;

    ApiErrorEnum(int errorCode, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return Integer.parseInt(ErrorAppIdEnum.LMY_API_ERROR.getCode() + "" + errorCode);
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
