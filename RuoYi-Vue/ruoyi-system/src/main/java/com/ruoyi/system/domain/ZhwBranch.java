package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 故事分支表 zhw_branch
 */
@Schema(description = "故事分支")
public class ZhwBranch extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分支ID")
    private Long branchId;
    @Schema(description = "所属故事ID")
    private Long storyId;
    @Schema(description = "父节点ID（0=直接续写故事设定）")
    private Long parentId;
    @Schema(description = "父节点类型（0=故事设定 1=分支）")
    private Integer parentType;
    @Schema(description = "章节序号（系统自动计算）")
    private Integer chapterNum;
    @Schema(description = "用户自定义章节标题")
    private String chapterTitle;
    @Schema(description = "分支正文内容（15~300字）")
    private String content;
    @Schema(description = "作者用户ID")
    private Long userId;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "浏览数")
    private Integer viewCount;
    @Schema(description = "评论数")
    private Integer commentCount;
    @Schema(description = "子分支数")
    private Integer childCount;
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
    @Schema(description = "父内容摘要，前50字（非持久化）")
    private String parentContentPreview;
    @Schema(description = "所属故事标题（非持久化）")
    private String storyTitle;

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Integer getParentType() { return parentType; }
    public void setParentType(Integer parentType) { this.parentType = parentType; }

    public Integer getChapterNum() { return chapterNum; }
    public void setChapterNum(Integer chapterNum) { this.chapterNum = chapterNum; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Integer getChildCount() { return childCount; }
    public void setChildCount(Integer childCount) { this.childCount = childCount; }

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

    public String getParentContentPreview() { return parentContentPreview; }
    public void setParentContentPreview(String parentContentPreview) { this.parentContentPreview = parentContentPreview; }

    public String getStoryTitle() { return storyTitle; }
    public void setStoryTitle(String storyTitle) { this.storyTitle = storyTitle; }
}
