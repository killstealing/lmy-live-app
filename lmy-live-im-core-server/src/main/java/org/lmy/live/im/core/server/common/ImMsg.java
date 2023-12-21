package org.lmy.live.im.core.server.common;

import java.util.Arrays;

public class ImMsg {
    //魔数 用于校验
    private short magic;
    //用于标识当前消息的作用，后续会交给不同的handler去处理
    private int code;
    //用于记录body的长度
    private int len;
    //存储消息体的内容，一般会按照字节数组的方式去存放
    private byte[] body;

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ImMsg{" +
                "magic=" + magic +
                ", code=" + code +
                ", len=" + len +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}