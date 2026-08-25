package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IZhwHomeBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "首页活动横幅接口")
@RestController
@RequestMapping("/zhw/banner")
public class ZhwHomeBannerController extends BaseController {

    @Autowired
    private IZhwHomeBannerService bannerService;

    @Operation(summary = "首页横幅列表（仅启用中）")
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        return success(bannerService.listEnabledBanners());
    }
}
