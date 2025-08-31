package com.forum.model; // 定义包名

import java.time.LocalDateTime; // 导入时间处理类
import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 * @author 刁兴烨
 * @date 2025-06-27
 */
public class Comment {

    private int id; // 评论唯一ID
    private String content; // 评论内容
    private User author; // 评论作者 (关联User对象)
    private Post post; // 所属帖子 (关联Post对象)
    private LocalDateTime createdAt; // 评论创建时间
    private int likes; // 点赞数
    private int dislikes; // 点踩数
    private Integer parentId; // 父评论ID，用于指向被回复的评论
    private List<Comment> replies = new ArrayList<>(); // 用于存放该评论下的所有回复，构建内存中的评论树

    // 无参构造函数
    public Comment() {
    }

    // 全参构造函数
    public Comment(int id, String content, User author, Post post) {
        this.id = id; // 初始化ID
        this.content = content; // 初始化内容
        this.author = author; // 初始化作者
        this.post = post; // 初始化所属帖子
        this.createdAt = LocalDateTime.now(); // 自动设置创建时间为当前时间
    }

    // --- 以下是各个属性的 Getter 和 Setter 方法 ---

    public int getId() { // 获取评论ID
        return id;
    }

    public void setId(int id) { // 设置评论ID
        this.id = id;
    }

    public String getContent() { // 获取评论内容
        return content;
    }

    public void setContent(String content) { // 设置评论内容
        this.content = content;
    }

    public User getAuthor() { // 获取作者对象
        return author;
    }

    public void setAuthor(User author) { // 设置作者对象
        this.author = author;
    }

    public Post getPost() { // 获取所属帖子对象
        return post;
    }

    public void setPost(Post post) { // 设置所属帖子对象
        this.post = post;
    }

    public LocalDateTime getCreatedAt() { // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // 设置创建时间
        this.createdAt = createdAt;
    }

    public int getLikes() { // 获取点赞数
        return likes;
    }

    public void setLikes(int likes) { // 设置点赞数
        this.likes = likes;
    }

    public int getDislikes() { // 获取点踩数
        return dislikes;
    }

    public void setDislikes(int dislikes) { // 设置点踩数
        this.dislikes = dislikes;
    }

    public Integer getParentId() { // 获取父评论ID
        return parentId;
    }

    public void setParentId(Integer parentId) { // 设置父评论ID
        this.parentId = parentId;
    }

    public List<Comment> getReplies() { // 获取回复列表
        return replies;
    }

    public void setReplies(List<Comment> replies) { // 设置回复列表
        this.replies = replies;
    }

    // 重写toString方法，方便调试
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", author=" + author.getUsername() +
                ", createdAt=" + createdAt +
                ", parentId=" + parentId +
                '}';
    }
}