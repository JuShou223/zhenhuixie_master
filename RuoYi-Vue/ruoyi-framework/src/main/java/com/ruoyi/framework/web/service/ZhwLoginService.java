package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.ZhwUser;
import com.ruoyi.system.mapper.ZhwUserMapper;
import com.ruoyi.system.service.IZhwUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 社区用户登录服务（走 zhw_user 表，绕过 Spring Security AuthenticationManager）
 */
@Component
public class ZhwLoginService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IZhwUserService zhwUserService;

    @Autowired
    private ZhwUserMapper zhwUserMapper;

    /**
     * 社区用户登录
     */
    public String login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        ZhwUser zhwUser = zhwUserService.selectByUserName(username);
        if (zhwUser == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!SecurityUtils.matchesPassword(password, zhwUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if ("1".equals(zhwUser.getStatus())) {
            throw new RuntimeException("账号已被停用");
        }

        // 构建 LoginUser（需要 SysUser 包装）
        SysUser sysUser = new SysUser();
        sysUser.setUserId(zhwUser.getUserId());
        sysUser.setUserName(zhwUser.getUserName());
        sysUser.setPassword(zhwUser.getPassword());

        LoginUser loginUser = new LoginUser(sysUser, Collections.emptySet());
        loginUser.setUserId(zhwUser.getUserId());
        loginUser.setIpaddr(IpUtils.getIpAddr());
        loginUser.setLoginTime(DateUtils.getNowDate().getTime());

        // 记录最后登录时间和IP
        zhwUserMapper.updateLoginInfo(zhwUser.getUserId(), IpUtils.getIpAddr());

        return tokenService.createToken(loginUser);
    }
}
