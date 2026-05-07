package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwBranch;
import com.ruoyi.system.domain.ZhwComment;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.mapper.ZhwBranchMapper;
import com.ruoyi.system.mapper.ZhwCommentMapper;
import com.ruoyi.system.mapper.ZhwStoryMapper;
import com.ruoyi.system.mapper.ZhwUserScoreMapper;
import com.ruoyi.system.service.IZhwCommentService;
import com.ruoyi.system.service.IZhwNotificationService;
import com.ruoyi.system.service.IZhwStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZhwCommentServiceImpl implements IZhwCommentService {

    @Autowired
    private ZhwCommentMapper commentMapper;

    @Autowired
    private ZhwStoryMapper storyMapper;

    @Autowired
    private ZhwBranchMapper branchMapper;

    @Autowired
    private ZhwUserScoreMapper userScoreMapper;

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private IZhwNotificationService notificationService;

    @Override
    public List<ZhwComment> listRootComments(Long storyId, Long branchId, Long currentUserId) {
        return commentMapper.selectRootComments(storyId, branchId, currentUserId == null ? 0L : currentUserId);
    }

    @Override
    public List<ZhwComment> listReplies(Long parentId, Long currentUserId) {
        return commentMapper.selectReplies(parentId, currentUserId == null ? 0L : currentUserId);
    }

    @Override
    @Transactional
    public Long addComment(ZhwComment comment) {
        // 默认值处理
        if (comment.getBranchId() == null) comment.setBranchId(0L);
        if (comment.getParentId() == null) comment.setParentId(0L);
        if (comment.getReplyToUserId() == null) comment.setReplyToUserId(0L);

        commentMapper.insertComment(comment);

        Long storyId = comment.getStoryId();
        Long branchId = comment.getBranchId();
        Long commentId = comment.getCommentId();

        // 更新评论计数
        storyMapper.updateCommentCount(storyId, 1);
        if (branchId > 0) {
            branchMapper.updateCommentCount(branchId, 1);
        }

        // 刷新热度
        storyService.refreshHeatScore(storyId);

        // 发通知并加积分
        ZhwStory story = storyMapper.selectStoryById(storyId, 0L);
        if (story == null) return commentId;

        if (comment.getParentId() > 0) {
            // 自动解析被回复者ID
            if (comment.getReplyToUserId() == null || comment.getReplyToUserId() <= 0) {
                ZhwComment parent = commentMapper.selectCommentById(comment.getParentId());
                if (parent != null) {
                    comment.setReplyToUserId(parent.getUserId());
                }
            }
            // 回复评论 → 通知被回复者（type=2）
            if (comment.getReplyToUserId() > 0 && !comment.getReplyToUserId().equals(comment.getUserId())) {
                notificationService.sendNotification(comment.getReplyToUserId(), comment.getUserId(),
                        2, commentId, 2, storyId, comment.getContent());
                userScoreMapper.addScore(comment.getReplyToUserId(), storyId, 3);
            }
        } else {
            // 根评论 → 通知对应内容作者（type=1）
            Long contentOwnerId;
            Long targetId;
            Integer targetType;
            if (branchId > 0) {
                ZhwBranch branch = branchMapper.selectBranchById(branchId, 0L);
                contentOwnerId = branch != null ? branch.getUserId() : story.getUserId();
                targetId = branchId;
                targetType = 1;
            } else {
                contentOwnerId = story.getUserId();
                targetId = storyId;
                targetType = 0;
            }
            if (!contentOwnerId.equals(comment.getUserId())) {
                notificationService.sendNotification(contentOwnerId, comment.getUserId(),
                        1, targetId, targetType, storyId, comment.getContent());
                userScoreMapper.addScore(contentOwnerId, storyId, 3);
            }
        }

        return commentId;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        commentMapper.deleteComment(commentId, userId);
    }

    @Override
    public int countComments(Long storyId, Long branchId) {
        return commentMapper.countComments(storyId, branchId == null ? 0L : branchId);
    }

    @Override
    public List<ZhwComment> adminListComments(String keyword, Long storyId) {
        return commentMapper.selectAdminCommentList(keyword, storyId != null ? storyId : 0L);
    }

    @Override
    public void adminDeleteComment(Long commentId) {
        commentMapper.deleteCommentAdmin(commentId);
    }
}
