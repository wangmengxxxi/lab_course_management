-- ====================================================
-- 实验室课程管理系统 - 最终修订版 SQL
-- MySQL 8.x
-- 修改点：Course表增加 status, max_students
-- ====================================================

-- 1. 用户表 (user)
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `user_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `account` VARCHAR(50) NOT NULL,
                        `password` VARCHAR(100) NOT NULL,
                        `real_name` VARCHAR(50) DEFAULT NULL,
                        `email` VARCHAR(100) DEFAULT NULL,
                        `phone` VARCHAR(20) DEFAULT NULL,
                        `status` TINYINT DEFAULT 1 COMMENT '账号状态（1正常，0禁用）',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_delete` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                        UNIQUE KEY `uk_user_username` (`username`),
                        INDEX `idx_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 实验室表 (laboratory)
DROP TABLE IF EXISTS `laboratory`;
CREATE TABLE `laboratory` (
                              `lab_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                              `lab_name` VARCHAR(100) NOT NULL,
                              `location` VARCHAR(100) DEFAULT NULL,
                              `capacity` INT DEFAULT NULL COMMENT '实验室物理容量（人数）',
                              `manager_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
                              `description` VARCHAR(255) DEFAULT NULL,
                              `status` TINYINT DEFAULT 0 COMMENT '0=可用，1=维护，2=禁用',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `is_delete` TINYINT DEFAULT 0,
                              UNIQUE KEY `uk_lab_name` (`lab_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室表';

-- 3. 角色与权限相关表 (role, permission, relations)
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                        `role_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `role_name` VARCHAR(50) NOT NULL,
                        `role_desc` VARCHAR(255) DEFAULT NULL,
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_delete` TINYINT DEFAULT 0,
                        UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
                              `permission_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                              `perm_code` VARCHAR(50) NOT NULL COMMENT '权限标识 (e.g. course:create)',
                              `perm_name` VARCHAR(50) NOT NULL,
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `is_delete` TINYINT DEFAULT 0,
                              UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                             `user_id` BIGINT NOT NULL,
                             `role_id` BIGINT NOT NULL,
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
                                   `role_id` BIGINT NOT NULL,
                                   `permission_id` BIGINT NOT NULL,
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 4. 上课时间段表 (class_time_slot)
DROP TABLE IF EXISTS `class_time_slot`;
CREATE TABLE `class_time_slot` (
                                   `slot_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   `slot_name` VARCHAR(50) NOT NULL COMMENT '例如：第1-2节',
                                   `start_time` TIME NOT NULL,
                                   `end_time` TIME NOT NULL,
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `is_delete` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段字典表';

-- 5. 课程表 (course) [核心修改]
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
                          `course_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `course_name` VARCHAR(100) NOT NULL,
                          `teacher_id` BIGINT NOT NULL COMMENT '责任教师',
                          `semester` VARCHAR(20) NOT NULL COMMENT '学期（2025-Spring）',
                          `description` VARCHAR(255) DEFAULT NULL,
                          `credit` INT DEFAULT NULL COMMENT '学分',
                          `max_students` INT DEFAULT 50 COMMENT '【新增】课程最大选课人数',
                          `status` TINYINT DEFAULT 0 COMMENT '【新增】0=待审批, 1=已发布/审批通过, 2=驳回',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `is_delete` TINYINT DEFAULT 0,
                          INDEX `idx_course_teacher` (`teacher_id`),
                          INDEX `idx_course_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程基本信息表';

-- 6. 实验室课程排课表 (lab_course)
DROP TABLE IF EXISTS `lab_course`;
CREATE TABLE `lab_course` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                              `course_id` BIGINT NOT NULL,
                              `lab_id` BIGINT NOT NULL,
                              `teacher_id` BIGINT DEFAULT NULL COMMENT '该时间段的授课老师（可为空，空则同course表）',
                              `slot_id` BIGINT NOT NULL COMMENT '时间段ID',
                              `start_week` TINYINT NOT NULL COMMENT '起始周',
                              `end_week` TINYINT NOT NULL COMMENT '结束周',
                              `day_of_week` TINYINT NOT NULL COMMENT '周几(1-7)',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `is_delete` TINYINT DEFAULT 0,
                              INDEX `idx_lc_conflict` (`lab_id`, `day_of_week`, `slot_id`),
                              INDEX `idx_lc_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课表（描述具体上课时间和地点）';

-- 7. 学生选课表 (student_course)
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course` (
                                  `course_id` BIGINT NOT NULL,
                                  `student_id` BIGINT NOT NULL,
                                  `usual_grade` DECIMAL(5,2) DEFAULT NULL COMMENT '平时分',
                                  `final_grade` DECIMAL(5,2) DEFAULT NULL COMMENT '期末分',
                                  `grade` DECIMAL(5,2) DEFAULT NULL COMMENT '总成绩',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`course_id`, `student_id`),
                                  INDEX `idx_sc_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生选课与成绩表';

-- 8. 公告表 (announcement)
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
                                `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                `title` VARCHAR(200) NOT NULL,
                                `content` TEXT NOT NULL,
                                `publisher_id` BIGINT DEFAULT NULL,
                                `publish_time` DATETIME DEFAULT NULL COMMENT '实际发布时间',
                                `schedule_time` DATETIME DEFAULT NULL COMMENT '预约发布时间',
                                `status` TINYINT DEFAULT 0 COMMENT '0=草稿/未到期, 1=已发布, 2=已撤回',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `is_delete` TINYINT DEFAULT 0,
                                INDEX `idx_announce_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';