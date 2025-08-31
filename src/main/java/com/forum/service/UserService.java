package com.forum.service; // 定义包名

import com.forum.model.User; // 导入User模型
import java.util.Optional; // 导入Optional类

/**
 * 用户服务接口
 * 定义了所有与用户相关的核心业务逻辑。
 * @author 任航瑞
 * @date 2025-06-27
 */
public interface UserService {

    // 注册新用户，包含用户名和密码校验逻辑
    User register(String username, String password);

    // 用户登录，验证凭据并返回用户信息
    Optional<User> login(String username, String password);

    // 封禁指定ID的用户 (管理员权限)
    void blockUser(int userId);

    // 解封指定ID的用户 (管理员权限)
    void unblockUser(int userId);

    // 授予指定用户版主角色 (管理员权限)
    void grantModeratorRole(int userId);

    // 撤销指定用户的版主角色 (管理员权限)
    void revokeModeratorRole(int userId);

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 根据ID查找用户
    Optional<User> findById(int id);

    // 修改指定用户的密码，需要验证旧密码
    void changePassword(int userId, String oldPassword, String newPassword);
}