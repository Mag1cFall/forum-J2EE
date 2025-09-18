package com.forum.dao; // 定义包名

import com.forum.model.User; // 导入User模型
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 用户数据访问对象 (DAO) 接口
 * 定义了所有与用户数据相关的数据库操作标准。
 * @author 刁兴烨
 * @date 2025-06-27
 */
public interface UserDao {

    // 根据ID查找用户，返回一个Optional<User>，以优雅地处理用户不存在的情况
    Optional<User> findById(int id);

    // 根据用户名查找用户，同样返回Optional<User>
    Optional<User> findByUsername(String username);

    // 查找所有用户，返回一个用户列表
    List<User> findAll();

    // 保存新用户 (创建)，将传入的User对象持久化到数据库
    void save(User user);

    // 更新用户信息，根据传入的User对象的ID去更新数据库中的记录
    void update(User user);

    // 根据ID删除用户
    void deleteById(int id);
}