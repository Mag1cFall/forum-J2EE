package com.forum.service; // 定义包名

import com.forum.model.Board; // 导入Board模型
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 版块服务接口
 * 定义了所有与版块相关的核心业务逻辑。
 * @author 任航瑞
 * @date 2025-06-27
 */
public interface BoardService {

    // 创建一个新版块 (管理员权限)
    Board createBoard(String name, String description, int moderatorId);

    // 根据ID查找版块
    Optional<Board> findById(int id);

    // 获取所有版块的列表
    List<Board> getAllBoards();

    // 更新指定版块的信息 (管理员权限)
    void updateBoard(int boardId, String name, String description);

    // 删除指定ID的版块 (管理员权限)
    void deleteBoard(int boardId);

    // 为指定版块分配或撤销版主 (管理员权限)
    void assignModerator(int boardId, int userId);
}