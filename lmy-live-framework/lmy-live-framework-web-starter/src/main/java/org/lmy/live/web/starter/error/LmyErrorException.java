package org.lmy.live.web.starter.error;

/**
 * @Author idea
 * @Date: Created in 11:15 2023/8/2
 * @Description
 */
public class LmyErrorException extends RuntimeException{

    private int errorCode;
    private String errorMsg;

    public LmyErrorException(LmyBaseError lmyBaseError) {
        this.errorCode = lmyBaseError.getErrorCode();
        this.errorMsg = lmyBaseError.getErrorMsg();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
