package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 话题挑战表 zhw_topic
 */
@Schema(description = "话题挑战")
public class ZhwTopic extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "话题ID")
    private Long topicId;
    @Schema(description = "话题标题")
    private String title;
    @Schema(description = "话题横幅图URL")
    private String banner;
    @Schema(description = "话题公告/简介")
    private String announcement;
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Schema(description = "状态（0进行中 1已结束）")
    private String status;
    @Schema(description = "删除标志（0存在 2删除）")
    private String delFlag;

    @Schema(description = "该话题下的故事数量（非持久化）")
    private Integer storyCount;
    @Schema(description = "是否有新活动（非持久化）")
    private Boolean hasNew;

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }

    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public Integer getStoryCount() { return storyCount; }
    public void setStoryCount(Integer storyCount) { this.storyCount = storyCount; }

    public Boolean getHasNew() { return hasNew; }
    public void setHasNew(Boolean hasNew) { this.hasNew = hasNew; }
}
