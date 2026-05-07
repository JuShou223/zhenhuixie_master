package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwBranch;
import com.ruoyi.system.domain.ZhwComment;
import com.ruoyi.system.domain.ZhwLike;
import com.ruoyi.system.domain.ZhwMark;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IZhwInteractionService;
import com.ruoyi.system.service.IZhwNotificationService;
import com.ruoyi.system.service.IZhwStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZhwInteractionServiceImpl implements IZhwInteractionService {

    @Autowired
    private ZhwLikeMapper likeMapper;

    @Autowired
    private ZhwMarkMapper markMapper;

    @Autowired
    private ZhwStoryMapper storyMapper;

    @Autowired
    private ZhwBranchMapper branchMapper;

    @Autowired
    private ZhwCommentMapper commentMapper;

    @Autowired
    private ZhwUserScoreMapper userScoreMapper;

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private IZhwNotificationService notificationService;

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long targetId, Integer targetType) {
        ZhwLike existing = likeMapper.selectLike(userId, targetId, targetType);
        if (existing != null) {
            // 取消点赞
            likeMapper.deleteLike(userId, targetId, targetType);
            updateLikeCount(targetId, targetType, -1);
            return false;
        } else {
            // 点赞
            ZhwLike like = new ZhwLike();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            likeMapper.insertLike(like);
            updateLikeCount(targetId, targetType, 1);

            // 发通知 & 积分
            Long ownerId = getTargetOwnerId(targetId, targetType);
            Long storyId = getTargetStoryId(targetId, targetType);
            if (ownerId != null && !ownerId.equals(userId)) {
                notificationService.sendNotification(ownerId, userId, 0, targetId, targetType, storyId, null);
                userScoreMapper.addScore(ownerId, storyId, 1);
            }

            return true;
        }
    }

    @Override
    @Transactional
    public boolean toggleMark(Long userId, Long storyId, Long branchId) {
        if (branchId == null) branchId = 0L;
        ZhwMark existing = markMapper.selectMark(userId, storyId, branchId);
        if (existing != null) {
            markMapper.deleteMark(userId, storyId, branchId);
            return false;
        } else {
            ZhwMark mark = new ZhwMark();
            mark.setUserId(userId);
            mark.setStoryId(storyId);
            mark.setBranchId(branchId);
            markMapper.insertMark(mark);

            // 积分：被标记 +20，给内容作者
            Long ownerId = getMarkTargetOwnerId(storyId, branchId);
            if (ownerId != null && !ownerId.equals(userId)) {
                userScoreMapper.addScore(ownerId, storyId, 20);
            }
            return true;
        }
    }

    @Override
    public List<ZhwMark> listUserMarks(Long userId) {
        return markMapper.selectUserMarkList(userId);
    }

    @Override
    public void updateMarkReadTime(Long userId, Long storyId, Long branchId) {
        markMapper.updateLastReadTime(userId, storyId, branchId == null ? 0L : branchId);
    }

    // ---- private helpers ----

    private void updateLikeCount(Long targetId, Integer targetType, int delta) {
        switch (targetType) {
            case 0 -> {
                storyMapper.updateLikeCount(targetId, delta);
                storyService.refreshHeatScore(targetId);
            }
            case 1 -> branchMapper.updateLikeCount(targetId, delta);
            case 2 -> commentMapper.updateLikeCount(targetId, delta);
        }
    }

    private Long getTargetOwnerId(Long targetId, Integer targetType) {
        return switch (targetType) {
            case 0 -> {
                ZhwStory s = storyMapper.selectStoryById(targetId, 0L);
                yield s != null ? s.getUserId() : null;
            }
            case 1 -> {
                ZhwBranch b = branchMapper.selectBranchById(targetId, 0L);
                yield b != null ? b.getUserId() : null;
            }
            case 2 -> {
                ZhwComment c = commentMapper.selectCommentById(targetId);
                yield c != null ? c.getUserId() : null;
            }
            default -> null;
        };
    }

    private Long getTargetStoryId(Long targetId, Integer targetType) {
        return switch (targetType) {
            case 0 -> targetId;
            case 1 -> {
                ZhwBranch b = branchMapper.selectBranchById(targetId, 0L);
                yield b != null ? b.getStoryId() : 0L;
            }
            case 2 -> {
                ZhwComment c = commentMapper.selectCommentById(targetId);
                yield c != null ? c.getStoryId() : 0L;
            }
            default -> 0L;
        };
    }

    private Long getMarkTargetOwnerId(Long storyId, Long branchId) {
        if (branchId == 0L) {
            ZhwStory s = storyMapper.selectStoryById(storyId, 0L);
            return s != null ? s.getUserId() : null;
        } else {
            ZhwBranch b = branchMapper.selectBranchById(branchId, 0L);
            return b != null ? b.getUserId() : null;
        }
    }
}
