package com.ruoyi.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 标记/订阅表 zhw_mark
 */
@Schema(description = "标记/订阅")
public class ZhwMark {
    private static final long serialVersionUID = 1L;

    @Schema(description = "标记ID")
    private Long markId;
    @Schema(description = "故事ID")
    private Long storyId;
    @Schema(description = "标记的分支ID（0=标记故事设定本身）")
    private Long branchId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "最后阅读时间（用于判断是否有新内容）")
    private Date lastReadTime;
    @Schema(description = "标记时间")
    private Date createTime;

    @Schema(description = "故事标题（非持久化）")
    private String storyTitle;
    @Schema(description = "标记处作者昵称（非持久化）")
    private String targetAuthorNickName;
    @Schema(description = "标记内容摘要（非持久化）")
    private String targetContentPreview;
    @Schema(description = "章节名称（标记分支时有值，非持久化）")
    private String chapterName;
    @Schema(description = "是否有新内容（非持久化）")
    private Boolean hasUpdate;
    @Schema(description = "故事封面（非持久化）")
    private String cover;
    @Schema(description = "标记处作者头像（非持久化）")
    private String targetAuthorAvatar;

    public Long getMarkId() { return markId; }
    public void setMarkId(Long markId) { this.markId = markId; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Date getLastReadTime() { return lastReadTime; }
    public void setLastReadTime(Date lastReadTime) { this.lastReadTime = lastReadTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getStoryTitle() { return storyTitle; }
    public void setStoryTitle(String storyTitle) { this.storyTitle = storyTitle; }

    public String getTargetAuthorNickName() { return targetAuthorNickName; }
    public void setTargetAuthorNickName(String targetAuthorNickName) { this.targetAuthorNickName = targetAuthorNickName; }

    public String getTargetContentPreview() { return targetContentPreview; }
    public void setTargetContentPreview(String targetContentPreview) { this.targetContentPreview = targetContentPreview; }

    public String getChapterName() { return chapterName; }
    public void setChapterName(String chapterName) { this.chapterName = chapterName; }

    public Boolean getHasUpdate() { return hasUpdate; }
    public void setHasUpdate(Boolean hasUpdate) { this.hasUpdate = hasUpdate; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getTargetAuthorAvatar() { return targetAuthorAvatar; }
    public void setTargetAuthorAvatar(String targetAuthorAvatar) { this.targetAuthorAvatar = targetAuthorAvatar; }
}
