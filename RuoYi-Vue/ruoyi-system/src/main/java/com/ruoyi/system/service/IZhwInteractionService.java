package com.ruoyi.system.service;

/**
 * 点赞 & 标记 互动服务
 */
public interface IZhwInteractionService {

    /**
     * 切换点赞状态
     * @param userId     操作用户
     * @param targetId   目标ID
     * @param targetType 目标类型（0=故事 1=分支 2=评论）
     * @return true=已点赞 false=已取消
     */
    boolean toggleLike(Long userId, Long targetId, Integer targetType);

    /**
     * 切换标记状态
     * @param userId   操作用户
     * @param storyId  故事ID
     * @param branchId 分支ID（0=标记故事设定本身）
     * @return true=已标记 false=已取消
     */
    boolean toggleMark(Long userId, Long storyId, Long branchId);

    /** 用户标记列表（含更新提示） */
    java.util.List<com.ruoyi.system.domain.ZhwMark> listUserMarks(Long userId);

    /** 更新标记的最后阅读时间（进入被标记的页面时调用） */
    void updateMarkReadTime(Long userId, Long storyId, Long branchId);
}
