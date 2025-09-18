package com.forum.dao.impl; // 定义包名

import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.User; // 导入User模型
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.*; // 导入JDBC相关类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * UserDao接口的JDBC实现类
 * @author 刁兴烨
 * @date 2025-06-27
 */
public class UserDaoImpl implements UserDao {

    @Override // 覆盖接口方法
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?"; // 定义根据ID查询的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement防止SQL注入
            pstmt.setInt(1, id); // 设置ID参数
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果结果集中有下一条记录
                    return Optional.of(mapRowToUser(rs)); // 映射记录为User对象并用Optional包装返回
                }
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常堆栈
        }
        return Optional.empty(); // 若未找到或发生异常，返回空的Optional
    }

    @Override // 覆盖接口方法
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?"; // 定义根据用户名查询的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, username); // 设置username参数
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToUser(rs)); // 映射并返回
                }
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 返回空Optional
    }

    @Override // 覆盖接口方法
    public List<User> findAll() {
        List<User> users = new ArrayList<>(); // 初始化一个空的用户列表
        String sql = "SELECT * FROM users"; // 定义查询所有用户的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             Statement stmt = conn.createStatement(); // 使用Statement因为没有参数
             ResultSet rs = stmt.executeQuery(sql)) { // 执行查询
            while (rs.next()) { // 循环遍历结果集
                users.add(mapRowToUser(rs)); // 将每条记录映射为User对象并添加到列表中
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
        return users; // 返回用户列表
    }

    @Override // 覆盖接口方法
    public void save(User user) {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, ?)"; // 定义插入用户的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 指定返回数据库生成的键
            pstmt.setString(1, user.getUsername()); // 设置用户名参数
            pstmt.setString(2, user.getPassword()); // 设置密码参数
            pstmt.setString(3, user.getRole().name()); // 将角色枚举转换为字符串名称并设置
            pstmt.setString(4, user.getStatus().name()); // 将状态枚举转换为字符串名称并设置
            pstmt.executeUpdate(); // 执行插入操作

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { // 获取返回的自增键
                if (generatedKeys.next()) { // 如果有自增键返回
                    user.setId(generatedKeys.getInt(1)); // 将返回的ID设置回原user对象
                }
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, status = ? WHERE id = ?"; // 定义更新用户的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, user.getUsername()); // 设置用户名
            pstmt.setString(2, user.getPassword()); // 设置密码
            pstmt.setString(3, user.getRole().name()); // 设置角色
            pstmt.setString(4, user.getStatus().name()); // 设置状态
            pstmt.setInt(5, user.getId()); // 设置用于定位记录的ID
            pstmt.executeUpdate(); // 执行更新操作
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?"; // 定义删除用户的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置要删除的用户ID
            pstmt.executeUpdate(); // 执行删除操作
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    // 辅助方法，将数据库查询结果的一行映射成一个User对象
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User(); // 创建一个新的User实例
        user.setId(rs.getInt("id")); // 从结果集获取id并设置
        user.setUsername(rs.getString("username")); // 从结果集获取username并设置
        user.setPassword(rs.getString("password")); // 从结果集获取password并设置
        user.setRole(User.UserRole.valueOf(rs.getString("role"))); // 从结果集获取role字符串并转换为枚举类型
        user.setStatus(User.UserStatus.valueOf(rs.getString("status"))); // 从结果集获取status字符串并转换为枚举类型
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // 从结果集获取时间戳并转换为LocalDateTime
        return user; // 返回构建完成的User对象
    }
}