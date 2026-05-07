package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwSensitiveWord;
import java.util.List;

public interface IZhwSensitiveWordService {

    List<ZhwSensitiveWord> listWords(ZhwSensitiveWord query);

    ZhwSensitiveWord getWord(Long id);

    Long addWord(ZhwSensitiveWord word);

    void updateWord(ZhwSensitiveWord word);

    void deleteWord(Long id);

    /** 获取所有敏感词用于刷新 DFA 缓存 */
    List<String> getAllActiveWords();
}
