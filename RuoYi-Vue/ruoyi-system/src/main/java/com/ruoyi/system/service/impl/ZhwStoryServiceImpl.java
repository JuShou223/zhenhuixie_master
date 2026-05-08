package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.domain.ZhwUserScore;
import com.ruoyi.system.mapper.ZhwStoryMapper;
import com.ruoyi.system.mapper.ZhwTopicMapper;
import com.ruoyi.system.mapper.ZhwUserScoreMapper;
import com.ruoyi.system.service.IZhwStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZhwStoryServiceImpl implements IZhwStoryService {

    @Autowired
    private ZhwStoryMapper storyMapper;

    @Autowired
    private ZhwTopicMapper topicMapper;

    @Autowired
    private ZhwUserScoreMapper userScoreMapper;

    @Override
    public List<ZhwStory> listStories(String queryType, Long topicId, Long currentUserId) {
        return storyMapper.selectStoryList(queryType, topicId, currentUserId);
    }

    @Override
    public ZhwStory getStoryDetail(Long storyId, Long currentUserId) {
        ZhwStory story = storyMapper.selectStoryById(storyId, currentUserId == null ? 0L : currentUserId);
        if (story == null) {
            return null;
        }
        // 异步增加浏览量（简化处理，直接同步更新）
        storyMapper.incrementViewCount(storyId);
        story.setViewCount(story.getViewCount() + 1);
        return story;
    }

    @Override
    @Transactional
    public Long createStory(ZhwStory story) {
        storyMapper.insertStory(story);
        return story.getStoryId();
    }

    @Override
    public void incrViewCount(Long storyId) {
        storyMapper.incrementViewCount(storyId);
    }

    @Override
    public void refreshHeatScore(Long storyId) {
        storyMapper.refreshParticipantCount(storyId);
        storyMapper.refreshHeatScore(storyId);
    }

    @Override
    public List<ZhwUserScore> getRanking(Long storyId) {
        return userScoreMapper.selectRankingByStory(storyId, 10);
    }

    @Override
    public List<ZhwStory> listUserStories(Long userId, Long currentUserId) {
        return storyMapper.selectByUserId(userId, currentUserId != null ? currentUserId : 0L);
    }

    @Override
    public List<ZhwStory> adminListStories(String keyword, String status) {
        return storyMapper.selectAdminStoryList(keyword, status);
    }

    @Override
    public void updateStoryStatus(Long storyId, String status) {
        storyMapper.updateStoryStatus(storyId, status);
    }

    @Override
    @Transactional
    public void adminDeleteStory(Long storyId) {
        storyMapper.deleteStoryAdmin(storyId);
        topicMapper.deleteStoryTopicByStoryId(storyId);
    }

    @Override
    public java.util.Map<String, Object> getPlatformStats() {
        return storyMapper.selectPlatformStats();
    }

    @Override
    public List<java.util.Map<String, Object>> getDailyNewStories(int days) {
        return storyMapper.selectDailyNewStories(days);
    }

    @Override
    public void markCollected(Long storyId, boolean collected) {
        storyMapper.updateCollected(storyId, collected ? 1 : 0);
    }

    @Override
    public List<ZhwStory> listCollectedStories() {
        return storyMapper.selectCollectedStories();
    }

    @Override
    public List<ZhwStory> listHotStoriesForExport(int minBranches, int limit) {
        return storyMapper.selectHotStoriesForExport(minBranches, limit);
    }
}
