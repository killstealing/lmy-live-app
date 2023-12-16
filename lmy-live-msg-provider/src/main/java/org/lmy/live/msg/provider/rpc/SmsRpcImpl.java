package org.lmy.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.msg.dto.MsgCheckDTO;
import org.lmy.live.msg.enums.MsgSendResultEnum;
import org.lmy.live.msg.interfaces.ISmsRpc;
import org.lmy.live.msg.provider.service.ISmsService;

@DubboService
public class SmsRpcImpl  implements ISmsRpc {

    @Resource
    private ISmsService iSmsService;


    @Override
    public MsgSendResultEnum sendMessage(String phone) {
        return iSmsService.sendMessage(phone);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return iSmsService.checkLoginCode(phone, code);
    }

    @Override
    public void insertOne(String phone, Integer code) {
        iSmsService.insertOne(phone, code);
    }
}
