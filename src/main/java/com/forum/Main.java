package com.forum; // 定义包名

// 导入DAO层接口和实现
import com.forum.dao.BoardDao;
import com.forum.dao.CommentDao;
import com.forum.dao.*;
import com.forum.dao.impl.BoardDaoImpl;
import com.forum.dao.impl.CommentDaoImpl;
import com.forum.dao.impl.PostDaoImpl;
import com.forum.dao.impl.UserDaoImpl;
import com.forum.service.BoardService;
import com.forum.service.PostService;
import com.forum.service.UserService;
import com.forum.service.impl.BoardServiceImpl;
import com.forum.service.impl.PostServiceImpl;
import com.forum.service.impl.UserServiceImpl;
import com.forum.util.DatabaseUtil;
import com.forum.view.ConsoleView;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;

/**
 * 程序主入口
 * 负责初始化所有组件并启动应用程序。
 * @author 任航瑞
 * @date 2025-07-04
 */
public class Main {

    public static void main(String[] args) {
//        // --- 数据库自动初始化 ---
//        System.out.println("正在初始化数据库，请稍候...");
//        try (Connection conn = DatabaseUtil.getConnection();
//             Reader reader = new BufferedReader(new FileReader("doc/数据库设计.sql"))) {
//            ScriptRunner sr = new ScriptRunner(conn);
//            sr.runScript(reader); // 执行SQL脚本，重置数据
//            System.out.println("数据库初始化完成。");
//        } catch (Exception e) {
//            System.err.println("数据库初始化失败，请检查数据库连接和SQL脚本！");
//            e.printStackTrace();
//            return; // 初始化失败则退出程序
//        }

        // --- 依赖注入阶段 ---

        // 1. 实例化DAO层的实现类
        UserDao userDao = new UserDaoImpl();
        BoardDao boardDao = new BoardDaoImpl();
        PostDao postDao = new PostDaoImpl();
        CommentDao commentDao = new CommentDaoImpl();

        // 2. 实例化Service层的实现类，并通过构造函数注入DAO实例 (依赖注入)
        UserService userService = new UserServiceImpl(userDao);
        BoardService boardService = new BoardServiceImpl(boardDao, userDao);
        PostService postService = new PostServiceImpl(postDao, userDao, boardDao, commentDao);

        // 3. 实例化View层，并注入Service实例
        ConsoleView consoleView = new ConsoleView(userService, boardService, postService);

        // --- 运行阶段 ---
        // 启动控制台视图，开始与用户交互
        consoleView.run();
    }
}