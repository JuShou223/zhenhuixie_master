package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ZhwUserMapper {

    ZhwUser selectByUserName(String userName);

    ZhwUser selectByPhone(String phone);

    ZhwUser selectById(Long userId);

    int insert(ZhwUser user);

    int update(ZhwUser user);

    /** 管理端分页列表（含统计数据 + 高级筛选 + 排序） */
    List<ZhwUser> selectList(@Param("keyword") String keyword, @Param("status") String status,
                              @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,
                              @Param("orderBy") String orderBy);

    /** 更新用户状态 */
    int updateStatus(@Param("userId") Long userId, @Param("status") String status);

    /** 重置密码 */
    int resetPwd(@Param("userId") Long userId, @Param("password") String password);

    /** 记录登录信息 */
    int updateLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp);
}
