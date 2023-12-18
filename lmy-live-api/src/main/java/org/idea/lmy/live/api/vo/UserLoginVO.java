package org.idea.lmy.live.api.vo;

public class UserLoginVO {
    private Long userId;

    @Override
    public String toString() {
        return "UserLoginVO{" +
                "userId=" + userId +
                '}';
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
