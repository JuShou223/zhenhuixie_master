package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwSensitiveWord;
import com.ruoyi.system.mapper.ZhwSensitiveWordMapper;
import com.ruoyi.system.service.IZhwSensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZhwSensitiveWordServiceImpl implements IZhwSensitiveWordService {

    @Autowired
    private ZhwSensitiveWordMapper mapper;

    @Override
    public List<ZhwSensitiveWord> listWords(ZhwSensitiveWord query) {
        return mapper.selectList(query);
    }

    @Override
    public ZhwSensitiveWord getWord(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public Long addWord(ZhwSensitiveWord word) {
        mapper.insert(word);
        return word.getId();
    }

    @Override
    public void updateWord(ZhwSensitiveWord word) {
        mapper.update(word);
    }

    @Override
    public void deleteWord(Long id) {
        mapper.delete(id);
    }

    @Override
    public List<String> getAllActiveWords() {
        return mapper.selectAllWords();
    }
}
