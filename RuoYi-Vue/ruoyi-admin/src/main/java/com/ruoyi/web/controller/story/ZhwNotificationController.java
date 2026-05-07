package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.IZhwNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "消息通知接口")
@RestController
@RequestMapping("/zhw/notification")
public class ZhwNotificationController extends BaseController {

    @Autowired
    private IZhwNotificationService notificationService;

    @Operation(summary = "通知列表", description = "notifType: -1=全部 0=被赞 1=被评论 2=被回复 3=被续写")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(defaultValue = "-1") Integer notifType,
                            @RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "20") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(notificationService.listNotifications(getUserId(), notifType));
    }

    @Operation(summary = "未读通知总数")
    @GetMapping("/unread/count")
    public AjaxResult unreadCount() {
        return success(notificationService.countUnread(getUserId()));
    }

    @Operation(summary = "各类未读角标（用于我的主页）")
    @GetMapping("/unread/badge")
    public AjaxResult unreadBadge() {
        return success(notificationService.countUnreadByCategory(getUserId()));
    }

    @Operation(summary = "标记已读", description = "Body: { notifId }，-1=全部已读，>0=单条已读")
    @PutMapping("/read")
    public AjaxResult markRead(@RequestBody Map<String, Object> body) {
        Long notifId = body.containsKey("notifId")
                ? Long.valueOf(body.get("notifId").toString()) : -1L;
        notificationService.markAsRead(getUserId(), notifId);
        return success();
    }
}
