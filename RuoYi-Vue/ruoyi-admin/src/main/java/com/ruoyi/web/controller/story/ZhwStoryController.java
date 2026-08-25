package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.SensitiveWordUtils;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.domain.ZhwUserScore;
import com.ruoyi.system.mapper.ZhwUserScoreMapper;
import com.ruoyi.system.service.IZhwBranchService;
import com.ruoyi.system.service.IZhwStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;

@Tag(name = "故事接口")
@RestController
@RequestMapping("/zhw/story")
public class ZhwStoryController extends BaseController {

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private IZhwBranchService branchService;

    @Autowired
    private ZhwUserScoreMapper userScoreMapper;

    @Operation(summary = "首页故事列表", description = "queryType: recommend=推荐(随机) / hot=热门(热度排序)")
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(defaultValue = "recommend") String queryType,
                               @RequestParam(required = false) Long topicId,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        List<ZhwStory> list = storyService.listStories(queryType, topicId, currentUserId);
        return getDataTable(list);
    }

    @Operation(summary = "搜索故事", description = "按标题/设定内容/作者昵称模糊匹配")
    @Anonymous
    @GetMapping("/search")
    public TableDataInfo search(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return getDataTable(java.util.Collections.emptyList());
        }
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        List<ZhwStory> list = storyService.searchStories(keyword.trim(), currentUserId);
        return getDataTable(list);
    }

    @Operation(summary = "故事详情")
    @Anonymous
    @GetMapping("/{storyId}")
    public AjaxResult getDetail(@PathVariable Long storyId) {
        Long currentUserId = getCurrentUserIdSafely();
        ZhwStory story = storyService.getStoryDetail(storyId, currentUserId);
        if (story == null) {
            return error("故事不存在");
        }
        return success(story);
    }

    @Operation(summary = "创建故事（需登录）", description = "必填：title、settingContent；可选：cover")
    @PostMapping
    public AjaxResult create(@RequestBody ZhwStory story) {
        if (story.getTitle() == null || story.getTitle().isBlank()) {
            return error("故事标题不能为空");
        }
        if (story.getSettingContent() == null || story.getSettingContent().length() < 15) {
            return error("故事设定内容不能少于15字");
        }
        if (story.getSettingContent().length() > 300) {
            return error("故事设定内容不能超过300字");
        }
        if (SensitiveWordUtils.containsSensitiveWord(story.getTitle())
                || SensitiveWordUtils.containsSensitiveWord(story.getSettingContent())) {
            return error("内容包含敏感词，请修改后重试");
        }
        story.setUserId(getUserId());
        story.setCreateBy(getUsername());
        Long storyId = storyService.createStory(story);
        return AjaxResult.success("发布成功", storyId);
    }

    @Operation(summary = "故事一级分支列表")
    @Anonymous
    @GetMapping("/{storyId}/branches")
    public TableDataInfo branches(@PathVariable Long storyId,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "5") int pageSize) {
        Long currentUserId = getCurrentUserIdSafely();
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(branchService.listFirstLevelBranches(storyId, currentUserId));
    }

    @Operation(summary = "故事排行榜")
    @Anonymous
    @GetMapping("/{storyId}/ranking")
    public AjaxResult ranking(@PathVariable Long storyId) {
        List<ZhwUserScore> ranking = storyService.getRanking(storyId);
        Map<String, Object> result = new HashMap<>();
        result.put("ranking", ranking);

        // 当前用户的排名（即使不在 Top10 也返回）
        Long currentUserId = getCurrentUserIdSafely();
        if (currentUserId > 0) {
            ZhwUserScore myScore = userScoreMapper.selectUserScore(currentUserId, storyId);
            if (myScore != null) {
                result.put("myScore", myScore);
            }
        }
        return success(result);
    }

    /** 安全获取当前用户ID（未登录返回0） */
    private Long getCurrentUserIdSafely() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return 0L;
        }
    }
}
