package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.IZhwCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-评论审核")
@RestController
@RequestMapping("/admin/zhw/comment")
public class AdminZhwCommentController extends BaseController {

    @Autowired
    private IZhwCommentService commentService;

    @Operation(summary = "评论列表", description = "支持关键词和storyId过滤")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long storyId,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(commentService.adminListComments(keyword, storyId));
    }

    @Operation(summary = "删除评论（强制软删除）")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{commentId}")
    public AjaxResult delete(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
        return success("删除成功");
    }

    @Operation(summary = "批量删除评论")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchDelete(@PathVariable String ids) {
        int count = 0;
        for (String id : ids.split(",")) {
            commentService.adminDeleteComment(Long.valueOf(id));
            count++;
        }
        return success("已删除 " + count + " 条评论");
    }
}
