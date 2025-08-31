package com.forum.dao.impl; // 定义包名

import com.forum.dao.CommentDao; // 导入CommentDao接口
import com.forum.dao.PostDao; // 导入PostDao接口
import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.Comment; // 导入Comment模型
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.*; // 导入JDBC相关类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * CommentDao接口的JDBC实现类
 * @author 刁兴烨
 * @date 2025-06-27
 */
public class CommentDaoImpl implements CommentDao {

    private final UserDao userDao = new UserDaoImpl(); // 依赖UserDao来获取作者信息
    private final PostDao postDao = new PostDaoImpl(); // 依赖PostDao来获取所属帖子信息

    @Override // 覆盖接口方法
    public Optional<Comment> findById(int id) {
        String sql = "SELECT * FROM comments WHERE id = ?"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToComment(rs)); // 映射并返回
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 返回空
    }

    @Override // 覆盖接口方法
    public List<Comment> findByPostId(int postId) {
        List<Comment> comments = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC"; // 定义SQL，按时间正序排列
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId); // 设置帖子ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                while (rs.next()) { // 遍历结果
                    comments.add(mapRowToComment(rs)); // 映射并添加到列表
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return comments; // 返回列表
    }

    @Override
    public List<Comment> findByAuthorId(int authorId) {
        List<Comment> comments = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM comments WHERE author_id = ? ORDER BY created_at DESC"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId); // 设置作者ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                while (rs.next()) { // 遍历结果
                    comments.add(mapRowToComment(rs)); // 映射并添加到列表
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return comments; // 返回列表
    }

    @Override // 覆盖接口方法
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (content, author_id, post_id, parent_id) VALUES (?, ?, ?, ?)"; // 定义插入SQL，包含parent_id
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 指定返回自增键
            pstmt.setString(1, comment.getContent()); // 设置内容
            pstmt.setInt(2, comment.getAuthor().getId()); // 设置作者ID
            pstmt.setInt(3, comment.getPost().getId()); // 设置帖子ID
            if (comment.getParentId() != null) { // 如果是回复
                pstmt.setInt(4, comment.getParentId()); // 设置父评论ID
            } else { // 如果是顶级评论
                pstmt.setNull(4, Types.INTEGER); // 将父评论ID设为NULL
            }
            pstmt.executeUpdate(); // 执行插入

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { // 获取返回的ID
                if (generatedKeys.next()) { // 如果有
                    comment.setId(generatedKeys.getInt(1)); // 将ID设置回原对象
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void update(Comment comment) {
        String sql = "UPDATE comments SET content = ?, likes = ?, dislikes = ? WHERE id = ?"; // 定义更新SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, comment.getContent()); // 设置内容
            pstmt.setInt(2, comment.getLikes()); // 设置点赞数
            pstmt.setInt(3, comment.getDislikes()); // 设置点踩数
            pstmt.setInt(4, comment.getId()); // 设置用于定位的ID
            pstmt.executeUpdate(); // 执行更新
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void deleteById(int id) {
        String sql = "DELETE FROM comments WHERE id = ?"; // 定义删除SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置要删除的ID
            pstmt.executeUpdate(); // 执行删除
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    // 辅助方法，将数据库一行记录映射成一个Comment对象
    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment(); // 创建新Comment实例
        comment.setId(rs.getInt("id")); // 映射ID
        comment.setContent(rs.getString("content")); // 映射内容
        comment.setLikes(rs.getInt("likes")); // 映射点赞数
        comment.setDislikes(rs.getInt("dislikes")); // 映射点踩数
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // 映射创建时间
        comment.setParentId(rs.getObject("parent_id", Integer.class)); // 映射父评论ID

        int authorId = rs.getInt("author_id"); // 获取作者ID
        userDao.findById(authorId).ifPresent(comment::setAuthor); // 通过userDao查找并设置作者

        int postId = rs.getInt("post_id"); // 获取帖子ID
        postDao.findById(postId).ifPresent(comment::setPost); // 通过postDao查找并设置帖子

        return comment; // 返回构建完成的Comment对象
    }
}