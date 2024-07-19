//package com.lmy.live.user.provider.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//@ConfigurationProperties(prefix = "lmy.rmq.producer")
//@Configuration
//public class RockerMQProducerProperties {
//
//    private String nameSrv;
//    private String groupName;
//    private int retryTimes;
//    private int sendTimeout;
//
//    public String getNameSrv() {
//        return nameSrv;
//    }
//
//    public void setNameSrv(String nameSrv) {
//        this.nameSrv = nameSrv;
//    }
//
//    public String getGroupName() {
//        return groupName;
//    }
//
//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }
//
//    public int getRetryTimes() {
//        return retryTimes;
//    }
//
//    public void setRetryTimes(int retryTimes) {
//        this.retryTimes = retryTimes;
//    }
//
//    public int getSendTimeout() {
//        return sendTimeout;
//    }
//
//    public void setSendTimeout(int sendTimeout) {
//        this.sendTimeout = sendTimeout;
//    }
//
//    @Override
//    public String toString() {
//        return "RockerMQProducerProperties{" +
//                "nameSrv='" + nameSrv + '\'' +
//                ", groupName='" + groupName + '\'' +
//                ", retryTimes=" + retryTimes +
//                ", sendTimeout=" + sendTimeout +
//                '}';
//    }
//}
