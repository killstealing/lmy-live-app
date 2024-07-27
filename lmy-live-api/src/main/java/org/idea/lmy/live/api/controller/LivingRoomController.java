package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.ILivingRoomService;
import org.idea.lmy.live.api.vo.LivingRoomInitVO;
import org.idea.lmy.live.api.vo.req.LivingRoomReqVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living")
public class LivingRoomController {


    @Resource
    private ILivingRoomService livingRoomService;


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
}
