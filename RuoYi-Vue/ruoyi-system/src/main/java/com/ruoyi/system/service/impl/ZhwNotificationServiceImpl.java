package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwNotification;
import com.ruoyi.system.mapper.ZhwNotificationMapper;
import com.ruoyi.system.service.IZhwNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZhwNotificationServiceImpl implements IZhwNotificationService {

    @Autowired
    private ZhwNotificationMapper notificationMapper;

    @Override
    public List<ZhwNotification> listNotifications(Long userId, Integer notifType) {
        return notificationMapper.selectNotificationList(userId, notifType);
    }

    @Override
    public void markAsRead(Long userId, Long notifId) {
        notificationMapper.markAsRead(userId, notifId);
    }

    @Override
    public int countUnread(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public Map<String, Integer> countUnreadByCategory(Long userId) {
        Map<String, Integer> result = new HashMap<>();
        // 互动消息：被赞(0)+被评论(1)+被回复(2)+被续写(3)
        int interact = notificationMapper.countUnreadByType(userId, 0)
                + notificationMapper.countUnreadByType(userId, 1)
                + notificationMapper.countUnreadByType(userId, 2)
                + notificationMapper.countUnreadByType(userId, 3);
        result.put("interact", interact);
        result.put("total", notificationMapper.countUnread(userId));
        return result;
    }

    @Override
    public void sendNotification(Long userId, Long senderId, Integer notifType,
                                  Long targetId, Integer targetType, Long storyId, String content) {
        if (userId == null || userId.equals(senderId)) return;

        ZhwNotification notif = new ZhwNotification();
        notif.setUserId(userId);
        notif.setSenderId(senderId);
        notif.setNotifType(notifType);
        notif.setTargetId(targetId);
        notif.setTargetType(targetType);
        notif.setStoryId(storyId != null ? storyId : 0L);
        if (content != null && content.length() > 100) {
            notif.setContent(content.substring(0, 100));
        } else {
            notif.setContent(content);
        }
        notificationMapper.insertNotification(notif);
    }
}
