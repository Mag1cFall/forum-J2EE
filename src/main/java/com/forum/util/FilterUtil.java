package com.forum.util;

import com.forum.dao.SensitiveWordDao;
import com.forum.dao.impl.SensitiveWordDaoImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 敏感词过滤工具类
 * @author 陈羽飞
 * @author 任航瑞 (修改为从数据库加载)
 * @date 2025-07-04
 */
public class FilterUtil {

    private static final List<String> sensitiveWords = new ArrayList<>(); // 静态列表，存储所有敏感词

    static { // 静态块，在类加载时从数据库初始化敏感词列表
        // 从数据库加载敏感词
        SensitiveWordDao sensitiveWordDao = new SensitiveWordDaoImpl(); // 创建DAO实例
        List<String> wordsFromDb = sensitiveWordDao.findAllWords(); // 获取所有敏感词
        
        sensitiveWords.addAll(wordsFromDb); // 将从数据库读出的词添加到静态列表中
        // 按长度从长到短排序，优先匹配最长的词，解决重叠问题
        sensitiveWords.sort(Comparator.comparingInt(String::length).reversed()); // 核心：按长度降序排序，避免"badword"被"bad"错误地先替换
    }

    public static String filter(String text) { // 公共静态方法，用于过滤文本
        if (text == null || text.isEmpty()) { // 检查输入文本是否为空
            return text; // 若为空则直接返回
        }
        for (String word : sensitiveWords) { // 遍历排好序的敏感词列表
            if (text.contains(word)) { // 如果文本包含当前敏感词
                text = text.replaceAll(word, generateAsterisks(word.length())); // 将其替换为等长的星号
            }
        }
        return text; // 返回过滤后的文本
    }

    private static String generateAsterisks(int length) { // 生成指定长度的星号字符串
        StringBuilder sb = new StringBuilder(length); // 创建一个StringBuilder以提高效率
        for (int i = 0; i < length; i++) { // 循环指定次数
            sb.append('*'); // 添加星号
        }
        return sb.toString(); // 返回最终的星号字符串
    }
}