package com.forum.service; // 定义包名

import com.forum.model.Post; // 导入Post模型
import com.forum.model.Comment; // 导入Comment模型
import com.forum.model.User; // 导入User模型

import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 帖子与评论服务接口
 * 定义了所有与帖子、评论相关的核心业务逻辑。
 * @author 陈羽飞
 * @author 任航瑞(添加了点赞点踩和具体实现)
 * @date 2025-06-27
 */
public interface PostService {

    // 创建一个新帖子
    Post createPost(String title, String content, int authorId, int boardId);

    // 获取帖子详情，包括其下所有评论的层级结构
    Optional<Post> getPostDetails(int postId);

    // 获取指定版块下的所有帖子列表
    List<Post> getPostsByBoard(int boardId);

    // 删除一篇帖子，包含权限验证逻辑
    void deletePost(int postId, User currentUser);

    // 置顶一篇帖子 (版主/管理员权限)
    void pinPost(int postId, User currentUser);

    // 取消置顶一篇帖子 (版主/管理员权限)
    void unpinPost(int postId, User currentUser);

    // 添加一条评论，parentId为null时是顶级评论，否则是回复
    Comment addComment(String content, int postId, int authorId, Integer parentId);

    // 删除一条评论，包含权限验证逻辑
    void deleteComment(int commentId, User currentUser);

    // 点赞一篇帖子
    void likePost(int postId, int userId);

    // 点踩一篇帖子
    void dislikePost(int postId, int userId);



    // 点赞一条评论
    void likeComment(int commentId, int userId);

    // 点踩一条评论
    void dislikeComment(int commentId, int userId);

    // 根据标题关键字模糊搜索帖子
    List<Post> searchPostsByTitle(String keyword);

    // 获取指定作者的所有帖子
    List<Post> getPostsByAuthor(int authorId);

    // 获取指定作者的所有评论
    List<Comment> getCommentsByAuthor(int authorId);
}