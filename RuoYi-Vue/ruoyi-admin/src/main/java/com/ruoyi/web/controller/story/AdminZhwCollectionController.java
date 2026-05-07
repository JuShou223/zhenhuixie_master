package com.ruoyi.web.controller.story;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ZhwStory;
import com.ruoyi.system.service.IZhwStoryService;
import com.ruoyi.system.service.ZhwExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-精选收集")
@RestController
@RequestMapping("/admin/zhw/collection")
public class AdminZhwCollectionController extends BaseController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IZhwStoryService storyService;

    @Autowired
    private ZhwExportService exportService;

    @Operation(summary = "标记/取消精选", description = "collected=true 标记，false 取消")
    @PreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{storyId}/collect")
    public AjaxResult collect(@PathVariable Long storyId,
                               @RequestParam(defaultValue = "true") boolean collected) {
        storyService.markCollected(storyId, collected);
        return success(collected ? "已加入精选" : "已取消精选");
    }

    @Operation(summary = "批量取消精选")
    @PreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/batch/uncollect")
    public AjaxResult batchUncollect(@RequestBody Map<String, String> params) {
        String ids = params.get("ids");
        int count = 0;
        for (String id : ids.split(",")) {
            storyService.markCollected(Long.valueOf(id), false);
            count++;
        }
        return success("已取消 " + count + " 个精选");
    }

    @Operation(summary = "精选故事列表")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    public AjaxResult list() {
        return success(storyService.listCollectedStories());
    }

    @Operation(summary = "导出精选故事 JSON（含完整分支树）")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCollection() throws Exception {
        List<ZhwStory> stories = storyService.listCollectedStories();
        return buildJsonDownload("zhw_collection", stories);
    }

    @Operation(summary = "按热度导出故事 JSON（minBranches=分支数下限，limit=最多条数）")
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/export/hot")
    public ResponseEntity<byte[]> exportHot(
            @RequestParam(defaultValue = "3") int minBranches,
            @RequestParam(defaultValue = "50") int limit) throws Exception {
        List<ZhwStory> stories = storyService.listHotStoriesForExport(minBranches, limit);
        return buildJsonDownload("zhw_hot_stories", stories);
    }

    private ResponseEntity<byte[]> buildJsonDownload(String prefix, List<ZhwStory> stories) throws Exception {
        List<Map<String, Object>> data = exportService.buildStoryTreeExport(stories);
        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
        String filename = prefix + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
