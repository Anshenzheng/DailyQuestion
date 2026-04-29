-- DailyQ 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS dailyq DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dailyq;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT '微信openid',
    union_id VARCHAR(100) DEFAULT NULL COMMENT '微信unionid',
    nickname VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    avatar_url VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 问题表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID',
    content TEXT NOT NULL COMMENT '问题内容',
    category VARCHAR(50) DEFAULT 'daily' COMMENT '问题分类：daily日常，work工作，life生活，emotion情感',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    used_count INT DEFAULT 0 COMMENT '使用次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 每日问题表（记录每日推送的问题）
CREATE TABLE IF NOT EXISTS daily_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    question_date DATE NOT NULL COMMENT '问题日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_question_date (question_date),
    INDEX idx_question_id (question_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日问题表';

-- 回答表
CREATE TABLE IF NOT EXISTS answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回答ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    daily_question_id BIGINT NOT NULL COMMENT '每日问题ID',
    content TEXT COMMENT '回答内容',
    image_url VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    answer_date DATE NOT NULL COMMENT '回答日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_answer_date (answer_date),
    INDEX idx_user_date (user_id, answer_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (daily_question_id) REFERENCES daily_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 管理员表
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 初始化一些示例问题
INSERT INTO questions (content, category, status) VALUES 
('今天最开心的事是什么？', 'daily', 1),
('今天最想感谢的人是谁？', 'emotion', 1),
('今天学到了什么新东西？', 'life', 1),
('今天有什么让你感到骄傲的成就？', 'daily', 1),
('今天遇到了什么有趣的事情？', 'daily', 1),
('今天最想对自己说的一句话是什么？', 'emotion', 1),
('今天吃了什么好吃的？', 'life', 1),
('今天最放松的时刻是什么时候？', 'daily', 1),
('今天和谁进行了有意义的交流？', 'emotion', 1),
('今天完成了哪些计划中的事情？', 'work', 1),
('今天有什么小小的惊喜？', 'daily', 1),
('今天最让你感动的瞬间是什么？', 'emotion', 1),
('今天你帮助了谁？', 'life', 1),
('今天你最期待的事情是什么？', 'daily', 1),
('今天的天气如何影响了你的心情？', 'life', 1);

-- 初始化管理员账号（密码：admin123，需要在应用启动时加密）
INSERT INTO admins (username, password, nickname, status) VALUES 
('admin', 'admin123', '超级管理员', 1);
