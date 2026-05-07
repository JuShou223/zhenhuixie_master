package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwSensitiveWord;

import java.util.List;

public interface ZhwSensitiveWordMapper {

    List<ZhwSensitiveWord> selectList(ZhwSensitiveWord query);

    ZhwSensitiveWord selectById(Long id);

    int insert(ZhwSensitiveWord word);

    int update(ZhwSensitiveWord word);

    int delete(Long id);

    /** 查询所有启用中的敏感词 */
    List<String> selectAllWords();
}
