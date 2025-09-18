package com.forum.service.impl; // 定义包名

import com.forum.dao.UserDao; // 导入UserDao接口
import com.forum.model.User; // 导入User模型
import com.forum.service.UserService; // 导入UserService接口

import java.util.Optional; // 导入Optional类

/**
 * UserService接口的实现类
 * @author 任航瑞
 * @date 2025-06-27
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao; // 持有一个UserDao的实例，用于数据访问

    // 构造函数，用于依赖注入UserDao
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override // 覆盖接口方法
    public User register(String username, String password) {
        // --- 执行一系列业务规则校验 ---
        // 1. 校验用户名格式
        if (username == null || username.trim().isEmpty() || username.length() < 2 || username.length() > 30) {
            throw new IllegalArgumentException("用户名长度必须在2到30个字符之间");
        }
        // 2. 校验密码格式与复杂度
        if (password == null || password.length() < 6 || password.length() > 30) { // 长度检查
            throw new IllegalArgumentException("密码长度必须在6到30个字符之间");
        }
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?]*$")) { // 检查是否包含非法字符
            throw new IllegalArgumentException("密码只能包含英文字母、数字和常用特殊符号");
        }
        int categoryCount = 0; // 密码种类计数器
        if (password.matches(".*[a-zA-Z]+.*")) categoryCount++; // 检查是否包含字母
        if (password.matches(".*[0-9]+.*")) categoryCount++;    // 检查是否包含数字
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?]+.*")) categoryCount++; // 检查是否包含特殊符号
        if (categoryCount < 2) { // 检查复杂度是否达标
            throw new IllegalArgumentException("密码必须包含字母、数字、特殊符号中的至少两种");
        }
        
        // 3. 检查用户名是否已被占用
        if (userDao.findByUsername(username).isPresent()) { // 调用DAO检查用户名是否存在
            throw new IllegalArgumentException("该用户名已被注册"); // 如果存在，则抛出业务异常
        }
        
        // 4. 创建并保存用户
        User newUser = new User(0, username, password, User.UserRole.MEMBER, User.UserStatus.ACTIVE); // 创建新用户实例，默认为普通成员和活跃状态
        userDao.save(newUser); // 调用DAO层保存用户到数据库
        return newUser; // 返回已保存的用户对象（包含数据库生成的ID）
    }

    @Override // 覆盖接口方法
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userDao.findByUsername(username); // 通过用户名查找用户
        if (userOpt.isPresent()) { // 如果用户存在
            User user = userOpt.get(); // 获取用户对象
            // 校验密码是否正确，且账户状态是否为活跃
            if (user.getPassword().equals(password) && user.getStatus() == User.UserStatus.ACTIVE) {
                return Optional.of(user); // 验证成功，返回包含用户的Optional
            }
        }
        return Optional.empty(); // 用户不存在、密码错误或账户被封禁，均返回空
    }

    @Override // 覆盖接口方法
    public void blockUser(int userId) {
        userDao.findById(userId).ifPresent(user -> { // 查找用户，如果存在则执行lambda表达式
            user.setStatus(User.UserStatus.BLOCKED); // 将用户状态设置为封禁
            userDao.update(user); // 调用DAO层更新数据库
        });
    }

    @Override // 覆盖接口方法
    public void unblockUser(int userId) {
        userDao.findById(userId).ifPresent(user -> { // 查找用户
            user.setStatus(User.UserStatus.ACTIVE); // 将用户状态设置为活跃
            userDao.update(user); // 更新数据库
        });
    }

    @Override // 覆盖接口方法
    public void grantModeratorRole(int userId) {
        userDao.findById(userId).ifPresent(user -> { // 查找用户
            user.setRole(User.UserRole.MODERATOR); // 将角色设置为版主
            userDao.update(user); // 更新数据库
        });
    }

    @Override // 覆盖接口方法
    public void revokeModeratorRole(int userId) {
        userDao.findById(userId).ifPresent(user -> { // 查找用户
            user.setRole(User.UserRole.MEMBER); // 将角色恢复为普通成员
            userDao.update(user); // 更新数据库
        });
    }

    @Override // 覆盖接口方法
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username); // 直接委托DAO层处理
    }

    @Override // 覆盖接口方法
    public Optional<User> findById(int id) {
        return userDao.findById(id); // 直接委托DAO层处理
    }

    @Override
    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDao.findById(userId) // 根据ID查找用户
                .orElseThrow(() -> new IllegalArgumentException("用户不存在")); // 如果找不到用户，则抛出异常

        // 1. 验证旧密码是否正确
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        // 2. 验证新密码是否与旧密码相同
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }

        // 3. 复用注册时的密码复杂度校验逻辑
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 30) {
            throw new IllegalArgumentException("新密码长度必须在6到30个字符之间");
        }
        if (!newPassword.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?]*$")) {
            throw new IllegalArgumentException("密码只能包含英文字母、数字和常用特殊符号");
        }
        int categoryCount = 0;
        if (newPassword.matches(".*[a-zA-Z]+.*")) categoryCount++;
        if (newPassword.matches(".*[0-9]+.*")) categoryCount++;
        if (newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?]+.*")) categoryCount++;
        if (categoryCount < 2) {
            throw new IllegalArgumentException("新密码必须包含字母、数字、特殊符号中的至少两种");
        }

        // 4. 所有验证通过后，更新密码
        user.setPassword(newPassword); // 设置新密码
        userDao.update(user); // 调用DAO层持久化更改
    }
}