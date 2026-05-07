package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwComment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwCommentMapper {

    /** 查询根评论列表（storyId必填，branchId=0查故事评论，>0查分支评论） */
    List<ZhwComment> selectRootComments(@Param("storyId") Long storyId,
                                         @Param("branchId") Long branchId,
                                         @Param("currentUserId") Long currentUserId);

    /** 查询某根评论下的回复列表 */
    List<ZhwComment> selectReplies(@Param("parentId") Long parentId,
                                    @Param("currentUserId") Long currentUserId);

    /** 按ID查单条评论 */
    ZhwComment selectCommentById(Long commentId);

    int insertComment(ZhwComment comment);

    int deleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /** 点赞数增减 */
    int updateLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);

    /** 统计评论总数 */
    int countComments(@Param("storyId") Long storyId, @Param("branchId") Long branchId);

    /** 管理端评论列表（支持关键词/storyId筛选） */
    List<ZhwComment> selectAdminCommentList(@Param("keyword") String keyword, @Param("storyId") Long storyId);

    /** 管理端强制删除评论（不校验userId） */
    int deleteCommentAdmin(Long commentId);
}
