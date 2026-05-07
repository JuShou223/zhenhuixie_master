package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwUserScore;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwUserScoreMapper {

    /** 查询故事排行榜（前N名） */
    List<ZhwUserScore> selectRankingByStory(@Param("storyId") Long storyId, @Param("limit") int limit);

    /** 积分增减 */
    int addScore(@Param("userId") Long userId, @Param("storyId") Long storyId, @Param("delta") int delta);

    /** 查询用户在某故事的积分 */
    ZhwUserScore selectUserScore(@Param("userId") Long userId, @Param("storyId") Long storyId);
}
