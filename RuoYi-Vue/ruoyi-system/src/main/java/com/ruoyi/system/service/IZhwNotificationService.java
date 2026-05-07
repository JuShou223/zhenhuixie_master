package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwNotification;
import java.util.List;
import java.util.Map;

public interface IZhwNotificationService {

    /** 通知列表（notifType=-1查全部） */
    List<ZhwNotification> listNotifications(Long userId, Integer notifType);

    /** 标记已读（notifId=-1全部已读） */
    void markAsRead(Long userId, Long notifId);

    /** 未读总数 */
    int countUnread(Long userId);

    /**
     * 各类未读数量（用于我的主页角标）
     * key: interact（互动消息）, mark（标记更新）, topic（话题）
     */
    Map<String, Integer> countUnreadByCategory(Long userId);

    /** 发送通知（内部调用，content=动作文字摘要，被赞传null） */
    void sendNotification(Long userId, Long senderId, Integer notifType,
                          Long targetId, Integer targetType, Long storyId, String content);
}
