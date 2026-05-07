package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "社区用户")
public class ZhwUser {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个人签名")
    private String remark;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别（0未知 1男 2女）")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "生日")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后登录时间")
    private Date loginTime;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    // 以下为统计字段（非持久化，来自 JOIN 或子查询）

    @Schema(description = "总积分")
    private Integer score;

    @Schema(description = "故事数")
    private Integer storyCount;

    @Schema(description = "分支数")
    private Integer branchCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "获赞数")
    private Integer likeCount;

    // --- getters and setters ---

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
    public String getLoginIp() { return loginIp; }
    public void setLoginIp(String loginIp) { this.loginIp = loginIp; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getStoryCount() { return storyCount; }
    public void setStoryCount(Integer storyCount) { this.storyCount = storyCount; }
    public Integer getBranchCount() { return branchCount; }
    public void setBranchCount(Integer branchCount) { this.branchCount = branchCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
}
