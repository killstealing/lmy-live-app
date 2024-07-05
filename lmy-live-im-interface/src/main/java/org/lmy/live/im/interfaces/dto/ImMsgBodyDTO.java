package org.lmy.live.im.interfaces.dto;

import java.io.Serializable;

public class ImMsgBodyDTO implements Serializable {
    private int appId;
    private Long userId;
    //从业务服务中获取，用于在IM服务建立连接的时候使用
    private String token;
    //和业务服务进行消息传递
    private String data;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImMsgBodyDTO{" +
                "appId='" + appId + '\'' +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
