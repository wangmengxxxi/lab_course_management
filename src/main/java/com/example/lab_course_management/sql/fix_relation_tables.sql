-- ====================================================
-- 修复三个关联表：添加自增主键，取消联合主键，添加唯一索引
-- MySQL 8.x
-- ====================================================

-- 1. 用户角色关联表 (user_role)
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                             `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `role_id` BIGINT NOT NULL COMMENT '角色ID',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             UNIQUE KEY `uk_user_role` (`user_id`, `role_id`) COMMENT '用户角色组合唯一',
                             INDEX `idx_user_id` (`user_id`),
                             INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 2. 角色权限关联表 (role_permission)
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
                                   `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                   `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                   `permission_id` BIGINT NOT NULL COMMENT '权限ID',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`) COMMENT '角色权限组合唯一',
                                   INDEX `idx_role_id` (`role_id`),
                                   INDEX `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 3. 学生选课表 (student_course)
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course` (
                                  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                  `course_id` BIGINT NOT NULL COMMENT '课程ID',
                                  `student_id` BIGINT NOT NULL COMMENT '学生ID',
                                  `usual_grade` DECIMAL(5,2) DEFAULT NULL COMMENT '平时成绩',
                                  `final_grade` DECIMAL(5,2) DEFAULT NULL COMMENT '期末成绩',
                                  `grade` DECIMAL(5,2) DEFAULT NULL COMMENT '总成绩',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  UNIQUE KEY `uk_student_course` (`student_id`, `course_id`) COMMENT '学生选课组合唯一',
                                  INDEX `idx_course_id` (`course_id`),
                                  INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生选课与成绩表';

-- ====================================================
-- 插入基础数据的SQL（可选，如果需要保留原有数据）
-- ====================================================

-- 用户角色关联数据示例
-- INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
-- (1, 1), -- 管理员
-- (2, 2), -- 教师
-- (3, 3); -- 学生

-- 角色权限关联数据示例
-- INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
-- (1, 1), (1, 2), (1, 3), -- 管理员拥有所有权限
-- (2, 1), (2, 2),         -- 教师拥有部分权限
-- (3, 1);                  -- 学生拥有基础权限