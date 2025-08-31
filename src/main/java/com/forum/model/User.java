package com.forum.model; // 定义包名

import java.time.LocalDateTime; // 导入时间处理类

/**
 * 用户实体类
 * @author 刁兴烨
 * @date 2025-06-27
 */
public class User {

    private int id; // 用户唯一ID
    private String username; // 用户名
    private String password; // 用户密码
    private UserRole role; // 用户角色 (枚举类型)
    private UserStatus status; // 用户状态 (枚举类型)
    private LocalDateTime createdAt; // 账号创建时间

    // 定义用户角色的枚举，用于类型安全地表示用户角色
    public enum UserRole {
        MEMBER,    // 普通成员
        MODERATOR, // 版主
        ADMIN      // 管理员
    }

    // 定义用户状态的枚举，用于类型安全地表示用户状态
    public enum UserStatus {
        ACTIVE,  // 活跃
        BLOCKED  // 被封禁
    }

    // 无参构造函数
    public User() {
    }

    // 全参构造函数，用于创建一个完整的用户对象
    public User(int id, String username, String password, UserRole role, UserStatus status) {
        this.id = id; // 初始化ID
        this.username = username; // 初始化用户名
        this.password = password; // 初始化密码
        this.role = role; // 初始化角色
        this.status = status; // 初始化状态
        this.createdAt = LocalDateTime.now(); // 自动设置创建时间为当前时间
    }

    // --- 以下是各个属性的 Getter 和 Setter 方法 ---

    public int getId() { // 获取用户ID
        return id;
    }

    public void setId(int id) { // 设置用户ID
        this.id = id;
    }

    public String getUsername() { // 获取用户名
        return username;
    }

    public void setUsername(String username) { // 设置用户名
        this.username = username;
    }

    public String getPassword() { // 获取密码
        return password;
    }

    public void setPassword(String password) { // 设置密码
        this.password = password;
    }

    public UserRole getRole() { // 获取用户角色
        return role;
    }

    public void setRole(UserRole role) { // 设置用户角色
        this.role = role;
    }

    public UserStatus getStatus() { // 获取用户状态
        return status;
    }

    public void setStatus(UserStatus status) { // 设置用户状态
        this.status = status;
    }

    public LocalDateTime getCreatedAt() { // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // 设置创建时间
        this.createdAt = createdAt;
    }

    // 重写toString方法，方便在日志或调试中打印用户信息
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}