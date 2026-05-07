package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IZhwStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理端-数据概览")
@RestController
@RequestMapping("/admin/zhw/dashboard")
public class AdminZhwDashboardController extends BaseController {

    @Autowired
    private IZhwStoryService storyService;

    @Operation(summary = "平台统计概况")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/stats")
    public AjaxResult stats() {
        return success(storyService.getPlatformStats());
    }

    @Operation(summary = "近N天每日新增故事趋势", description = "默认最近7天")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/trend")
    public AjaxResult trend(@RequestParam(defaultValue = "7") int days) {
        if (days < 1 || days > 90) days = 7;
        Map<String, Object> result = new HashMap<>();
        result.put("dailyStories", storyService.getDailyNewStories(days));
        return success(result);
    }

    @Operation(summary = "热度 Top10 故事")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/hot")
    public AjaxResult hot() {
        PageHelper.startPage(1, 10);
        return success(storyService.listStories("hot", null, 0L));
    }
}
