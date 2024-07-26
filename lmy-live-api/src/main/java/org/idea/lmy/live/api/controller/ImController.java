package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.IImService;
import org.idea.lmy.live.api.vo.resp.ImConfigVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im")
public class ImController {

    @Resource
    private IImService imService;

    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig() {
        ImConfigVO imConfigVO = imService.getImConfig();
        return WebResponseVO.success(imConfigVO);
    }

}
