package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwTopic;
import com.ruoyi.system.domain.ZhwStory;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwTopicMapper {

    List<ZhwTopic> selectTopicList();

    ZhwTopic selectTopicById(Long topicId);

    int insertTopic(ZhwTopic topic);

    int updateTopic(ZhwTopic topic);

    int deleteTopic(Long topicId);

    /** 关联故事到话题 */
    int insertStoryTopic(@Param("storyId") Long storyId, @Param("topicId") Long topicId);

    /** 取消关联（指定话题） */
    int deleteStoryTopic(@Param("storyId") Long storyId, @Param("topicId") Long topicId);

    /** 删除故事的全部话题关联（故事删除时级联清理） */
    int deleteStoryTopicByStoryId(Long storyId);

    /** 查询话题广场故事列表（最热=热度排序，最新=更新时间排序） */
    List<ZhwStory> selectTopicStories(@Param("topicId") Long topicId,
                                       @Param("sortType") String sortType,
                                       @Param("currentUserId") Long currentUserId);

    /** 查询故事绑定的话题名称列表 */
    List<String> selectTopicNamesByStoryId(Long storyId);

    /** 统计进行中的话题数 */
    int countActiveTopics();
}
