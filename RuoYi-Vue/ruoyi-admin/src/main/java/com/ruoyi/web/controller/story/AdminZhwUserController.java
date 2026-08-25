package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.ZhwUser;
import com.ruoyi.system.mapper.ZhwUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-用户管理")
@RestController
@RequestMapping("/admin/zhw/user")
public class AdminZhwUserController extends BaseController {

    private static final String DEFAULT_PASSWORD = "Zhw@123456";

    @Autowired
    private ZhwUserMapper zhwUserMapper;

    @Operation(summary = "用户列表（分页 + 统计 + 高级筛选 + 排序）")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String orderBy,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        Date begin = DateUtils.parseDate(beginTime);
        Date end = DateUtils.parseDate(endTime);
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(zhwUserMapper.selectList(keyword, status, begin, end, orderBy));
    }

    @Operation(summary = "用户详情（含统计数据）")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{userId}")
    public AjaxResult detail(@PathVariable Long userId) {
        ZhwUser user = zhwUserMapper.selectById(userId);
        if (user == null) return error("用户不存在");
        user.setPassword(null);
        return success(user);
    }

    @Operation(summary = "停用/启用用户")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{userId}/status")
    public AjaxResult updateStatus(@PathVariable Long userId, @RequestParam String status) {
        if (!"0".equals(status) && !"1".equals(status)) return error("status参数只能为0或1");
        zhwUserMapper.updateStatus(userId, status);
        return success("操作成功");
    }

    @Operation(summary = "批量更新用户状态")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/status")
    public AjaxResult batchUpdateStatus(@RequestBody Map<String, String> params) {
        String ids = params.get("ids");
        String status = params.get("status");
        if (!"0".equals(status) && !"1".equals(status)) return error("status参数只能为0或1");
        int count = 0;
        for (String id : ids.split(",")) {
            zhwUserMapper.updateStatus(Long.valueOf(id), status);
            count++;
        }
        return success("已" + ("1".equals(status) ? "停用" : "启用") + " " + count + " 个用户");
    }

    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE, isSaveResponseData = false)
    @PutMapping("/{userId}/reset-pwd")
    public AjaxResult resetPwd(@PathVariable Long userId) {
        String pwd = SecurityUtils.encryptPassword(DEFAULT_PASSWORD);
        zhwUserMapper.resetPwd(userId, pwd);
        return success("密码已重置为 " + DEFAULT_PASSWORD);
    }

    @Operation(summary = "导出用户列表")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT, isSaveResponseData = false)
    @GetMapping("/export")
    public AjaxResult export(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime,
                              @RequestParam(required = false) String orderBy) {
        Date begin = DateUtils.parseDate(beginTime);
        Date end = DateUtils.parseDate(endTime);
        List<ZhwUser> list = zhwUserMapper.selectList(keyword, status, begin, end, orderBy);
        for (ZhwUser u : list) u.setPassword(null);
        return success(list);
    }
}
