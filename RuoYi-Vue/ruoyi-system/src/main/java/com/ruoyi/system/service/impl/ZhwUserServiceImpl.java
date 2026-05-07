package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwUser;
import com.ruoyi.system.mapper.ZhwUserMapper;
import com.ruoyi.system.service.IZhwUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZhwUserServiceImpl implements IZhwUserService {

    @Autowired
    private ZhwUserMapper mapper;

    @Override
    public ZhwUser selectByUserName(String userName) {
        return mapper.selectByUserName(userName);
    }

    @Override
    public ZhwUser selectById(Long userId) {
        return mapper.selectById(userId);
    }

    @Override
    public Long register(ZhwUser user) {
        mapper.insert(user);
        return user.getUserId();
    }

    @Override
    public int updateUser(ZhwUser user) {
        return mapper.update(user);
    }
}
