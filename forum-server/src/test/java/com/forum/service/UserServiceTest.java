package com.forum.service;

import com.forum.dao.UserDao;
import com.forum.dao.impl.UserDaoImpl;
import com.forum.model.User;
import com.forum.service.impl.UserServiceImpl;
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

@DisplayName("用户服务核心功能测试")
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Reader reader = new BufferedReader(new FileReader("doc/数据库设计.sql"))) {
            ScriptRunner sr = new ScriptRunner(conn);
            sr.runScript(reader);
        }
        UserDao userDao = new UserDaoImpl();
        userService = new UserServiceImpl(userDao);
    }

    @Test
    @DisplayName("测试用户成功注册")
    void testRegisterSuccess() {
        User newUser = userService.register("newUser", "password123!");
        assertNotNull(newUser);
        assertTrue(newUser.getId() > 0);
    }

    @Test
    @DisplayName("测试使用已存在的用户名注册失败")
    void testRegisterFailOnExistingUsername() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register("admin", "newpassword1");
        });
        assertEquals("该用户名已被注册", ex.getMessage());
    }

    @Test
    @DisplayName("测试密码不符合规范时注册失败")
    void testRegisterFailWithInvalidPassword() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register("newUser", "12345");
        });
        assertEquals("密码长度必须在6到30个字符之间", ex.getMessage());
    }

    @Test
    @DisplayName("测试已存在的活跃用户成功登录")
    void testLoginSuccessForActiveUser() {
        Optional<User> userOpt = userService.login("member1", "mem123");
        assertTrue(userOpt.isPresent());
        assertEquals("member1", userOpt.get().getUsername());
    }

    @Test
    @DisplayName("测试被封禁的用户登录失败")
    void testLoginFailForBlockedUser() {
        Optional<User> userOpt = userService.login("blockeduser", "blocked123");
        assertTrue(userOpt.isEmpty());
    }

    @Test
    @DisplayName("测试封禁和解封用户功能")
    void testBlockAndUnblockUser() {
        User user = userService.findByUsername("member2").get();
        userService.blockUser(user.getId());
        User blockedUser = userService.findById(user.getId()).get();
        assertEquals(User.UserStatus.BLOCKED, blockedUser.getStatus());

        userService.unblockUser(user.getId());
        User unblockedUser = userService.findById(user.getId()).get();
        assertEquals(User.UserStatus.ACTIVE, unblockedUser.getStatus());
    }

    @Test
    @DisplayName("测试成功修改密码")
    void testChangePasswordSuccess() {
        User user = userService.findByUsername("member1").get();
        assertDoesNotThrow(() -> userService.changePassword(user.getId(), "mem123", "newPass123!"));
        assertTrue(userService.login("member1", "newPass123!").isPresent());
    }

    @Test
    @DisplayName("测试修改密码时旧密码错误")
    void testChangePasswordFailWithWrongOldPassword() {
        User user = userService.findByUsername("member1").get();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(user.getId(), "wrongOldPassword", "newPass123!");
        });
        assertEquals("旧密码不正确", ex.getMessage());
    }

    @Test
    @DisplayName("健壮性测试：注册时使用null或空用户名")
    void testRegisterWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> userService.register(null, "password123!"));
        assertThrows(IllegalArgumentException.class, () -> userService.register(" ", "password123!"));
    }

    @Test
    @DisplayName("健壮性测试：注册时使用null密码")
    void testRegisterWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("newUser", null));
    }
}