package com.ruoyi.web.controller.story;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SensitiveWordUtils;
import com.ruoyi.system.domain.ZhwUser;
import com.ruoyi.system.mapper.ZhwBranchMapper;
import com.ruoyi.system.service.IZhwNotificationService;
import com.ruoyi.system.service.IZhwStoryService;
import com.ruoyi.system.service.IZhwTopicService;
import com.ruoyi.system.service.IZhwUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户主页接口")
@RestController
@RequestMapping("/zhw/user")
public class ZhwUserController extends BaseController {

    @Autowired
    private IZhwUserService userService;

    @Autowired
    private IZhwNotificationService notificationService;

    @Autowired
    private ZhwBranchMapper branchMapper;

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private IZhwTopicService topicService;

    @Operation(summary = "我的主页数据", description = "返回用户基本信息 + 各类未读角标")
    @GetMapping("/profile")
    public AjaxResult profile() {
        Long userId = getUserId();
        ZhwUser user = userService.selectById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("userName", user.getUserName());
        userMap.put("nickName", user.getNickName());
        userMap.put("avatar", user.getAvatar());
        userMap.put("remark", user.getRemark());
        userMap.put("score", branchMapper.sumLikesReceivedByUser(userId));
        userMap.put("storyCount", branchMapper.countPublishedByUser(userId));
        userMap.put("branchCount", branchMapper.countReceivedContinuations(userId));

        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> badge = notificationService.countUnreadByCategory(userId);
        badge.put("topic", topicService.countActiveTopics());

        result.put("user", userMap);
        result.put("badge", badge);
        return success(result);
    }

    @Operation(summary = "编辑个人资料", description = "可更新：nickName、avatar、remark（个人签名）")
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody ZhwUser user) {
        ZhwUser updateUser = new ZhwUser();
        updateUser.setUserId(getUserId());

        if (user.getNickName() != null && !user.getNickName().isBlank()) {
            updateUser.setNickName(user.getNickName());
        }
        if (user.getAvatar() != null) {
            updateUser.setAvatar(user.getAvatar());
        }
        if (user.getRemark() != null) {
            updateUser.setRemark(user.getRemark());
        }
        if ((updateUser.getNickName() != null && SensitiveWordUtils.containsSensitiveWord(updateUser.getNickName()))
                || (updateUser.getRemark() != null && SensitiveWordUtils.containsSensitiveWord(updateUser.getRemark()))) {
            return error("内容包含敏感词，请修改后重试");
        }

        int rows = userService.updateUser(updateUser);
        return rows > 0 ? success("保存成功") : error("保存失败");
    }

    @Operation(summary = "我发布的故事列表")
    @GetMapping("/stories")
    public AjaxResult myStories() {
        Long userId = getUserId();
        return success(storyService.listUserStories(userId, userId));
    }
}
