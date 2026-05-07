package com.ruoyi.web.controller.story;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.ZhwLoginService;
import com.ruoyi.system.domain.ZhwUser;
import com.ruoyi.system.service.IZhwUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "社区用户认证")
@RestController
@RequestMapping("/zhw")
public class ZhwLoginController extends BaseController {

    @Autowired
    private ZhwLoginService loginService;

    @Autowired
    private IZhwUserService zhwUserService;

    @Operation(summary = "社区用户登录")
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        try {
            String token = loginService.login(username, password);
            return AjaxResult.success("操作成功", token);
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @Operation(summary = "社区用户注册")
    @Anonymous
    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickName = body.getOrDefault("nickName", username);

        if (StringUtils.isEmpty(username)) {
            return error("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            return error("密码不能为空");
        }
        if (username.length() < 4 || username.length() > 20) {
            return error("用户名长度必须在4到20个字符之间");
        }
        if (password.length() < 6 || password.length() > 20) {
            return error("密码长度必须在6到20个字符之间");
        }
        if (zhwUserService.selectByUserName(username) != null) {
            return error("用户名已存在");
        }

        ZhwUser user = new ZhwUser();
        user.setUserName(username);
        user.setNickName(nickName);
        user.setPassword(SecurityUtils.encryptPassword(password));
        Long userId = zhwUserService.register(user);
        return AjaxResult.success("操作成功", userId);
    }
}
