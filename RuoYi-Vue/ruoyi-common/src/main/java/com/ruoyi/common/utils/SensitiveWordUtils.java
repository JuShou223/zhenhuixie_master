package com.ruoyi.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器（DFA 算法）
 *
 * <p>从 classpath:sensitive-words/ 目录加载词库：
 * <ul>
 *   <li>key.txt — UTF-8 BOM，竖线分隔，约 14,000 词</li>
 *   <li>色情/暴恐/反动/民生/贪腐/其他词库.txt — GBK 编码，每行一词</li>
 * </ul>
 */
public class SensitiveWordUtils {

    private static Map<Character, Map> DFA = new HashMap<>();
    private static final char END_FLAG = '\0';

    /**
     * 词库文件（classpath 相对路径）
     *
     * <p>只加载色情/暴恐/反动三类合规相关词库。key.txt（约14,000通用词）以及
     * 民生/贪腐/其他词库对本平台（小说创作社区）误杀率过高——"弟子""江湖"等仙侠武侠
     * 常用写作词汇会被当成敏感词拦截，因此不加载。
     */
    private static final String[] WORD_FILES = {
        "/sensitive-words/色情词库.txt",
        "/sensitive-words/暴恐词库.txt",
        "/sensitive-words/反动词库.txt"
    };

    static {
        init();
    }

    @SuppressWarnings("unchecked")
    private static void init() {
        int count = 0;
        for (String filePath : WORD_FILES) {
            count += loadFile(filePath);
        }
        System.out.println("[SensitiveWordUtils] 已加载敏感词 " + count + " 个");
    }

    @SuppressWarnings("unchecked")
    private static int loadFile(String filePath) {
        boolean isKeyFile = filePath.endsWith("key.txt");
        Charset charset = isKeyFile ? StandardCharsets.UTF_8 : Charset.forName("GBK");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        SensitiveWordUtils.class.getResourceAsStream(filePath),
                        charset))) {
            if (reader == null) {
                System.err.println("[SensitiveWordUtils] 词库文件未找到: " + filePath);
                return 0;
            }

            int count = 0;
            if (isKeyFile) {
                // key.txt: 单行，竖线分隔
                String line = reader.readLine();
                if (line != null) {
                    // 去掉 UTF-8 BOM 头（﻿）
                    if (line.startsWith("﻿")) {
                        line = line.substring(1);
                    }
                    for (String word : line.split("\\|")) {
                        word = word.trim();
                        if (!word.isEmpty() && word.length() >= 1) {
                            addWord(word);
                            count++;
                        }
                    }
                }
            } else {
                // 分类词库：每行一词，GBK 编码
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    addWord(line);
                    count++;
                }
            }
            return count;
        } catch (IOException e) {
            System.err.println("[SensitiveWordUtils] 加载词库失败 " + filePath + ": " + e.getMessage());
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private static void addWord(String word) {
        Map<Character, Map> current = DFA;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Map<Character, Map> child = current.get(c);
            if (child == null) {
                child = new HashMap<>();
                current.put(c, child);
            }
            current = child;
        }
        current.put(END_FLAG, null);
    }

    /**
     * 检查文本是否包含敏感词
     */
    @SuppressWarnings("unchecked")
    public static boolean containsSensitiveWord(String text) {
        if (StringUtils.isEmpty(text) || DFA.isEmpty()) {
            return false;
        }
        int len = text.length();
        for (int i = 0; i < len; i++) {
            Map<Character, Map> current = DFA;
            for (int j = i; j < len; j++) {
                char c = text.charAt(j);
                Map<Character, Map> child = current.get(c);
                if (child == null) {
                    break;
                }
                if (child.containsKey(END_FLAG)) {
                    return true;
                }
                current = child;
            }
        }
        return false;
    }

    /**
     * 过滤敏感词，替换为指定字符
     */
    @SuppressWarnings("unchecked")
    public static String filterSensitiveWord(String text, char replacement) {
        if (StringUtils.isEmpty(text) || DFA.isEmpty()) {
            return text;
        }
        char[] chars = text.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            Map<Character, Map> current = DFA;
            int matchEnd = -1;
            for (int j = i; j < len; j++) {
                char c = chars[j];
                Map<Character, Map> child = current.get(c);
                if (child == null) {
                    break;
                }
                if (child.containsKey(END_FLAG)) {
                    matchEnd = j;
                }
                current = child;
            }
            if (matchEnd >= 0) {
                for (int k = i; k <= matchEnd; k++) {
                    chars[k] = replacement;
                }
                i = matchEnd;
            }
        }
        return new String(chars);
    }

    /**
     * 从外部词库重新加载 DFA（用于从数据库刷新）
     */
    @SuppressWarnings("unchecked")
    public static void reload(java.util.Collection<String> words) {
        synchronized (DFA) {
            DFA.clear();
            int count = 0;
            for (String w : words) {
                if (w != null && !w.trim().isEmpty()) {
                    addWord(w.trim());
                    count++;
                }
            }
            // 同时从 classpath 词库再合并一份（防止数据库词库不全）
            init();
            System.out.println("[SensitiveWordUtils] 已重新加载敏感词：DB " + count + " 个 + classpath 文件");
        }
    }

    /**
     * 过滤敏感词，替换为 *
     */
    public static String filterSensitiveWord(String text) {
        return filterSensitiveWord(text, '*');
    }
}
