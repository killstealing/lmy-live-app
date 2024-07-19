package org.idea.lmy.live.api.vo;

public class UserLoginVO {
    private Long userId;
    private String token;

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

    @Override
    public String toString() {
        return "UserLoginVO{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                '}';
    }
}
