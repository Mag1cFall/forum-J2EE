package com.forum.dao.impl; // 定义包名

import com.forum.dao.BoardDao; // 导入BoardDao接口
import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.Board; // 导入Board模型
//import com.forum.model.User; // 导入User模型
import com.forum.util.DatabaseUtil; // 导入数据库工具类

import java.sql.*; // 导入JDBC相关类
import java.util.ArrayList; // 导入ArrayList类
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * BoardDao接口的JDBC实现类
 * @author 李子昂
 * @date 2025-06-27
 */
public class BoardDaoImpl implements BoardDao {

    private final UserDao userDao = new UserDaoImpl(); // 依赖UserDao来获取版主信息 (在大型项目中应使用依赖注入框架)

    @Override // 覆盖接口方法
    public Optional<Board> findById(int id) {
        String sql = "SELECT * FROM boards WHERE id = ?"; // 定义根据ID查询的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置ID参数
            try (ResultSet rs = pstmt.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果有结果
                    return Optional.of(mapRowToBoard(rs)); // 映射记录为Board对象并用Optional包装返回
                }
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
        return Optional.empty(); // 若未找到或发生异常，返回空的Optional
    }

    @Override // 覆盖接口方法
    public List<Board> findAll() {
        List<Board> boards = new ArrayList<>(); // 初始化一个空的版块列表
        String sql = "SELECT * FROM boards"; // 定义查询所有版块的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             Statement stmt = conn.createStatement(); // 使用Statement
             ResultSet rs = stmt.executeQuery(sql)) { // 执行查询
            while (rs.next()) { // 遍历结果集
                boards.add(mapRowToBoard(rs)); // 将每条记录映射为Board对象并添加到列表
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
        return boards; // 返回版块列表
    }

    @Override // 覆盖接口方法
    public void save(Board board) {
        String sql = "INSERT INTO boards (name, description, moderator_id) VALUES (?, ?, ?)"; // 定义插入版块的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 指定返回自增键
            pstmt.setString(1, board.getName()); // 设置版块名称
            pstmt.setString(2, board.getDescription()); // 设置版块描述
            if (board.getModerator() != null) { // 如果版主对象不为null
                pstmt.setInt(3, board.getModerator().getId()); // 设置版主ID
            } else { // 如果没有版主
                pstmt.setNull(3, Types.INTEGER); // 在数据库中将moderator_id设置为NULL
            }
            pstmt.executeUpdate(); // 执行插入

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { // 获取返回的自增键
                if (generatedKeys.next()) { // 如果有
                    board.setId(generatedKeys.getInt(1)); // 将ID设置回原board对象
                }
            }
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void update(Board board) {
        String sql = "UPDATE boards SET name = ?, description = ?, moderator_id = ? WHERE id = ?"; // 定义更新版块的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setString(1, board.getName()); // 设置名称
            pstmt.setString(2, board.getDescription()); // 设置描述
            if (board.getModerator() != null) { // 如果版主不为null
                pstmt.setInt(3, board.getModerator().getId()); // 设置版主ID
            } else { // 如果要移除版主
                pstmt.setNull(3, Types.INTEGER); // 将数据库中moderator_id设为NULL
            }
            pstmt.setInt(4, board.getId()); // 设置用于定位记录的ID
            pstmt.executeUpdate(); // 执行更新
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    @Override // 覆盖接口方法
    public void deleteById(int id) {
        String sql = "DELETE FROM boards WHERE id = ?"; // 定义删除版块的SQL
        try (Connection conn = DatabaseUtil.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // 创建PreparedStatement
            pstmt.setInt(1, id); // 设置要删除的版块ID
            pstmt.executeUpdate(); // 执行删除
        } catch (SQLException e) { // 捕获SQL异常
            e.printStackTrace(); // 打印异常
        }
    }

    // 辅助方法，将数据库查询结果的一行映射成一个Board对象
    private Board mapRowToBoard(ResultSet rs) throws SQLException {
        Board board = new Board(); // 创建新Board实例
        board.setId(rs.getInt("id")); // 设置ID
        board.setName(rs.getString("name")); // 设置名称
        board.setDescription(rs.getString("description")); // 设置描述
        board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // 设置创建时间

        int moderatorId = rs.getInt("moderator_id"); // 获取版主ID
        if (!rs.wasNull()) { // 检查数据库中的版主ID是否为NULL
            userDao.findById(moderatorId).ifPresent(board::setModerator); // 如果不为NULL，则通过userDao查找完整的User对象并设置
        }
        return board; // 返回构建完成的Board对象
    }
}