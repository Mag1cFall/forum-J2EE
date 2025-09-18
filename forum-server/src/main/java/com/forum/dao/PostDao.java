package com.forum.dao; // 定义包名

import com.forum.model.Post; // 导入Post模型
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 帖子数据访问对象 (DAO) 接口
 * 定义了所有与帖子数据相关的数据库操作标准。
 * @author 李子昂
 * @author 任航瑞
 * @date 2025-06-27
 */
public interface PostDao {

    // 根据ID查找帖子，返回Optional<Post>
    Optional<Post> findById(int id);

    // 根据版块ID查找该版块下的所有帖子
    List<Post> findByBoardId(int boardId);

    // 查找所有帖子 (主要用于测试或未来可能的全局展示)
    List<Post> findAll();

    // 根据标题关键字搜索帖子
    List<Post> searchByTitle(String keyword);

    // 根据作者ID查找其所有帖子
    List<Post> findByAuthorId(int authorId);

    // 保存新帖子 (创建)
    void save(Post post);

    // 更新帖子信息
    void update(Post post);

    // 根据ID删除帖子
    void deleteById(int id);
}