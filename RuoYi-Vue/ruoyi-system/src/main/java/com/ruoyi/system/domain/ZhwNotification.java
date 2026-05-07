package com.ruoyi.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 消息通知表 zhw_notification
 * notifType: 0=被赞 1=被评论 2=被回复 3=被续写
 */
@Schema(description = "消息通知")
public class ZhwNotification {
    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID")
    private Long notifId;
    @Schema(description = "接收通知的用户ID")
    private Long userId;
    @Schema(description = "触发通知的用户ID")
    private Long senderId;
    @Schema(description = "通知类型（0=被赞 1=被评论 2=被回复 3=被续写）")
    private Integer notifType;
    @Schema(description = "目标内容ID（故事/分支/评论的ID）")
    private Long targetId;
    @Schema(description = "目标类型（0=故事 1=分支 2=评论）")
    private Integer targetType;
    @Schema(description = "关联故事ID")
    private Long storyId;
    @Schema(description = "是否已读（0=未读 1=已读）")
    private Integer isRead;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "动作内容摘要（评论/回复/续写的文字，存库）")
    private String content;

    @Schema(description = "触发者昵称（非持久化）")
    private String senderNickName;
    @Schema(description = "触发者头像（非持久化）")
    private String senderAvatar;
    @Schema(description = "目标内容摘要（非持久化）")
    private String targetContentPreview;
    @Schema(description = "故事标题（非持久化）")
    private String storyTitle;

    public Long getNotifId() { return notifId; }
    public void setNotifId(Long notifId) { this.notifId = notifId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Integer getNotifType() { return notifType; }
    public void setNotifType(Integer notifType) { this.notifType = notifType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getTargetType() { return targetType; }
    public void setTargetType(Integer targetType) { this.targetType = targetType; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getSenderNickName() { return senderNickName; }
    public void setSenderNickName(String senderNickName) { this.senderNickName = senderNickName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetContentPreview() { return targetContentPreview; }
    public void setTargetContentPreview(String targetContentPreview) { this.targetContentPreview = targetContentPreview; }

    public String getStoryTitle() { return storyTitle; }
    public void setStoryTitle(String storyTitle) { this.storyTitle = storyTitle; }
}
