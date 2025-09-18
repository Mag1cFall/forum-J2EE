package com.forum.dao.impl; // 定义包名

import com.forum.dao.BoardDao; // 导入BoardDao接口
import com.forum.dao.PostDao; // 导入PostDao接口
import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.Post; // 导入Post模型
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.*; // 导入JDBC相关类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * PostDao接口的JDBC实现类
 * @author 李子昂
 * @author 任航瑞
 * @date 2025-06-27
 */
public class PostDaoImpl implements PostDao {

    private final UserDao userDao = new UserDaoImpl(); // 依赖UserDao来获取作者的完整信息
    private final BoardDao boardDao = new BoardDaoImpl(); // 依赖BoardDao来获取版块的完整信息

    @Override // 覆盖接口方法
    public Optional<Post> findById(int id) {
        String sql = "SELECT * FROM posts WHERE id = ?"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置参数
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToPost(rs)); // 映射并返回
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 返回空
    }

    @Override // 覆盖接口方法
    public List<Post> findByBoardId(int boardId) {
        List<Post> posts = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM posts WHERE board_id = ? ORDER BY is_pinned DESC, created_at DESC"; // SQL查询，置顶优先，再按时间倒序
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, boardId); // 设置版块ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                while (rs.next()) { // 遍历结果
                    posts.add(mapRowToPost(rs)); // 映射并添加到列表
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return posts; // 返回列表
    }

    @Override // 覆盖接口方法
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM posts"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             Statement stmt = conn.createStatement(); // 创建Statement
             ResultSet rs = stmt.executeQuery(sql)) { // 执行查询
            while (rs.next()) { // 遍历结果
                posts.add(mapRowToPost(rs)); // 映射并添加到列表
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return posts; // 返回列表
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        List<Post> posts = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM posts WHERE title LIKE ?"; // 使用LIKE进行模糊查询
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%"); // 设置包含通配符的关键字
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                while (rs.next()) { // 遍历结果
                    posts.add(mapRowToPost(rs)); // 映射并添加到列表
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return posts; // 返回列表
    }

    @Override
    public List<Post> findByAuthorId(int authorId) {
        List<Post> posts = new ArrayList<>(); // 初始化列表
        String sql = "SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC"; // 定义SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId); // 设置作者ID
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                while (rs.next()) { // 遍历结果
                    posts.add(mapRowToPost(rs)); // 映射并添加到列表
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
        return posts; // 返回列表
    }

    @Override // 覆盖接口方法
    public void save(Post post) {
        String sql = "INSERT INTO posts (title, content, author_id, board_id, is_pinned) VALUES (?, ?, ?, ?, ?)"; // 定义插入SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 指定返回自增键
            pstmt.setString(1, post.getTitle()); // 设置标题
            pstmt.setString(2, post.getContent()); // 设置内容
            pstmt.setInt(3, post.getAuthor().getId()); // 设置作者ID
            pstmt.setInt(4, post.getBoard().getId()); // 设置版块ID
            pstmt.setBoolean(5, post.isPinned()); // 设置置顶状态
            pstmt.executeUpdate(); // 执行插入

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { // 获取返回的ID
                if (generatedKeys.next()) { // 如果有
                    post.setId(generatedKeys.getInt(1)); // 将ID设置回原对象
                }
            }
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ?, is_pinned = ?, likes = ?, dislikes = ? WHERE id = ?"; // 定义更新SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, post.getTitle()); // 设置标题
            pstmt.setString(2, post.getContent()); // 设置内容
            pstmt.setBoolean(3, post.isPinned()); // 设置置顶状态
            pstmt.setInt(4, post.getLikes()); // 设置点赞数
            pstmt.setInt(5, post.getDislikes()); // 设置点踩数
            pstmt.setInt(6, post.getId()); // 设置用于定位的ID
            pstmt.executeUpdate(); // 执行更新
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void deleteById(int id) {
        String sql = "DELETE FROM posts WHERE id = ?"; // 定义删除SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置要删除的ID
            pstmt.executeUpdate(); // 执行删除
        } catch (SQLException e) { // 捕获异常
            e.printStackTrace(); // 打印异常
        }
    }

    // 辅助方法，将数据库一行记录映射成一个Post对象
    private Post mapRowToPost(ResultSet rs) throws SQLException {
        Post post = new Post(); // 创建新Post实例
        post.setId(rs.getInt("id")); // 映射ID
        post.setTitle(rs.getString("title")); // 映射标题
        post.setContent(rs.getString("content")); // 映射内容
        post.setPinned(rs.getBoolean("is_pinned")); // 映射置顶状态
        post.setLikes(rs.getInt("likes")); // 映射点赞数
        post.setDislikes(rs.getInt("dislikes")); // 映射点踩数
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // 映射创建时间

        int authorId = rs.getInt("author_id"); // 获取作者ID
        userDao.findById(authorId).ifPresent(post::setAuthor); // 通过userDao查找并设置完整的作者对象

        int boardId = rs.getInt("board_id"); // 获取版块ID
        boardDao.findById(boardId).ifPresent(post::setBoard); // 通过boardDao查找并设置完整的版块对象

        return post; // 返回构建完成的Post对象
    }
}