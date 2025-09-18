package com.forum.service;

import com.forum.dao.BoardDao;
import com.forum.dao.UserDao;
import com.forum.dao.impl.BoardDaoImpl;
import com.forum.dao.impl.UserDaoImpl;
import com.forum.model.Board;
import com.forum.model.User;
import com.forum.service.impl.BoardServiceImpl;
import com.forum.util.DatabaseUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("版块服务核心功能测试")
class BoardServiceTest {

    private BoardService boardService;
    private UserDao userDao;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Reader reader = new BufferedReader(new FileReader("doc/数据库设计.sql"))) {
            ScriptRunner sr = new ScriptRunner(conn);
            sr.runScript(reader);
        }
        BoardDao boardDao = new BoardDaoImpl();
        userDao = new UserDaoImpl();
        boardService = new BoardServiceImpl(boardDao, userDao);
    }

    @Test
    @DisplayName("测试成功创建一个新版块")
    void testCreateBoardSuccess() {
        User moderator = userDao.findByUsername("moderator").get();
        Board newBoard = boardService.createBoard("Go语言学习", "讨论Go语言", moderator.getId());
        assertNotNull(newBoard);
        assertTrue(newBoard.getId() > 0);
    }

    @Test
    @DisplayName("测试使用不存在的版主ID创建版块失败")
    void testCreateBoardFailWithNonExistentModerator() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.createBoard("新版块", "描述", 999);
        });
    }

    @Test
    @DisplayName("测试成功为版块分配新版主")
    void testAssignModeratorSuccess() {
        Board board = boardService.getAllBoards().get(1); // 生活分享版块
        User newModerator = userDao.findByUsername("member1").get();
        
        boardService.assignModerator(board.getId(), newModerator.getId());
        
        Board updatedBoard = boardService.findById(board.getId()).get();
        assertNotNull(updatedBoard.getModerator());
        assertEquals("member1", updatedBoard.getModerator().getUsername());
    }

    @Test
    @DisplayName("测试使用用户ID为0成功撤销版主")
    void testRevokeModerator() {
        Board board = boardService.getAllBoards().get(0); // 技术交流版块
        assertNotNull(board.getModerator());

        boardService.assignModerator(board.getId(), 0);

        Board updatedBoard = boardService.findById(board.getId()).get();
        assertNull(updatedBoard.getModerator());
    }

    @Test
    @DisplayName("健壮性测试：创建版块时使用无效参数")
    void testCreateBoardWithInvalidParams() {
        User moderator = userDao.findByUsername("moderator").get();
        // 此处可以添加更多对name和description的校验，如非空、长度限制等
        // 当前实现仅测试Dao/Service层对非法ID的反应
        assertThrows(Exception.class, () -> boardService.createBoard("新版块", "描述", 999));
    }
}