package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwUser;

public interface IZhwUserService {

    ZhwUser selectByUserName(String userName);

    ZhwUser selectById(Long userId);

    Long register(ZhwUser user);

    int updateUser(ZhwUser user);
}
