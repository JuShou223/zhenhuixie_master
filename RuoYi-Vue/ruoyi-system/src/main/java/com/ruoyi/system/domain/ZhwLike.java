package com.ruoyi.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 点赞表 zhw_like
 */
@Schema(description = "点赞")
public class ZhwLike {
    private static final long serialVersionUID = 1L;

    @Schema(description = "点赞ID")
    private Long likeId;
    @Schema(description = "目标ID（故事/分支/评论的ID）")
    private Long targetId;
    @Schema(description = "目标类型（0=故事 1=分支 2=评论）")
    private Integer targetType;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "点赞时间")
    private Date createTime;

    public Long getLikeId() { return likeId; }
    public void setLikeId(Long likeId) { this.likeId = likeId; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getTargetType() { return targetType; }
    public void setTargetType(Integer targetType) { this.targetType = targetType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
