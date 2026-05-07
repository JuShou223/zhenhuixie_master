package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.SensitiveWordUtils;
import com.ruoyi.system.domain.ZhwBranch;
import com.ruoyi.system.service.IZhwBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "分支接口")
@RestController
@RequestMapping("/zhw/branch")
public class ZhwBranchController extends BaseController {

    @Autowired
    private IZhwBranchService branchService;

    @Operation(summary = "分支详情（同时记录浏览量）")
    @Anonymous
    @GetMapping("/{branchId}")
    public AjaxResult getDetail(@PathVariable Long branchId) {
        Long currentUserId = getCurrentUserIdSafely();
        ZhwBranch branch = branchService.getBranchDetail(branchId, currentUserId);
        if (branch == null) {
            return error("分支不存在");
        }
        return success(branch);
    }

    @Operation(summary = "子分支列表（后续接龙）")
    @Anonymous
    @GetMapping("/{branchId}/children")
    public TableDataInfo children(@PathVariable Long branchId,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "5") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(branchService.listChildBranches(branchId, currentUserId));
    }

    @Operation(summary = "创建分支（需登录）", description = "必填：storyId、content、parentType；parentType=1时需填parentId")
    @PostMapping
    public AjaxResult create(@RequestBody ZhwBranch branch) {
        if (branch.getStoryId() == null) {
            return error("故事ID不能为空");
        }
        if (branch.getContent() == null || branch.getContent().length() < 15) {
            return error("内容不能少于15字");
        }
        if (branch.getContent().length() > 300) {
            return error("内容不能超过300字");
        }
        if (branch.getParentType() == null) {
            branch.setParentType(0);
        }
        if (branch.getParentType() == 1 && (branch.getParentId() == null || branch.getParentId() <= 0)) {
            return error("续写分支时必须指定父分支ID");
        }
        if ((branch.getChapterTitle() != null && SensitiveWordUtils.containsSensitiveWord(branch.getChapterTitle()))
                || SensitiveWordUtils.containsSensitiveWord(branch.getContent())) {
            return error("内容包含敏感词，请修改后重试");
        }
        branch.setUserId(getUserId());
        branch.setCreateBy(getUsername());
        Long branchId = branchService.createBranch(branch);
        return AjaxResult.success("发布成功", branchId);
    }

    private Long getCurrentUserIdSafely() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return 0L;
        }
    }
}
