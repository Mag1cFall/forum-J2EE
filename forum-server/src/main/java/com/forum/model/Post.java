package com.forum.model; // 定义包名

import java.time.LocalDateTime; // 导入时间处理类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口

/**
 * 帖子实体类
 * @author 李子昂
 * @date 2025-06-27
 */
public class Post {

    private int id; // 帖子唯一ID
    private String title; // 帖子标题
    private String content; // 帖子内容
    private User author; // 帖子作者 (关联User对象)
    private Board board; // 所属版块 (关联Board对象)
    private LocalDateTime createdAt; // 帖子创建时间
    private boolean isPinned; // 是否置顶
    private int likes; // 点赞数
    private int dislikes; // 点踩数
    private List<Comment> comments; // 帖子下的评论列表

    // 无参构造函数
    public Post() {
        this.comments = new ArrayList<>(); // 初始化评论列表，避免空指针
    }

    // 全参构造函数
    public Post(int id, String title, String content, User author, Board board) {
        this.id = id; // 初始化ID
        this.title = title; // 初始化标题
        this.content = content; // 初始化内容
        this.author = author; // 初始化作者
        this.board = board; // 初始化所属版块
        this.createdAt = LocalDateTime.now(); // 自动设置创建时间
        this.isPinned = false; // 新帖子默认不置顶
        this.comments = new ArrayList<>(); // 初始化评论列表
    }

    // --- 以下是各个属性的 Getter 和 Setter 方法 ---

    public int getId() { // 获取帖子ID
        return id;
    }

    public void setId(int id) { // 设置帖子ID
        this.id = id;
    }

    public String getTitle() { // 获取标题
        return title;
    }

    public void setTitle(String title) { // 设置标题
        this.title = title;
    }

    public String getContent() { // 获取内容
        return content;
    }

    public void setContent(String content) { // 设置内容
        this.content = content;
    }

    public User getAuthor() { // 获取作者对象
        return author;
    }

    public void setAuthor(User author) { // 设置作者对象
        this.author = author;
    }

    public Board getBoard() { // 获取版块对象
        return board;
    }

    public void setBoard(Board board) { // 设置版块对象
        this.board = board;
    }

    public LocalDateTime getCreatedAt() { // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // 设置创建时间
        this.createdAt = createdAt;
    }

    public boolean isPinned() { // 检查是否置顶
        return isPinned;
    }

    public void setPinned(boolean pinned) { // 设置置顶状态
        isPinned = pinned;
    }

    public List<Comment> getComments() { // 获取评论列表
        return comments;
    }

    public void setComments(List<Comment> comments) { // 设置评论列表
        this.comments = comments;
    }

    // 添加一条评论到评论列表
    public void addComment(Comment comment) { // 添加单个评论
        this.comments.add(comment);
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

    // 重写toString方法，方便调试
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author=" + author.getUsername() +
                ", board=" + board.getName() +
                ", createdAt=" + createdAt +
                ", isPinned=" + isPinned +
                '}';
    }
}