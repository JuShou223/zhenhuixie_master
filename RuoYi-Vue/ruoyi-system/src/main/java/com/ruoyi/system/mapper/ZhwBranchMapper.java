package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwBranch;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwBranchMapper {

    ZhwBranch selectBranchById(@Param("branchId") Long branchId,
                                @Param("currentUserId") Long currentUserId);

    /** 查询故事的一级分支（直接续写故事设定的） */
    List<ZhwBranch> selectFirstLevelBranches(@Param("storyId") Long storyId,
                                              @Param("currentUserId") Long currentUserId);

    /** 查询某分支的子分支列表 */
    List<ZhwBranch> selectChildBranches(@Param("parentId") Long parentId,
                                         @Param("currentUserId") Long currentUserId);

    int insertBranch(ZhwBranch branch);

    int updateBranch(ZhwBranch branch);

    /** 浏览量+1 */
    int incrementViewCount(Long branchId);

    /** 点赞数增减 */
    int updateLikeCount(@Param("branchId") Long branchId, @Param("delta") int delta);

    /** 评论数增减 */
    int updateCommentCount(@Param("branchId") Long branchId, @Param("delta") int delta);

    /** 子分支数增减 */
    int updateChildCount(@Param("branchId") Long branchId, @Param("delta") int delta);

    /** 统计故事中某用户的分支数 */
    int countUserBranchesInStory(@Param("storyId") Long storyId, @Param("userId") Long userId);

    /** 统计用户发布的分支总数 */
    int countPublishedByUser(@Param("userId") Long userId);

    /** 统计用户收到的续写数（其他人在自己分支下的续写） */
    int countReceivedContinuations(@Param("userId") Long userId);

    /** 统计用户所有分支收到的点赞总数 */
    long sumLikesReceivedByUser(@Param("userId") Long userId);

    /** 一次性查出某故事的全部分支（供导出/树构建用） */
    List<ZhwBranch> selectAllBranchesByStoryId(Long storyId);
}
