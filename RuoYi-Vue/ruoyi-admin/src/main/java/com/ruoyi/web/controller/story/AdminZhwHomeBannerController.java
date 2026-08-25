package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.ZhwHomeBanner;
import com.ruoyi.system.service.IZhwHomeBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-首页横幅管理")
@RestController
@RequestMapping("/admin/zhw/banner")
public class AdminZhwHomeBannerController extends BaseController {

    @Autowired
    private IZhwHomeBannerService bannerService;

    @Operation(summary = "横幅列表")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public AjaxResult list() {
        return success(bannerService.listBanners());
    }

    @Operation(summary = "横幅详情")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{bannerId}")
    public AjaxResult detail(@PathVariable Long bannerId) {
        return success(bannerService.getBanner(bannerId));
    }

    @Operation(summary = "新增横幅")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "首页横幅管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody ZhwHomeBanner banner) {
        if (banner.getImage() == null || banner.getImage().isBlank()) {
            return error("图片URL不能为空");
        }
        banner.setCreateBy(getUsername());
        Long bannerId = bannerService.createBanner(banner);
        return AjaxResult.success("创建成功", bannerId);
    }

    @Operation(summary = "更新横幅")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "首页横幅管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody ZhwHomeBanner banner) {
        if (banner.getBannerId() == null) {
            return error("横幅ID不能为空");
        }
        banner.setUpdateBy(getUsername());
        bannerService.updateBanner(banner);
        return success("更新成功");
    }

    @Operation(summary = "删除横幅")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "首页横幅管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bannerId}")
    public AjaxResult delete(@PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        return success("删除成功");
    }
}
