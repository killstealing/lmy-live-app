package org.lmy.live.msg.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lmy.sms.ccp")
public class ApplicationProperties {
    private String serverIp;
    private String serverPort;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String phone;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getAccountSId() {
        return accountSId;
    }

    public void setAccountSId(String accountSId) {
        this.accountSId = accountSId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "serverIp='" + serverIp + '\'' +
                ", serverPort='" + serverPort + '\'' +
                ", accountSId='" + accountSId + '\'' +
                ", accountToken='" + accountToken + '\'' +
                ", appId='" + appId + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
