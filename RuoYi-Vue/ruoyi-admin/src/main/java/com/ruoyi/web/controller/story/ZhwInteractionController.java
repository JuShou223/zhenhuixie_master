package com.ruoyi.web.controller.story;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IZhwInteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "点赞与标记接口")
@RestController
@RequestMapping("/zhw")
public class ZhwInteractionController extends BaseController {

    @Autowired
    private IZhwInteractionService interactionService;

    @Operation(summary = "切换点赞", description = "Body: { targetId, targetType }，targetType: 0=故事 1=分支 2=评论")
    @PostMapping("/like")
    public AjaxResult toggleLike(@RequestBody Map<String, Object> body) {
        Long targetId = Long.valueOf(body.get("targetId").toString());
        Integer targetType = Integer.valueOf(body.get("targetType").toString());
        boolean liked = interactionService.toggleLike(getUserId(), targetId, targetType);
        return AjaxResult.success(liked ? "点赞成功" : "已取消点赞", liked);
    }

    @Operation(summary = "切换标记", description = "Body: { storyId, branchId }，branchId=0表示标记故事设定本身")
    @PostMapping("/mark")
    public AjaxResult toggleMark(@RequestBody Map<String, Object> body) {
        Object storyIdObj = body.get("storyId");
        if (storyIdObj == null) return error("storyId不能为空");
        Long storyId = Long.valueOf(storyIdObj.toString());
        Long branchId = body.get("branchId") != null
                ? Long.valueOf(body.get("branchId").toString()) : 0L;
        boolean marked = interactionService.toggleMark(getUserId(), storyId, branchId);
        return AjaxResult.success(marked ? "标记成功" : "已取消", marked);
    }

    @Operation(summary = "我的标记列表")
    @GetMapping("/mark/list")
    public AjaxResult markList() {
        return success(interactionService.listUserMarks(getUserId()));
    }

    @Operation(summary = "更新标记已读时间", description = "进入被标记页面时调用，Body: { storyId, branchId }")
    @PutMapping("/mark/read")
    public AjaxResult updateMarkRead(@RequestBody Map<String, Object> body) {
        Long storyId = Long.valueOf(body.get("storyId").toString());
        Long branchId = body.containsKey("branchId")
                ? Long.valueOf(body.get("branchId").toString()) : 0L;
        interactionService.updateMarkReadTime(getUserId(), storyId, branchId);
        return success();
    }
}
