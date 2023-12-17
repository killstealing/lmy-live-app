package com.lmy.live.user.dto;

public class UserLoginDTO {
    private String token;
    private Long userId;
    private boolean ifLoginSuccess;
    private String desc;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isIfLoginSuccess() {
        return ifLoginSuccess;
    }

    public void setIfLoginSuccess(boolean ifLoginSuccess) {
        this.ifLoginSuccess = ifLoginSuccess;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", ifLoginSuccess=" + ifLoginSuccess +
                ", desc='" + desc + '\'' +
                '}';
    }

    public static UserLoginDTO loginSuccess(Long userId, String token){
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setIfLoginSuccess(true);
        userLoginDTO.setUserId(userId);
        userLoginDTO.setToken(token);
        return userLoginDTO;
    }
    public static UserLoginDTO loginFailed(String desc){
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setIfLoginSuccess(false);
        userLoginDTO.setDesc(desc);
        return userLoginDTO;
    }
}
