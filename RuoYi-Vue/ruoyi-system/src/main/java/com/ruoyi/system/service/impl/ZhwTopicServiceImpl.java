package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.domain.ZhwTopic;
import com.ruoyi.system.mapper.ZhwTopicMapper;
import com.ruoyi.system.service.IZhwTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZhwTopicServiceImpl implements IZhwTopicService {

    @Autowired
    private ZhwTopicMapper topicMapper;

    @Override
    public List<ZhwTopic> listTopics() {
        return topicMapper.selectTopicList();
    }

    @Override
    public ZhwTopic getTopicDetail(Long topicId) {
        return topicMapper.selectTopicById(topicId);
    }

    @Override
    public List<ZhwStory> listTopicStories(Long topicId, String sortType, Long currentUserId) {
        return topicMapper.selectTopicStories(topicId, sortType, currentUserId == null ? 0L : currentUserId);
    }

    @Override
    public void joinTopic(Long storyId, Long topicId) {
        topicMapper.insertStoryTopic(storyId, topicId);
    }

    @Override
    public int countActiveTopics() {
        return topicMapper.countActiveTopics();
    }

    @Override
    public Long createTopic(ZhwTopic topic) {
        topicMapper.insertTopic(topic);
        return topic.getTopicId();
    }

    @Override
    public void updateTopic(ZhwTopic topic) {
        topicMapper.updateTopic(topic);
    }

    @Override
    public void deleteTopic(Long topicId) {
        topicMapper.deleteTopic(topicId);
    }
}
