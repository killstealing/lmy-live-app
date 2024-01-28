package org.lmy.live.im.interfaces.dto;

import java.io.Serializable;

public class ImMsgBodyDTO implements Serializable {
    private int appId;
    private Long userId;
    private String token;
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
