package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 首页活动横幅表 zhw_home_banner
 */
@Schema(description = "首页活动横幅")
public class ZhwHomeBanner extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "横幅ID")
    private Long bannerId;
    @Schema(description = "图片URL")
    private String image;
    @Schema(description = "点击跳转的小程序内部路径，可为空")
    private String linkUrl;
    @Schema(description = "排序，越小越靠前")
    private Integer sortOrder;
    @Schema(description = "状态（0启用 1禁用）")
    private String status;

    public Long getBannerId() { return bannerId; }
    public void setBannerId(Long bannerId) { this.bannerId = bannerId; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
