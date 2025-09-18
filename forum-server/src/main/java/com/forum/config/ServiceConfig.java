package com.forum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.dao.BoardDao;
import com.forum.dao.CommentDao;
import com.forum.dao.PostDao;
import com.forum.dao.UserDao;
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

/**
 */
public class ServiceConfig {

    private ServiceConfig() {
    }

    private static final UserDao userDao = new UserDaoImpl();
    private static final BoardDao boardDao = new BoardDaoImpl();
    private static final PostDao postDao = new PostDaoImpl();
    private static final CommentDao commentDao = new CommentDaoImpl();

    public static final UserService userService = new UserServiceImpl(userDao);
    public static final BoardService boardService = new BoardServiceImpl(boardDao, userDao);
    public static final PostService postService = new PostServiceImpl(postDao, userDao, boardDao, commentDao);

    public static final ObjectMapper mapperService = new ObjectMapper();
}
