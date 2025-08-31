package com.forum.service;

import com.forum.dao.BoardDao;
import com.forum.dao.CommentDao;
import com.forum.dao.PostDao;
import com.forum.dao.UserDao;
import com.forum.dao.impl.BoardDaoImpl;
import com.forum.dao.impl.CommentDaoImpl;
import com.forum.dao.impl.PostDaoImpl;
import com.forum.dao.impl.UserDaoImpl;
import com.forum.model.Board;
import com.forum.model.Comment;
import com.forum.model.Post;
import com.forum.model.User;
import com.forum.service.impl.BoardServiceImpl;
import com.forum.service.impl.PostServiceImpl;
import com.forum.util.DatabaseUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("帖子与评论服务核心功能测试")
class PostServiceTest {

    private PostService postService;
    private UserDao userDao;
    private PostDao postDao;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Reader reader = new BufferedReader(new FileReader("doc/数据库设计.sql"))) {
            ScriptRunner sr = new ScriptRunner(conn);
            sr.runScript(reader);
        }
        userDao = new UserDaoImpl();
        BoardDao boardDao = new BoardDaoImpl();
        postDao = new PostDaoImpl();
        CommentDao commentDao = new CommentDaoImpl();
        postService = new PostServiceImpl(postDao, userDao, boardDao, commentDao);
    }

    @Test
    @DisplayName("测试成功发表帖子")
    void testCreatePostSuccess() {
        User author = userDao.findByUsername("member1").get();
        Board board = new Board();
        board.setId(1);
        Post newPost = postService.createPost("新帖子标题", "新帖子内容", author.getId(), board.getId());
        assertNotNull(newPost);
        assertTrue(newPost.getId() > 0);
    }

    @Test
    @DisplayName("测试帖子点赞和取消点赞")
    void testPostVoteLogic() {
        Post post = postDao.findAll().get(1);
        User user = userDao.findByUsername("member1").get();

        postService.likePost(post.getId(), user.getId());
        Post postAfterLike = postDao.findById(post.getId()).get();
        assertEquals(1, postAfterLike.getLikes());

        postService.likePost(post.getId(), user.getId());
        Post postAfterUnlike = postDao.findById(post.getId()).get();
        assertEquals(0, postAfterUnlike.getLikes());
    }

    @Test
    @DisplayName("测试评论点赞和切换为点踩")
    void testCommentVoteLogic() {
        Comment comment = postService.getPostDetails(2).get().getComments().get(0);
        User user = userDao.findByUsername("member1").get();

        postService.likeComment(comment.getId(), user.getId());
        Comment commentAfterLike = postService.getPostDetails(2).get().getComments().get(0);
        assertEquals(1, commentAfterLike.getLikes());

        postService.dislikeComment(comment.getId(), user.getId());
        Comment commentAfterSwitch = postService.getPostDetails(2).get().getComments().get(0);
        assertEquals(0, commentAfterSwitch.getLikes());
        assertEquals(1, commentAfterSwitch.getDislikes());
    }

    @Test
    @DisplayName("测试对不存在的帖子点赞失败")
    void testVoteOnNonExistentPost() {
        User user = userDao.findByUsername("member1").get();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.likePost(999, user.getId());
        });
        assertEquals("帖子不存在。", exception.getMessage());
    }

    @Test
    @DisplayName("健壮性测试：创建帖子时使用无效参数")
    void testCreatePostWithInvalidParams() {
        User author = userDao.findByUsername("member1").get();
        // 测试无效的authorId
        assertThrows(Exception.class, () -> postService.createPost("标题", "内容", 999, 1));
        // 测试无效的boardId
        assertThrows(Exception.class, () -> postService.createPost("标题", "内容", author.getId(), 999));
    }
}