package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.SensitiveWordUtils;
import com.ruoyi.system.domain.ZhwComment;
import com.ruoyi.system.service.IZhwCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论接口")
@RestController
@RequestMapping("/zhw/comment")
public class ZhwCommentController extends BaseController {

    @Autowired
    private IZhwCommentService commentService;

    @Operation(summary = "根评论列表", description = "branchId=0或不传=故事评论；>0=分支评论")
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Long storyId,
                            @RequestParam(defaultValue = "0") Long branchId,
                            @RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "20") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(commentService.listRootComments(storyId, branchId, currentUserId));
    }

    @Operation(summary = "某评论的回复列表")
    @Anonymous
    @GetMapping("/{commentId}/replies")
    public TableDataInfo replies(@PathVariable Long commentId,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(commentService.listReplies(commentId, currentUserId));
    }

    @Operation(summary = "发布评论或回复（需登录）", description = "必填：storyId、content；可选：branchId、parentId、replyToUserId")
    @PostMapping
    public AjaxResult add(@RequestBody ZhwComment comment) {
        if (comment.getStoryId() == null) {
            return error("故事ID不能为空");
        }
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            return error("评论内容不能为空");
        }
        if (comment.getContent().length() > 500) {
            return error("评论内容不能超过500字");
        }
        if (SensitiveWordUtils.containsSensitiveWord(comment.getContent())) {
            return error("内容包含敏感词，请修改后重试");
        }
        comment.setUserId(getUserId());
        comment.setCreateBy(getUsername());
        Long commentId = commentService.addComment(comment);
        return AjaxResult.success("评论成功", commentId);
    }

    @Operation(summary = "删除评论（仅作者）")
    @DeleteMapping("/{commentId}")
    public AjaxResult delete(@PathVariable Long commentId) {
        commentService.deleteComment(commentId, getUserId());
        return success();
    }

    private Long getCurrentUserIdSafely() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return 0L;
        }
    }
}
