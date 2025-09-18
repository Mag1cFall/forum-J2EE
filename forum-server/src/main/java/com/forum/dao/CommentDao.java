package com.forum.dao; // 定义包名

import com.forum.model.Comment; // 导入Comment模型
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 评论数据访问对象 (DAO) 接口
 * 定义了所有与评论数据相关的数据库操作标准。
 * @author 刁兴烨
 * @date 2025-06-27
 */
public interface CommentDao {

    // 根据ID查找评论
    Optional<Comment> findById(int id);

    // 根据帖子ID查找该帖子下的所有评论（包括所有层级的回复）
    List<Comment> findByPostId(int postId);

    // 根据作者ID查找其所有评论
    List<Comment> findByAuthorId(int authorId);

    // 保存新评论 (创建)
    void save(Comment comment);

    // 更新评论信息
    void update(Comment comment);

    // 根据ID删除评论
    void deleteById(int id);
}