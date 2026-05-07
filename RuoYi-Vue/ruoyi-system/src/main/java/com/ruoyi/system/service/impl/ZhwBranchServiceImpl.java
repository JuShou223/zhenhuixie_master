package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwBranch;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.mapper.ZhwBranchMapper;
import com.ruoyi.system.mapper.ZhwStoryMapper;
import com.ruoyi.system.mapper.ZhwUserScoreMapper;
import com.ruoyi.system.service.IZhwBranchService;
import com.ruoyi.system.service.IZhwNotificationService;
import com.ruoyi.system.service.IZhwStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZhwBranchServiceImpl implements IZhwBranchService {

    @Autowired
    private ZhwBranchMapper branchMapper;

    @Autowired
    private ZhwStoryMapper storyMapper;

    @Autowired
    private ZhwUserScoreMapper userScoreMapper;

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private IZhwNotificationService notificationService;

    @Override
    public ZhwBranch getBranchDetail(Long branchId, Long currentUserId) {
        ZhwBranch branch = branchMapper.selectBranchById(branchId, currentUserId == null ? 0L : currentUserId);
        if (branch != null) {
            branchMapper.incrementViewCount(branchId);
            branch.setViewCount(branch.getViewCount() + 1);
        }
        return branch;
    }

    @Override
    public List<ZhwBranch> listFirstLevelBranches(Long storyId, Long currentUserId) {
        return branchMapper.selectFirstLevelBranches(storyId, currentUserId == null ? 0L : currentUserId);
    }

    @Override
    public List<ZhwBranch> listChildBranches(Long parentId, Long currentUserId) {
        return branchMapper.selectChildBranches(parentId, currentUserId == null ? 0L : currentUserId);
    }

    @Override
    @Transactional
    public Long createBranch(ZhwBranch branch) {
        // 1. 计算章节序号
        if (branch.getParentType() == 0) {
            // 续写故事设定 → 第1章
            branch.setChapterNum(1);
            branch.setParentId(0L);
        } else {
            // 续写某分支 → 父章节序号 + 1
            ZhwBranch parentBranch = branchMapper.selectBranchById(branch.getParentId(), 0L);
            branch.setChapterNum(parentBranch != null ? parentBranch.getChapterNum() + 1 : 1);
        }

        // 2. 插入分支
        branchMapper.insertBranch(branch);

        Long storyId = branch.getStoryId();
        Long branchId = branch.getBranchId();

        // 3. 更新父节点子分支计数
        if (branch.getParentType() == 1 && branch.getParentId() > 0) {
            branchMapper.updateChildCount(branch.getParentId(), 1);
        }

        // 4. 更新故事分支数
        storyMapper.updateBranchCount(storyId, 1);

        // 5. 刷新热度
        storyService.refreshHeatScore(storyId);

        // 6. 给父内容作者发续写通知（自己续写自己不通知）
        ZhwStory story = storyMapper.selectStoryById(storyId, 0L);
        if (story != null && !story.getUserId().equals(branch.getUserId())) {
            Long targetId;
            Integer targetType;
            Long targetOwnerId;
            if (branch.getParentType() == 0) {
                // 续写故事设定 → 通知故事作者
                targetId = storyId;
                targetType = 0;
                targetOwnerId = story.getUserId();
            } else {
                // 续写分支 → 通知分支作者
                ZhwBranch parentBranch = branchMapper.selectBranchById(branch.getParentId(), 0L);
                targetId = branch.getParentId();
                targetType = 1;
                targetOwnerId = parentBranch != null ? parentBranch.getUserId() : story.getUserId();
            }
            if (!targetOwnerId.equals(branch.getUserId())) {
                notificationService.sendNotification(targetOwnerId, branch.getUserId(),
                        3, targetId, targetType, storyId, branch.getContent());
                // 被续写积分 +30
                userScoreMapper.addScore(targetOwnerId, storyId, 30);
            }
        }

        return branchId;
    }
}
