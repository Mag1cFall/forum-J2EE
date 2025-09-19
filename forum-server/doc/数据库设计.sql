-- 数据库初始化脚本 By 王绍源、刁兴烨
-- 该脚本被设计为可以重复执行，以重置数据库到初始状态。

-- 1. 按外键依赖的逆序，删除所有表（如果存在）
DROP TABLE IF EXISTS `user_votes`;

DROP TABLE IF EXISTS `comments`;

DROP TABLE IF EXISTS `posts`;

DROP TABLE IF EXISTS `boards`;

DROP TABLE IF EXISTS `users`;

DROP TABLE IF EXISTS `sensitive_words`;

-- 2. 按正确顺序创建表 (从没有外键的表开始)
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `role` ENUM(
        'MEMBER',
        'MODERATOR',
        'ADMIN'
    ) NOT NULL DEFAULT 'MEMBER' COMMENT '角色',
    `status` ENUM('ACTIVE', 'BLOCKED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT = '用户表';

CREATE TABLE `boards` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '版块名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '版块描述',
    `moderator_id` INT DEFAULT NULL COMMENT '版主ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `fk_moderator_id` (`moderator_id`),
    CONSTRAINT `fk_moderator_id` FOREIGN KEY (`moderator_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) COMMENT = '版块表';

CREATE TABLE `posts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL COMMENT '帖子标题',
    `content` TEXT NOT NULL COMMENT '帖子内容',
    `author_id` INT NOT NULL COMMENT '作者ID',
    `board_id` INT NOT NULL COMMENT '所属版块ID',
    `is_pinned` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `dislikes` INT NOT NULL DEFAULT 0 COMMENT '点踩数',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `fk_post_author_id` (`author_id`),
    KEY `fk_post_board_id` (`board_id`),
    CONSTRAINT `fk_post_author_id` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_post_board_id` FOREIGN KEY (`board_id`) REFERENCES `boards` (`id`) ON DELETE CASCADE
) COMMENT = '帖子表';

CREATE TABLE `comments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `content` TEXT NOT NULL COMMENT '评论内容',
    `author_id` INT NOT NULL COMMENT '评论作者ID',
    `post_id` INT NOT NULL COMMENT '所属帖子ID',
    `parent_id` INT DEFAULT NULL COMMENT '父评论ID，用于实现楼中楼',
    `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `dislikes` INT NOT NULL DEFAULT 0 COMMENT '点踩数',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `fk_comment_author_id` (`author_id`),
    KEY `fk_comment_post_id` (`post_id`),
    KEY `fk_comment_parent_id` (`parent_id`),
    CONSTRAINT `fk_comment_author_id` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_post_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) COMMENT = '评论表';

CREATE TABLE `sensitive_words` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `word` VARCHAR(100) NOT NULL UNIQUE COMMENT '敏感词'
) COMMENT = '敏感词表';

CREATE TABLE `user_votes` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `post_id` INT DEFAULT NULL,
    `comment_id` INT DEFAULT NULL,
    `vote_type` ENUM('LIKE', 'DISLIKE') NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vote_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_vote_post_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_vote_comment_id` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_post_vote` (`user_id`, `post_id`),
    UNIQUE KEY `uk_user_comment_vote` (`user_id`, `comment_id`),
    CONSTRAINT `chk_vote_target` CHECK (
        `post_id` IS NOT NULL
        OR `comment_id` IS NOT NULL
    )
) COMMENT = '用户投票记录表';

-- 3. 插入初始数据
INSERT INTO
    `users` (
        `username`,
        `password`,
        `role`,
        `status`
    )
VALUES (
        'admin',
        'admin123',
        'ADMIN',
        'ACTIVE'
    ),
    (
        'moderator',
        'mod123',
        'MODERATOR',
        'ACTIVE'
    ),
    (
        'member1',
        'mem123',
        'MEMBER',
        'ACTIVE'
    ),
    (
        'member2',
        'mem123',
        'MEMBER',
        'ACTIVE'
    ),
    (
        'blockeduser',
        'blocked123',
        'MEMBER',
        'BLOCKED'
    );

INSERT INTO
    `boards` (
        `name`,
        `description`,
        `moderator_id`
    )
VALUES (
        '技术交流',
        '讨论Java, Python, C++等编程技术',
        (
            SELECT id
            FROM users
            WHERE
                username = 'moderator'
        )
    ),
    ('生活分享', '分享日常生活的点点滴滴', 1),
    (
        '站务公告',
        '发布论坛的重要通知',
        (
            SELECT id
            FROM users
            WHERE
                username = 'admin'
        )
    );

INSERT INTO
    `posts` (
        `title`,
        `content`,
        `author_id`,
        `board_id`,
        `is_pinned`
    )
VALUES (
        '【置顶】论坛使用规范',
        '请大家文明交流，遵守社区规则。',
        (
            SELECT id
            FROM users
            WHERE
                username = 'admin'
        ),
        (
            SELECT id
            FROM boards
            WHERE
                name = '站务公告'
        ),
        TRUE
    ),
    (
        'Java是最好的语言吗？',
        '如题，大家来辩论一下，理性讨论，禁止人身攻击。',
        (
            SELECT id
            FROM users
            WHERE
                username = 'member1'
        ),
        (
            SELECT id
            FROM boards
            WHERE
                name = '技术交流'
        ),
        FALSE
    ),
    (
        '今天天气真好',
        '阳光明媚，适合出去走走。',
        (
            SELECT id
            FROM users
            WHERE
                username = 'member2'
        ),
        (
            SELECT id
            FROM boards
            WHERE
                name = '生活分享'
        ),
        FALSE
    ),
    (
        'Python学习求助',
        '请问Python的异步编程应该怎么入门？',
        (
            SELECT id
            FROM users
            WHERE
                username = 'member1'
        ),
        (
            SELECT id
            FROM boards
            WHERE
                name = '技术交流'
        ),
        FALSE
    );

INSERT INTO
    `comments` (
        `content`,
        `author_id`,
        `post_id`
    )
VALUES (
        '同意，Java天下第一！',
        (
            SELECT id
            FROM users
            WHERE
                username = 'member2'
        ),
        (
            SELECT id
            FROM posts
            WHERE
                title = 'Java是最好的语言吗？'
        )
    ),
    (
        '我还是觉得Python更简洁。',
        (
            SELECT id
            FROM users
            WHERE
                username = 'moderator'
        ),
        (
            SELECT id
            FROM posts
            WHERE
                title = 'Java是最好的语言吗？'
        )
    ),
    (
        '沙发！',
        (
            SELECT id
            FROM users
            WHERE
                username = 'admin'
        ),
        (
            SELECT id
            FROM posts
            WHERE
                title = '今天天气真好'
        )
    );

INSERT INTO
    `sensitive_words` (`word`)
VALUES ('敏感词1'),
    ('badword'),
    ('censor'),
    ('bad'),
    ('censorship');