package com.forum.dao.impl; // 定义包名

import com.forum.dao.SensitiveWordDao; // 导入SensitiveWordDao接口
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.Connection; // 导入JDBC相关类
import java.sql.PreparedStatement; // 导入JDBC相关类
import java.sql.ResultSet; // 导入JDBC相关类
import java.sql.SQLException; // 导入JDBC相关类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口

/**
 * SensitiveWordDao接口的JDBC实现类
 * @author 任航瑞
 * @date 2025-07-04
 */
public class SensitiveWordDaoImpl implements SensitiveWordDao {

    @Override // 覆盖接口方法
    public List<String> findAllWords() {
        List<String> words = new ArrayList<>(); // 初始化一个空的字符串列表
        String sql = "SELECT word FROM sensitive_words"; // 定义查询所有敏感词的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql); // 创建PreparedStatement
             ResultSet rs = pstmt.executeQuery()) { // 执行查询
            while (rs.next()) { // 遍历结果集
                words.add(rs.getString("word")); // 将"word"列的值添加到列表中
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
        return words; // 返回包含所有敏感词的列表
    }
}