package com.forum.dao.impl; // 定义包名

import com.forum.dao.VoteDao; // 导入VoteDao接口
import com.forum.model.Vote; // 导入Vote模型
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.*; // 导入JDBC相关类
import java.util.Optional; // 导入Optional类

/**
 * VoteDao接口的JDBC实现类
 * @author 任航瑞
 * @date 2025-07-04
 */
public class VoteDaoImpl implements VoteDao {

    @Override
    public Optional<Vote> findByUserIdAndPostId(int userId, int postId) {
        String sql = "SELECT * FROM user_votes WHERE user_id = ? AND post_id = ?"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, userId); // 设置用户ID
            pstmt.setInt(2, postId); // 设置帖子ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToVote(rs)); // 映射并返回
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 返回空
    }

    @Override
    public Optional<Vote> findByUserIdAndCommentId(int userId, int commentId) {
        String sql = "SELECT * FROM user_votes WHERE user_id = ? AND comment_id = ?"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, userId); // 设置用户ID
            pstmt.setInt(2, commentId); // 设置评论ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToVote(rs)); // 映射并返回
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 返回空
    }

    @Override
    public void save(Vote vote) {
        String sql = "INSERT INTO user_votes (user_id, post_id, comment_id, vote_type) VALUES (?, ?, ?, ?)"; // 定义插入SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 指定返回自增键
            setVoteParameters(pstmt, vote); // 使用辅助方法设置参数
            pstmt.executeUpdate(); // 执行插入
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { // 获取返回的ID
                if (generatedKeys.next()) { // 如果有
                    vote.setId(generatedKeys.getInt(1)); // 将ID设置回原对象
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override
    public void update(Vote vote) {
        String sql = "UPDATE user_votes SET vote_type = ? WHERE id = ?"; // 定义更新SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, vote.getVoteType().name()); // 设置投票类型
            pstmt.setInt(2, vote.getId()); // 设置用于定位的ID
            pstmt.executeUpdate(); // 执行更新
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override
    public void delete(int voteId) {
        String sql = "DELETE FROM user_votes WHERE id = ?"; // 定义删除SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, voteId); // 设置要删除的ID
            pstmt.executeUpdate(); // 执行删除
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    private Vote mapRowToVote(ResultSet rs) throws SQLException {
        Vote vote = new Vote(); // 创建新Vote实例
        vote.setId(rs.getInt("id")); // 映射ID
        vote.setUserId(rs.getInt("user_id")); // 映射用户ID
        vote.setPostId((Integer) rs.getObject("post_id")); // 映射帖子ID，可能为null
        vote.setCommentId((Integer) rs.getObject("comment_id")); // 映射评论ID，可能为null
        vote.setVoteType(Vote.VoteType.valueOf(rs.getString("vote_type"))); // 映射投票类型
        vote.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // 映射创建时间
        return vote; // 返回构建完成的Vote对象
    }

    private void setVoteParameters(PreparedStatement pstmt, Vote vote) throws SQLException {
        pstmt.setInt(1, vote.getUserId()); // 设置用户ID
        if (vote.getPostId() != null) { // 如果帖子ID不为null
            pstmt.setInt(2, vote.getPostId()); // 设置帖子ID
        } else {
            pstmt.setNull(2, Types.INTEGER); // 否则设为NULL
        }
        if (vote.getCommentId() != null) { // 如果评论ID不为null
            pstmt.setInt(3, vote.getCommentId()); // 设置评论ID
        } else {
            pstmt.setNull(3, Types.INTEGER); // 否则设为NULL
        }
        pstmt.setString(4, vote.getVoteType().name()); // 设置投票类型
    }
}