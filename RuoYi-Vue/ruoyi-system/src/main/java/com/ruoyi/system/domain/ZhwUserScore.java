package com.ruoyi.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 用户故事积分表 zhw_user_score
 * 积分规则：被赞x1 被评论x3 被标记x20 被他人续写x30
 */
@Schema(description = "用户故事积分")
public class ZhwUserScore {
    private static final long serialVersionUID = 1L;

    @Schema(description = "积分记录ID")
    private Long scoreId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "故事ID")
    private Long storyId;
    @Schema(description = "积分（被赞×1 被评论×3 被标记×20 被续写×30）")
    private Integer score;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "用户昵称（非持久化）")
    private String nickName;
    @Schema(description = "用户头像（非持久化）")
    private String avatar;
    @Schema(description = "排名（非持久化）")
    private Integer rank;

    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
}
