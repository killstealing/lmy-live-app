package org.lmy.live.common.interfaces.enums;

public enum GatewayHeaderEnum {
    USER_LOGIN_ID("用户id", "lmy_gh_user_id");
    String desc;
    String name;

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    GatewayHeaderEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }
    @Override
    public String toString() {
        return "GatewayHeaderEnum{" +
                "desc='" + desc + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
