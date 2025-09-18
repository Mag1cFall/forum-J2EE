package com.forum.util; // 定义包名

import java.sql.Connection; // 导入数据库连接类
import java.sql.DriverManager; // 导入驱动管理类
import java.sql.SQLException; // 导入SQL异常类

/**
 * 数据库连接工具类
 * 提供获取和关闭数据库连接的静态方法。
 * @author 王绍源
 * @date 2025-06-27
 */
public class DatabaseUtil {

    // 数据库连接URL，指向本地的forum_db数据库
    private static final String URL = "jdbc:mysql://localhost:3306/forum_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    // 数据库用户名
    private static final String USER = "root";
    // 数据库密码
    private static final String PASSWORD = "123456";

    static { // 静态块，用于在类加载时仅执行一次驱动加载
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 反射加载MySQL驱动
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver.", e); // 如果驱动未找到，抛出运行时异常，终止程序
        }
    }

    public static Connection getConnection() throws SQLException { // 提供全局唯一的获取数据库连接的方法
        return DriverManager.getConnection(URL, USER, PASSWORD); // 通过DriverManager获取一个新的数据库连接
    }

    // 关闭数据库连接的静态方法
    public static void close(Connection connection) {
        if (connection != null) { // 检查连接是否已打开
            try {
                connection.close(); // 尝试关闭连接
            } catch (SQLException e) { // 捕获并处理关闭时可能发生的异常
                // 在实际应用中，这里应该使用日志框架记录错误，此处简化了。
                e.printStackTrace(); // 打印异常信息到控制台
            }
        }
    }
}