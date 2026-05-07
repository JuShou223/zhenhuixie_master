package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwLike;
import org.apache.ibatis.annotations.Param;

public interface ZhwLikeMapper {

    /** 查询点赞记录（判断是否已点赞） */
    ZhwLike selectLike(@Param("userId") Long userId,
                        @Param("targetId") Long targetId,
                        @Param("targetType") Integer targetType);

    int insertLike(ZhwLike like);

    int deleteLike(@Param("userId") Long userId,
                   @Param("targetId") Long targetId,
                   @Param("targetType") Integer targetType);
}
