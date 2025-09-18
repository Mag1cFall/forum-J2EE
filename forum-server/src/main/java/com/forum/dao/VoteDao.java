package com.forum.dao; // 定义包名

import com.forum.model.Vote; // 导入Vote模型
import java.util.Optional; // 导入Optional类

/**
 * 投票数据访问对象 (DAO) 接口
 * 定义了所有与用户投票数据相关的数据库操作标准。
 * @author 任航瑞
 * @date 2025-07-04
 */
public interface VoteDao {

    // 查找用户对特定帖子的投票记录
    Optional<Vote> findByUserIdAndPostId(int userId, int postId);

    // 查找用户对特定评论的投票记录
    Optional<Vote> findByUserIdAndCommentId(int userId, int commentId);

    // 保存一个新的投票记录
    void save(Vote vote);

    // 更新一个已有的投票记录（例如，从点赞切换为点踩）
    void update(Vote vote);

    // 根据投票ID删除一个投票记录（例如，取消点赞/点踩）
    void delete(int voteId);
}