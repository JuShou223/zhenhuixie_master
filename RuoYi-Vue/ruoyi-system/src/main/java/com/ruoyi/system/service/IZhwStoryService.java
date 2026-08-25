package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.domain.ZhwUserScore;
import java.util.List;

public interface IZhwStoryService {

    /** 首页列表（推荐/热门） */
    List<ZhwStory> listStories(String queryType, Long topicId, Long currentUserId);

    /** 搜索故事（标题/设定内容/作者昵称） */
    List<ZhwStory> searchStories(String keyword, Long currentUserId);

    /** 故事详情 */
    ZhwStory getStoryDetail(Long storyId, Long currentUserId);

    /** 创建故事 */
    Long createStory(ZhwStory story);

    /** 浏览量+1 */
    void incrViewCount(Long storyId);

    /** 刷新热度（分支创建/删除、评论、点赞后调用） */
    void refreshHeatScore(Long storyId);

    /** 故事排行榜 */
    List<ZhwUserScore> getRanking(Long storyId);

    /** 查询用户发布的故事列表 */
    List<ZhwStory> listUserStories(Long userId, Long currentUserId);

    /** 管理端故事列表 */
    List<ZhwStory> adminListStories(String keyword, String status);

    /** 禁用/恢复故事 */
    void updateStoryStatus(Long storyId, String status);

    /** 管理端强制删除故事 */
    void adminDeleteStory(Long storyId);

    /** 平台统计数据 */
    java.util.Map<String, Object> getPlatformStats();

    /** 近N天每天新增故事数 */
    List<java.util.Map<String, Object>> getDailyNewStories(int days);

    /** 标记/取消精选 */
    void markCollected(Long storyId, boolean collected);

    /** 精选故事列表 */
    List<ZhwStory> listCollectedStories();

    /** 按分支数阈值筛选热门故事（导出用） */
    List<ZhwStory> listHotStoriesForExport(int minBranches, int limit);
}
