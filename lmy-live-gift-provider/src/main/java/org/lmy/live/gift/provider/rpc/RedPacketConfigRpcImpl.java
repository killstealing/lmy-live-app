package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigRespDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;
import org.lmy.live.gift.interfaces.rpc.IRedPacketConfigRpc;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;
import org.lmy.live.gift.provider.service.IRedPacketConfigService;

/**
 * @Author idea
 * @Date: Created in 22:56 2023/9/5
 * @Description
 */
@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {

    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.updateById(ConvertBeanUtils.convert(redPacketConfigReqDTO,RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.receiveRedPacket(reqDTO);
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
