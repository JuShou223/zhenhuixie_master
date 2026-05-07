package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwComment;
import java.util.List;

public interface IZhwCommentService {

    /** 根评论列表（branchId=0查故事评论，>0查分支评论） */
    List<ZhwComment> listRootComments(Long storyId, Long branchId, Long currentUserId);

    /** 某评论的回复列表 */
    List<ZhwComment> listReplies(Long parentId, Long currentUserId);

    /** 发布评论/回复 */
    Long addComment(ZhwComment comment);

    /** 删除评论（仅作者可删） */
    void deleteComment(Long commentId, Long userId);

    /** 评论总数 */
    int countComments(Long storyId, Long branchId);

    /** 管理端评论列表 */
    List<ZhwComment> adminListComments(String keyword, Long storyId);

    /** 管理端强制删除评论 */
    void adminDeleteComment(Long commentId);
}
