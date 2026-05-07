package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwBranch;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.mapper.ZhwBranchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 故事树导出服务：把故事列表构建成含完整分支树的 JSON 可序列化结构
 */
@Service
public class ZhwExportService {

    @Autowired
    private ZhwBranchMapper branchMapper;

    /**
     * 把故事列表构建为导出结构（含完整分支树）
     */
    public List<Map<String, Object>> buildStoryTreeExport(List<ZhwStory> stories) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ZhwStory story : stories) {
            List<ZhwBranch> allBranches = branchMapper.selectAllBranchesByStoryId(story.getStoryId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("storyId", story.getStoryId());
            item.put("title", story.getTitle());
            item.put("settingContent", story.getSettingContent());
            item.put("authorNickName", story.getAuthorNickName());
            item.put("branchCount", story.getBranchCount());
            item.put("participantCount", story.getParticipantCount());
            item.put("likeCount", story.getLikeCount());
            item.put("commentCount", story.getCommentCount());
            item.put("heatScore", story.getHeatScore());
            item.put("createTime", story.getCreateTime());
            item.put("branches", buildBranchTree(allBranches, 0L));
            result.add(item);
        }
        return result;
    }

    /**
     * 把扁平 branch 列表递归构建为树形结构
     */
    private List<Map<String, Object>> buildBranchTree(List<ZhwBranch> all, Long parentId) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        // 按 parentId 分组，避免每次全扫
        Map<Long, List<ZhwBranch>> byParent = all.stream()
                .collect(Collectors.groupingBy(ZhwBranch::getParentId));

        List<ZhwBranch> children = byParent.getOrDefault(parentId, Collections.emptyList());
        for (ZhwBranch b : children) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("branchId", b.getBranchId());
            node.put("parentId", b.getParentId());
            node.put("chapterNum", b.getChapterNum());
            node.put("chapterTitle", b.getChapterTitle());
            node.put("content", b.getContent());
            node.put("authorNickName", b.getAuthorNickName());
            node.put("likeCount", b.getLikeCount());
            node.put("childCount", b.getChildCount());
            node.put("createTime", b.getCreateTime());
            node.put("children", buildBranchTree(all, b.getBranchId()));
            nodes.add(node);
        }
        return nodes;
    }
}
