package com.forum.service.impl; // 定义包名

import com.forum.dao.BoardDao; // 导入BoardDao接口
import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.Board; // 导入Board模型
import com.forum.model.User; // 导入User模型
import com.forum.service.BoardService; // 导入BoardService接口

import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * BoardService接口的实现类
 * @author 任航瑞
 * @date 2025-06-27
 */
public class BoardServiceImpl implements BoardService {

    private final BoardDao boardDao; // 持有BoardDao实例，用于版块数据访问
    private final UserDao userDao; // 持有UserDao实例，用于用户数据访问

    // 构造函数，用于依赖注入
    public BoardServiceImpl(BoardDao boardDao, UserDao userDao) {
        this.boardDao = boardDao;
        this.userDao = userDao;
    }

    @Override // 覆盖接口方法
    public Board createBoard(String name, String description, int moderatorId) {
        // 查找指定的版主用户，如果ID无效则抛出异常
        User moderator = userDao.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("Moderator not found with id: " + moderatorId));
        Board board = new Board(0, name, description, moderator); // 创建一个新的版块实例
        boardDao.save(board); // 调用DAO层保存到数据库
        return board; // 返回创建完成的版块对象
    }

    @Override // 覆盖接口方法
    public Optional<Board> findById(int id) {
        return boardDao.findById(id); // 直接委托DAO层处理
    }

    @Override // 覆盖接口方法
    public List<Board> getAllBoards() {
        return boardDao.findAll(); // 直接委托DAO层处理
    }

    @Override // 覆盖接口方法
    public void updateBoard(int boardId, String name, String description) {
        // 查找要更新的版块，如果找不到则抛出异常
        Board board = boardDao.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        board.setName(name); // 更新版块名称
        board.setDescription(description); // 更新版块描述
        boardDao.update(board); // 调用DAO层持久化更改
    }

    @Override // 覆盖接口方法
    public void deleteBoard(int boardId) {
        boardDao.deleteById(boardId); // 直接委托DAO层处理
    }

    @Override // 覆盖接口方法
    public void assignModerator(int boardId, int userId) {
        // 查找要操作的版块，如果找不到则抛出异常
        Board board = boardDao.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        if (userId == 0) { // 约定：如果传入的用户ID为0，则表示撤销版主
            board.setModerator(null); // 将版主对象设置为null
        } else { // 否则，执行任命新版主的逻辑
            // 查找要任命的用户，如果找不到则抛出异常
            User moderator = userDao.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
            board.setModerator(moderator); // 将查找到的用户对象设置为新版主
        }
        
        boardDao.update(board); // 调用DAO层更新数据库中的版块信息
    }
}