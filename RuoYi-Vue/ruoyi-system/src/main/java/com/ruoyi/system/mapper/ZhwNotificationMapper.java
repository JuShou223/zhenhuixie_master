package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwNotification;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ZhwNotificationMapper {

    /** 查询用户通知列表，notifType=-1查全部 */
    List<ZhwNotification> selectNotificationList(@Param("userId") Long userId,
                                                  @Param("notifType") Integer notifType);

    int insertNotification(ZhwNotification notification);

    /** 标记已读（notifId=-1全部已读） */
    int markAsRead(@Param("userId") Long userId, @Param("notifId") Long notifId);

    /** 查询未读数量 */
    int countUnread(@Param("userId") Long userId);

    /** 按类型查询未读数量 */
    int countUnreadByType(@Param("userId") Long userId, @Param("notifType") Integer notifType);
}
