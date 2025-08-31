package com.forum.model; // 定义包名

import java.time.LocalDateTime; // 导入时间处理类

/**
 * 版块实体类
 * @author 李子昂
 * @date 2025-06-27
 */
public class Board {

    private int id; // 版块唯一ID
    private String name; // 版块名称
    private String description; // 版块描述
    private User moderator; // 版主 (关联User对象)
    private LocalDateTime createdAt; // 版块创建时间

    // 无参构造函数，JavaBean规范
    public Board() {
    }

    // 全参构造函数，用于快速创建版块对象
    public Board(int id, String name, String description, User moderator) {
        this.id = id; // 初始化ID
        this.name = name; // 初始化名称
        this.description = description; // 初始化描述
        this.moderator = moderator; // 初始化版主
        this.createdAt = LocalDateTime.now(); // 自动设置创建时间为当前时间
    }

    // --- 以下是各个属性的 Getter 和 Setter 方法 ---

    public int getId() { // 获取版块ID
        return id;
    }

    public void setId(int id) { // 设置版块ID
        this.id = id;
    }

    public String getName() { // 获取版块名称
        return name;
    }

    public void setName(String name) { // 设置版块名称
        this.name = name;
    }

    public String getDescription() { // 获取版块描述
        return description;
    }

    public void setDescription(String description) { // 设置版块描述
        this.description = description;
    }

    public User getModerator() { // 获取版主对象
        return moderator;
    }

    public void setModerator(User moderator) { // 设置版主对象
        this.moderator = moderator;
    }

    public LocalDateTime getCreatedAt() { // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // 设置创建时间
        this.createdAt = createdAt;
    }

    // 重写toString方法，方便调试时打印对象信息
    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", moderator=" + (moderator != null ? moderator.getUsername() : "None") + // 安全地获取版主名
                ", createdAt=" + createdAt +
                '}';
    }
}