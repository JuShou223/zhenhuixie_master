package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.IZhwStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "精选故事（公开）")
@RestController
@RequestMapping("/zhw/featured")
public class ZhwFeaturedController extends BaseController {

    @Autowired
    private IZhwStoryService storyService;

    @Operation(summary = "精选故事列表（按热度排序）")
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(storyService.listCollectedStories());
    }
}
