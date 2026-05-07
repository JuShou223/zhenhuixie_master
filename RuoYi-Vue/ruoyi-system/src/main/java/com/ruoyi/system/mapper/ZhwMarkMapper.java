package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwMark;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwMarkMapper {

    ZhwMark selectMark(@Param("userId") Long userId,
                        @Param("storyId") Long storyId,
                        @Param("branchId") Long branchId);

    int insertMark(ZhwMark mark);

    int deleteMark(@Param("userId") Long userId,
                   @Param("storyId") Long storyId,
                   @Param("branchId") Long branchId);

    /** 查询用户的所有标记（含更新状态） */
    List<ZhwMark> selectUserMarkList(@Param("userId") Long userId);

    /** 更新最后阅读时间 */
    int updateLastReadTime(@Param("userId") Long userId,
                           @Param("storyId") Long storyId,
                           @Param("branchId") Long branchId);

    /** 统计某分支/故事被标记的人数（用于积分） */
    int countMarksByTarget(@Param("storyId") Long storyId, @Param("branchId") Long branchId);
}
