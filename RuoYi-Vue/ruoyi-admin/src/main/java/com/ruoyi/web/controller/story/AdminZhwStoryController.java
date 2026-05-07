package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.IZhwStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端-故事管理")
@RestController
@RequestMapping("/admin/zhw/story")
public class AdminZhwStoryController extends BaseController {

    @Autowired
    private IZhwStoryService storyService;

    @Operation(summary = "故事列表（含禁用）", description = "status: 0=正常 1=禁用，不传=全部")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(storyService.adminListStories(keyword, status));
    }

    @Operation(summary = "禁用故事（status=1）或恢复（status=0）")
    @PreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{storyId}/status")
    public AjaxResult updateStatus(@PathVariable Long storyId, @RequestParam String status) {
        if (!"0".equals(status) && !"1".equals(status)) {
            return error("status参数只能为0或1");
        }
        storyService.updateStoryStatus(storyId, status);
        return success("操作成功");
    }

    @Operation(summary = "删除故事（软删除）")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{storyId}")
    public AjaxResult delete(@PathVariable Long storyId) {
        storyService.adminDeleteStory(storyId);
        return success("删除成功");
    }

    @Operation(summary = "批量删除故事")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchDelete(@PathVariable String ids) {
        int count = 0;
        for (String id : ids.split(",")) {
            storyService.adminDeleteStory(Long.valueOf(id));
            count++;
        }
        return success("已删除 " + count + " 个故事");
    }

    @Operation(summary = "批量更新故事状态")
    @PreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/batch/status")
    public AjaxResult batchUpdateStatus(@RequestBody Map<String, String> params) {
        String ids = params.get("ids");
        String status = params.get("status");
        if (!"0".equals(status) && !"1".equals(status)) {
            return error("status参数只能为0或1");
        }
        int count = 0;
        for (String id : ids.split(",")) {
            storyService.updateStoryStatus(Long.valueOf(id), status);
            count++;
        }
        return success("已" + ("1".equals(status) ? "禁用" : "恢复") + " " + count + " 个故事");
    }
}
