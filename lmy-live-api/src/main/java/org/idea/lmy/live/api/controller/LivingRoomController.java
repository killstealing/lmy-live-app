package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.ILivingRoomService;
import org.idea.lmy.live.api.vo.LivingRoomInitVO;
import org.idea.lmy.live.api.vo.req.LivingRoomReqVO;
import org.idea.lmy.live.api.vo.req.OnlinePkReqVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.web.starter.config.RequestLimit;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living")
public class LivingRoomController {


    @Resource
    private ILivingRoomService livingRoomService;

    @PostMapping("/prepareRedPacket")
    @RequestLimit(limit = 1, second = 10, msg = "正在初始化中，请稍等")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.prepareRedPacket(LmyRequestContext.getUserId(),livingRoomReqVO.getRoomId()));
    }

    @PostMapping("/startRedPacket")
    @RequestLimit(limit = 1, second = 10, msg = "正在广播直播间用户，请稍等")
    public WebResponseVO startRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.startRedPacket(LmyRequestContext.getUserId(),livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/getRedPacket")
    @RequestLimit(limit = 1, second = 1, msg = "")
    public WebResponseVO getRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.getRedPacket(LmyRequestContext.getUserId(),livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/list")
    public WebResponseVO list(LivingRoomReqVO livingRoomReqVO){
        if(livingRoomReqVO==null||livingRoomReqVO.getType()==null){
            return WebResponseVO.errorParam("需要给定直播间类型");
        }
        if(livingRoomReqVO.getPage()<=0||livingRoomReqVO.getPageSize()>100){
            return WebResponseVO.errorParam("分页查询参数错误");
        }
        return WebResponseVO.success(livingRoomService.list(livingRoomReqVO));
    }

    @PostMapping("/startingLiving")
    public WebResponseVO startLiving(Integer type){
        if(type==null){
            return WebResponseVO.errorParam("需要给定直播间类型");
        }
        Integer roomId = livingRoomService.startingLiving(type);
        LivingRoomInitVO livingRoomInitVO=new LivingRoomInitVO();
        livingRoomInitVO.setRoomId(roomId);
        return  WebResponseVO.success(livingRoomInitVO);
    }

    @PostMapping("/closeLiving")
    @RequestLimit(limit = 1, second = 10, msg = "关播请求过于频繁，请稍后再试")
    public WebResponseVO closeLiving(Integer roomId){
        if (roomId==null){
            return WebResponseVO.errorParam("需要给定直播间ID");
        }
        boolean closeStatus = livingRoomService.closeLiving(roomId);
        if (closeStatus){
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }

    /**
     * 获取主播相关配置信息（只有主播才会有权限）
     *
     * @return
     */
    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId) {
        long userId = LmyRequestContext.getUserId();
        return WebResponseVO.success(livingRoomService.anchorConfig(userId,roomId));
    }

    @PostMapping("/onlinePk")
    @RequestLimit(limit = 1,second = 3)
    public WebResponseVO onlinePK(OnlinePkReqVO onlinePkReqVO){
        ErrorAssert.isNotNull(onlinePkReqVO.getRoomId(), BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.onlinePk(onlinePkReqVO));
    }
}
