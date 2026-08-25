package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwStory;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwStoryMapper {

    ZhwStory selectStoryById(@Param("storyId") Long storyId, @Param("currentUserId") Long currentUserId);

    /** 首页列表（推荐=随机，热门=热度排序），可按话题过滤 */
    List<ZhwStory> selectStoryList(@Param("queryType") String queryType,
                                   @Param("topicId") Long topicId,
                                   @Param("currentUserId") Long currentUserId);

    /** 搜索故事（标题/设定内容/作者昵称） */
    List<ZhwStory> searchStories(@Param("keyword") String keyword,
                                  @Param("currentUserId") Long currentUserId);

    int insertStory(ZhwStory story);

    int updateStory(ZhwStory story);

    /** 浏览量+1 */
    int incrementViewCount(Long storyId);

    /** 点赞数增减 */
    int updateLikeCount(@Param("storyId") Long storyId, @Param("delta") int delta);

    /** 评论数增减 */
    int updateCommentCount(@Param("storyId") Long storyId, @Param("delta") int delta);

    /** 分支数增减 */
    int updateBranchCount(@Param("storyId") Long storyId, @Param("delta") int delta);

    /** 刷新热度分值 */
    int refreshHeatScore(Long storyId);

    /** 刷新独立参与人数 */
    int refreshParticipantCount(Long storyId);

    /** 查询用户发布的故事列表 */
    List<ZhwStory> selectByUserId(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

    /** 统计用户已发布故事数（与 selectByUserId 过滤条件保持一致） */
    int countByUser(Long userId);

    /** 管理端故事列表（含禁用，支持关键词/状态筛选） */
    List<ZhwStory> selectAdminStoryList(@Param("keyword") String keyword, @Param("status") String status);

    /** 管理端禁用/恢复故事 */
    int updateStoryStatus(@Param("storyId") Long storyId, @Param("status") String status);

    /** 管理端强制删除故事（软删除） */
    int deleteStoryAdmin(Long storyId);

    /** 精选标记 */
    int updateCollected(@Param("storyId") Long storyId, @Param("collected") int collected);

    /** 精选故事列表 */
    List<ZhwStory> selectCollectedStories();

    /** 按分支数阈值筛选热门故事（导出用） */
    List<ZhwStory> selectHotStoriesForExport(@Param("minBranches") int minBranches, @Param("limit") int limit);

    /** 管理端统计各状态故事数 */
    int countStoriesByStatus(@Param("status") String status);

    /** 近N天每天新增故事数 */
    List<java.util.Map<String, Object>> selectDailyNewStories(@Param("days") int days);

    /** 平台总统计 */
    java.util.Map<String, Object> selectPlatformStats();
}
