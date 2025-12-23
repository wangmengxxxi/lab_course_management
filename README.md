# 实验室课程管理系统

## 项目简介

实验室课程管理系统是一个基于 Spring Boot 3.x 的实验室排课与选课管理平台。系统旨在解决高校实验室资源分配、课程排课、学生选课、成绩管理等核心业务需求，提供完整的实验室教学管理解决方案。

## 技术架构

### 后端技术栈

- **框架**: Spring Boot 3.2.2
- **Java版本**: JDK 17
- **数据库**: MySQL 8.x
- **缓存**: Redis
- **ORM框架**: MyBatis-Plus 3.5.8
- **认证授权**: Sa-Token 1.37.0
- **API文档**: Knife4j 4.4.0 (Swagger)
- **工具库**:
  - Hutool 5.8.22
  - Apache Commons Lang3
  - Lombok
  - Spring Boot Validation

### 项目结构

```
src/main/java/com/example/lab_course_management/
├── auth/                    # 认证授权模块
│   └── StpInterfaceImpl.java    # Sa-Token权限接口实现
├── common/                  # 公共组件
│   ├── ErrorCode.java           # 统一错误码定义
│   ├── PageResult.java          # 分页结果封装
│   └── Result.java              # 统一返回结果封装
├── config/                  # 配置类
│   ├── MybatisPlusConfig.java   # MyBatis-Plus配置
│   ├── RedisConfig.java         # Redis配置
│   ├── SaTokenConfig.java       # Sa-Token配置
│   ├── ScheduleConfig.java      # 定时任务配置
│   └── SwaggerConfig.java       # Swagger文档配置
├── controller/              # 控制器层
│   ├── UserController.java      # 用户管理
│   ├── LabController.java       # 实验室管理
│   ├── CourseController.java    # 课程管理
│   ├── LabCourseController.java # 实验室排课管理
│   ├── StudentCourseController.java # 学生选课管理
│   └── EnrollmentController.java # 选课管理
├── entity/                  # 实体类
│   ├── User.java               # 用户实体
│   ├── Role.java               # 角色实体
│   ├── Permission.java         # 权限实体
│   ├── Laboratory.java         # 实验室实体
│   ├── Course.java             # 课程实体
│   ├── LabCourse.java          # 实验室排课实体
│   ├── StudentCourse.java      # 学生选课实体
│   └── ClassTimeSlot.java      # 时间段实体
├── mapper/                  # 数据访问层（MyBatis-Plus）
├── service/                 # 业务逻辑层
│   ├── UserService.java
│   ├── CourseService.java
│   ├── LabCourseService.java
│   └── StudentCourseService.java
├── model/                   # 数据传输对象
│   ├── dto/                 # 请求对象
│   │   ├── request/         # 请求DTO
│   │   └── query/           # 查询DTO
│   └── vo/                  # 视图对象
├── exception/               # 异常处理
│   ├── BussniesException.java      # 业务异常
│   └── GlobalExceptionHandler.java # 全局异常处理器
├── utils/                   # 工具类
│   ├── DigestUtils.java         # 加密工具
│   └── StudentScheduleUtils.java # 学生课表冲突检测
└── task/                    # 定时任务
    └── AnnouncementScheduledTask.java # 公告定时发布
```

## 数据库设计

### 核心数据表

1. **用户表 (user)**
   - 存储系统用户信息（管理员、教师、学生）
   - 支持账号状态管理
   - 包含真实姓名、邮箱、手机号等基本信息

2. **权限管理表**
   - **角色表 (role)**: 定义系统角色（管理员、教师、学生）
   - **权限表 (permission)**: 定义系统权限粒度
   - **用户角色关联表 (user_role)**: 用户角色多对多关系
   - **角色权限关联表 (role_permission)**: 角色权限多对多关系

3. **实验室表 (laboratory)**
   - 实验室基本信息（名称、位置、容量）
   - 实验室状态管理（可用、维护、禁用）
   - 支持实验室负责人配置

4. **课程表 (course)**
   - 课程基本信息（名称、学分、描述）
   - 课程状态管理（待审批、已发布、驳回）
   - 支持最大选课人数限制

5. **实验室排课表 (lab_course)**
   - 核心排课信息
   - 定义具体的上课时间、地点、教师
   - 支持周次、星期、时间段等详细时间配置
   - 内置时间冲突检测机制

6. **学生选课表 (student_course)**
   - 学生选课记录
   - 成绩管理（平时成绩、期末成绩、总成绩）
   - 自动计算总成绩（平时30% + 期末70%）

