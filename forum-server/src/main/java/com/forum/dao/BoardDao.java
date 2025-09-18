package com.forum.dao; // 定义包名

import com.forum.model.Board; // 导入Board模型
import java.util.List; // 导入List接口
import java.util.Optional; // 导入Optional类

/**
 * 版块数据访问对象 (DAO) 接口
 * 定义了所有与版块数据相关的数据库操作标准。
 * @author 李子昂
 * @date 2025-06-27
 */
public interface BoardDao {

    // 根据ID查找版块，返回Optional<Board>处理版块不存在的情况
    Optional<Board> findById(int id);

    // 查找所有版块，返回一个版块列表
    List<Board> findAll();

    // 保存新版块 (创建)，将Board对象持久化
    void save(Board board);

    // 更新版块信息
    void update(Board board);

    // 根据ID删除版块
    void deleteById(int id);
}