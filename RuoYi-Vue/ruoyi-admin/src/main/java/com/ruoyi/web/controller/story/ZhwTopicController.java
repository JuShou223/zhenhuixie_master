package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.IZhwTopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "话题挑战接口")
@RestController
@RequestMapping("/zhw/topic")
public class ZhwTopicController extends BaseController {

    @Autowired
    private IZhwTopicService topicService;

    @Operation(summary = "话题列表")
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        return success(topicService.listTopics());
    }

    @Operation(summary = "话题详情（活动公告）")
    @Anonymous
    @GetMapping("/{topicId}")
    public AjaxResult detail(@PathVariable Long topicId) {
        return success(topicService.getTopicDetail(topicId));
    }

    @Operation(summary = "话题广场故事列表", description = "sortType: hot=最热 new=最新")
    @Anonymous
    @GetMapping("/{topicId}/stories")
    public TableDataInfo stories(@PathVariable Long topicId,
                               @RequestParam(defaultValue = "hot") String sortType,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(topicService.listTopicStories(topicId, sortType, currentUserId));
    }

    @Operation(summary = "故事参加话题（需登录）", description = "Body: { storyId, topicId }")
    @PostMapping("/join")
    public AjaxResult join(@RequestBody Map<String, Object> body) {
        Long storyId = Long.valueOf(body.get("storyId").toString());
        Long topicId = Long.valueOf(body.get("topicId").toString());
        topicService.joinTopic(storyId, topicId);
        return success("参与成功");
    }

    private Long getCurrentUserIdSafely() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return 0L;
        }
    }
}
