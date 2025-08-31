package com.forum.dao; // 定义包名

import java.util.List; // 导入List接口

/**
 * 敏感词数据访问对象 (DAO) 接口
 * 定义了从数据库获取敏感词的操作标准。
 * @author 任航瑞
 * @date 2025-07-04
 */
public interface SensitiveWordDao {

    // 查找并返回数据库中所有的敏感词字符串列表
    List<String> findAllWords();
}