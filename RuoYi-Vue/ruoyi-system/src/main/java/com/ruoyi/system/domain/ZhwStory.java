package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 故事主表 zhw_story
 */
@Schema(description = "故事")
public class ZhwStory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "故事ID")
    private Long storyId;
    @Schema(description = "故事标题")
    private String title;
    @Schema(description = "封面图URL")
    private String cover;
    @Schema(description = "故事设定内容（15~300字）")
    private String settingContent;
    @Schema(description = "作者用户ID")
    private Long userId;
    @Schema(description = "浏览数")
    private Integer viewCount;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "评论数")
    private Integer commentCount;
    @Schema(description = "分支数")
    private Integer branchCount;
    @Schema(description = "参与人数")
    private Integer participantCount;
    @Schema(description = "热度值（独立参与人数×30 + 分支数×30 + 评论数×3 + 点赞数×1）")
    private Long heatScore;
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    @Schema(description = "删除标志（0存在 2删除）")
    private String delFlag;

    @Schema(description = "作者昵称（非持久化）")
    private String authorNickName;
    @Schema(description = "作者头像（非持久化）")
    private String authorAvatar;
    @Schema(description = "当前用户是否已点赞（非持久化）")
    private Boolean liked;
    @Schema(description = "当前用户是否已标记（非持久化）")
    private Boolean marked;
    @Schema(description = "话题名称列表，逗号分隔（非持久化）")
    private String topicNames;
    @Schema(description = "是否精选（0否 1是）")
    private Integer isCollected;

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getSettingContent() { return settingContent; }
    public void setSettingContent(String settingContent) { this.settingContent = settingContent; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Integer getBranchCount() { return branchCount; }
    public void setBranchCount(Integer branchCount) { this.branchCount = branchCount; }

    public Integer getParticipantCount() { return participantCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }

    public Long getHeatScore() { return heatScore; }
    public void setHeatScore(Long heatScore) { this.heatScore = heatScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getAuthorNickName() { return authorNickName; }
    public void setAuthorNickName(String authorNickName) { this.authorNickName = authorNickName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }

    public Boolean getMarked() { return marked; }
    public void setMarked(Boolean marked) { this.marked = marked; }

    public String getTopicNames() { return topicNames; }
    public void setTopicNames(String topicNames) { this.topicNames = topicNames; }

    public Integer getIsCollected() { return isCollected; }
    public void setIsCollected(Integer isCollected) { this.isCollected = isCollected; }
}
