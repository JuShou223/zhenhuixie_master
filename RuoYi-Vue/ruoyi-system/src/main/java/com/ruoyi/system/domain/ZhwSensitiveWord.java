package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "敏感词")
public class ZhwSensitiveWord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "敏感词ID")
    private Long id;

    @Schema(description = "敏感词内容")
    private String word;

    @Schema(description = "分类")
    private String category;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
