package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.ZhwTopic;
import com.ruoyi.system.service.IZhwTopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-话题管理")
@RestController
@RequestMapping("/admin/zhw/topic")
public class AdminZhwTopicController extends BaseController {

    @Autowired
    private IZhwTopicService topicService;

    @Operation(summary = "话题列表")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public AjaxResult list() {
        return success(topicService.listTopics());
    }

    @Operation(summary = "话题详情")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{topicId}")
    public AjaxResult detail(@PathVariable Long topicId) {
        return success(topicService.getTopicDetail(topicId));
    }

    @Operation(summary = "创建话题")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "话题管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody ZhwTopic topic) {
        if (topic.getTitle() == null || topic.getTitle().isBlank()) {
            return error("话题标题不能为空");
        }
        topic.setCreateBy(getUsername());
        Long topicId = topicService.createTopic(topic);
        return AjaxResult.success("创建成功", topicId);
    }

    @Operation(summary = "更新话题")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "话题管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody ZhwTopic topic) {
        if (topic.getTopicId() == null) {
            return error("话题ID不能为空");
        }
        topic.setUpdateBy(getUsername());
        topicService.updateTopic(topic);
        return success("更新成功");
    }

    @Operation(summary = "删除话题")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "话题管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{topicId}")
    public AjaxResult delete(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return success("删除成功");
    }

    @Operation(summary = "批量删除话题")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "话题管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchDelete(@PathVariable String ids) {
        int count = 0;
        for (String id : ids.split(",")) {
            topicService.deleteTopic(Long.valueOf(id));
            count++;
        }
        return success("已删除 " + count + " 个话题");
    }
}
