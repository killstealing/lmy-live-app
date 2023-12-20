package org.lmy.live.msg.provider.config;

public enum SmsTemplateIdEnum {
    SMS_LOGIN_CODE_TEMPLATE("1","登录验证短信模板");
    String templateId;
    String desc;
    SmsTemplateIdEnum(String templateId, String desc) {
        this.templateId = templateId;
        this.desc = desc;
    }
    public String getTemplateId() {
        return templateId;
    }

    public String getDesc() {
        return desc;
    }



    @Override
    public String toString() {
        return "SmsTemplateIdEnum{" +
                "templateId=" + templateId +
                ", desc='" + desc + '\'' +
                '}';
    }
}