7. **时间段表 (class_time_slot)**
   - 标准化时间段定义（如：第1-2节）
   - 用于排课的时间标准化

8. **公告表 (announcement)**
   - 系统公告发布
   - 支持定时发布功能
   - 公告状态管理（草稿、已发布、已撤回）

## 系统功能模块

### 1. 用户管理模块

- **用户注册/登录**
  - 支持多角色注册（管理员、教师、学生）
  - 基于Sa-Token的无状态认证
  - Redis会话管理

- **用户信息管理**
  - 个人信息查看与修改
  - 密码修改功能
  - 账号状态管理

### 2. 权限管理模块

- **RBAC权限模型**
  - 基于角色的访问控制
  - 精细化权限粒度管理
  - 动态权限验证

- **角色管理**
  - 系统角色定义（管理员、教师、学生）
  - 角色权限配置
  - 用户角色分配

### 3. 实验室管理模块

- **实验室信息管理**
  - 实验室基本信息录入
  - 实验室容量配置
  - 实验室状态管理（可用/维护/禁用）

- **实验室分配**
  - 支持实验室负责人配置
  - 实验室使用情况统计

### 4. 课程管理模块

- **课程创建与审批**
  - 教师创建课程申请
  - 管理员审批课程
  - 课程发布管理

- **课程信息管理**
  - 课程基本信息维护
  - 学分管理
  - 最大选课人数设置

### 5. 排课管理模块

- **实验室排课**
  - 可视化排课界面
  - 时间段标准化管理
  - 自动时间冲突检测

- **排课优化**
  - 智能排课建议
  - 实验室资源利用率统计
  - 排课信息导出

### 6. 选课管理模块

- **学生选课**
  - 课程浏览与搜索
  - 在线选课功能
  - 选课时间冲突检测

- **选课控制**
  - 选课人数限制
  - 选课时间窗口控制
  - 退课管理

### 7. 成绩管理模块

- **成绩录入**
  - 平时成绩录入
  - 期末成绩录入
  - 自动计算总成绩

- **成绩查询**
  - 学生成绩查询
  - 教师成绩管理
  - 成绩统计分析

### 8. 公告管理模块

- **公告发布**
  - 系统公告创建
  - 定时发布功能
  - 公告状态管理

- **公告通知**
  - 实时公告推送
  - 历史公告查询
  - 公告分类管理

## 接口文档

系统集成了 Knife4j (Swagger) API文档，启动项目后可访问：
- **API文档地址**: http://localhost:8080/doc.html
- **UI界面**: 中文界面，支持在线测试

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone [项目地址]
cd lab_course_management
```

2. **数据库配置**
```sql
-- 创建数据库
CREATE DATABASE lab_course_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 执行SQL脚本
-- src/main/java/com/example/lab_course_management/sql/create.sql
-- src/main/java/com/example/lab_course_management/sql/insert.sql
```

3. **配置文件修改**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lab_course_management
    username: [你的数据库用户名]
    password: [你的数据库密码]

  data:
    redis:
      host: [Redis服务器地址]
      port: [Redis端口]
```

4. **启动项目**
```bash
mvn spring-boot:run
```

5. **访问系统**
- API文档: http://localhost:8080/doc.html
- 健康检查: http://localhost:8080/actuator/health

### 测试账号

系统提供以下测试账号：
- 管理员: admin / 12345678
- 教师: teacher_wang / 123456
- 学生: student_zhang / 123456

## 系统特性

### 技术特性

1. **微服务架构设计**
   - 模块化设计，易于扩展
   - 统一异常处理
   - 全局参数验证

2. **高性能设计**
   - Redis缓存支持
   - 数据库连接池
   - 分页查询优化

3. **安全性保障**
   - Sa-Token无状态认证
   - RBAC权限控制
   - 密码加密存储
   - SQL注入防护

4. **可维护性**
   - 统一日志管理
   - 完整的API文档
   - 代码注释规范

### 业务特性

1. **智能排课**
   - 自动时间冲突检测
   - 实验室资源优化分配
   - 排课信息可视化

2. **灵活选课**
   - 在线选课功能
   - 选课时间窗口控制
   - 课程容量限制

3. **成绩管理**
   - 多维度成绩录入
   - 自动成绩计算
   - 成绩统计分析

4. **系统通知**
   - 定时公告发布
   - 实时消息推送
   - 多渠道通知支持


## 版本历史

- **v1.0.0** - 初始版本
  - 基础用户管理功能
  - 实验室管理功能
  - 课程排课功能
  - 学生选课功能
  - 成绩管理功能

## 联系方式
- 项目维护者: dddwmx

*最后更新: 2025-12-05*