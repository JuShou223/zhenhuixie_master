package com.ruoyi.web.controller.story;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SensitiveWordUtils;
import com.ruoyi.system.domain.ZhwSensitiveWord;
import com.ruoyi.system.service.IZhwSensitiveWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理端-敏感词管理")
@RestController
@RequestMapping("/admin/zhw/sensitive-word")
public class AdminZhwSensitiveWordController extends BaseController {

    @Autowired
    private IZhwSensitiveWordService sensitiveWordService;

    @Operation(summary = "敏感词列表（分页）")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String word,
                               @RequestParam(required = false) String category,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        ZhwSensitiveWord query = new ZhwSensitiveWord();
        query.setWord(word);
        query.setCategory(category);
        PageHelper.startPage(pageNum, pageSize);
        return getDataTable(sensitiveWordService.listWords(query));
    }

    @Operation(summary = "敏感词详情")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        return success(sensitiveWordService.getWord(id));
    }

    @Operation(summary = "新增敏感词")
    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping
    public AjaxResult create(@RequestBody ZhwSensitiveWord word) {
        if (word.getWord() == null || word.getWord().isBlank()) {
            return error("敏感词不能为空");
        }
        word.setCreateBy(getUsername());
        Long id = sensitiveWordService.addWord(word);
        return AjaxResult.success("新增成功", id);
    }

    @Operation(summary = "编辑敏感词")
    @PreAuthorize("@ss.hasRole('admin')")
    @PutMapping
    public AjaxResult update(@RequestBody ZhwSensitiveWord word) {
        if (word.getId() == null) {
            return error("ID不能为空");
        }
        word.setUpdateBy(getUsername());
        sensitiveWordService.updateWord(word);
        return success("修改成功");
    }

    @Operation(summary = "删除敏感词")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        sensitiveWordService.deleteWord(id);
        return success("删除成功");
    }

    @Operation(summary = "批量删除敏感词")
    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchDelete(@PathVariable String ids) {
        int count = 0;
        for (String id : ids.split(",")) {
            sensitiveWordService.deleteWord(Long.valueOf(id));
            count++;
        }
        return success("已删除 " + count + " 个敏感词");
    }

    @Operation(summary = "从数据库刷新敏感词缓存")
    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/reload")
    public AjaxResult reload() {
        List<String> words = sensitiveWordService.getAllActiveWords();
        SensitiveWordUtils.reload(words);
        return success("已刷新 " + words.size() + " 个敏感词");
    }
}
