package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 评论表 zhw_comment
 */
@Schema(description = "评论")
public class ZhwComment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    private Long commentId;
    @Schema(description = "所属故事ID")
    private Long storyId;
    @Schema(description = "所属分支ID（0=故事评论）")
    private Long branchId;
    @Schema(description = "父评论ID（0=根评论）")
    private Long parentId;
    @Schema(description = "回复的目标用户ID")
    private Long replyToUserId;
    @Schema(description = "评论内容（500字以内）")
    private String content;
    @Schema(description = "评论者用户ID")
    private Long userId;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "删除标志（0存在 2删除）")
    private String delFlag;

    @Schema(description = "评论者昵称（非持久化）")
    private String authorNickName;
    @Schema(description = "评论者头像（非持久化）")
    private String authorAvatar;
    @Schema(description = "被回复者昵称（非持久化）")
    private String replyToNickName;
    @Schema(description = "当前用户是否已点赞（非持久化）")
    private Boolean liked;

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getReplyToUserId() { return replyToUserId; }
    public void setReplyToUserId(Long replyToUserId) { this.replyToUserId = replyToUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getAuthorNickName() { return authorNickName; }
    public void setAuthorNickName(String authorNickName) { this.authorNickName = authorNickName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getReplyToNickName() { return replyToNickName; }
    public void setReplyToNickName(String replyToNickName) { this.replyToNickName = replyToNickName; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }
}
