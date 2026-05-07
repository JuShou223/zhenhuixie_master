package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwBranch;
import java.util.List;

public interface IZhwBranchService {

    /** 分支详情（同时增加浏览量） */
    ZhwBranch getBranchDetail(Long branchId, Long currentUserId);

    /** 故事一级分支列表 */
    List<ZhwBranch> listFirstLevelBranches(Long storyId, Long currentUserId);

    /** 某分支的子分支列表 */
    List<ZhwBranch> listChildBranches(Long parentId, Long currentUserId);

    /** 创建分支（写新分支/续写） */
    Long createBranch(ZhwBranch branch);
}
