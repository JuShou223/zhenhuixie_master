package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.domain.ZhwTopic;
import java.util.List;


public interface IZhwTopicService {

    List<ZhwTopic> listTopics();

    ZhwTopic getTopicDetail(Long topicId);

    /** 话题广场故事列表（sortType: hot / new） */
    List<ZhwStory> listTopicStories(Long topicId, String sortType, Long currentUserId);

    /** 故事参加话题 */
    void joinTopic(Long storyId, Long topicId);

    /** 统计进行中的话题数 */
    int countActiveTopics();

    /** 管理端：创建话题 */
    Long createTopic(ZhwTopic topic);

    /** 管理端：更新话题 */
    void updateTopic(ZhwTopic topic);

    /** 管理端：删除话题（软删除） */
    void deleteTopic(Long topicId);
}
