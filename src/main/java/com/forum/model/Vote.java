package com.forum.model; // 定义包名

import java.time.LocalDateTime; // 导入时间处理类

/**
 * 用户投票记录实体类
 * @author 任航瑞
 * @date 2025-07-04
 */
public class Vote {

    private int id; // 投票记录唯一ID
    private int userId; // 用户ID
    private Integer postId; // 帖子ID (可以为null，因为投票对象可能是评论)
    private Integer commentId; // 评论ID (可以为null，因为投票对象可能是帖子)
    private VoteType voteType; // 投票类型 (枚举)
    private LocalDateTime createdAt; // 创建时间

    // 定义投票类型的枚举，用于安全地表示点赞或点踩
    public enum VoteType {
        LIKE,    // 点赞
        DISLIKE  // 点踩
    }

    // 无参构造函数
    public Vote() {
    }

    // --- 以下是各个属性的 Getter 和 Setter 方法 ---

    public int getId() { // 获取投票ID
        return id;
    }

    public void setId(int id) { // 设置投票ID
        this.id = id;
    }

    public int getUserId() { // 获取用户ID
        return userId;
    }

    public void setUserId(int userId) { // 设置用户ID
        this.userId = userId;
    }

    public Integer getPostId() { // 获取帖子ID
        return postId;
    }

    public void setPostId(Integer postId) { // 设置帖子ID
        this.postId = postId;
    }

    public Integer getCommentId() { // 获取评论ID
        return commentId;
    }

    public void setCommentId(Integer commentId) { // 设置评论ID
        this.commentId = commentId;
    }

    public VoteType getVoteType() { // 获取投票类型
        return voteType;
    }

    public void setVoteType(VoteType voteType) { // 设置投票类型
        this.voteType = voteType;
    }

    public LocalDateTime getCreatedAt() { // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // 设置创建时间
        this.createdAt = createdAt;
    }
}