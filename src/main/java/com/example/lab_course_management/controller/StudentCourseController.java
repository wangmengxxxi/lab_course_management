package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.query.CourseGradeQuery;
import com.example.lab_course_management.model.dto.request.StudentCourseRequest;
import com.example.lab_course_management.model.vo.StudentCourseGradeVO;
import com.example.lab_course_management.model.vo.StudentCourseVO;
import com.example.lab_course_management.service.CourseService;
import com.example.lab_course_management.service.StudentCourseService;
import com.example.lab_course_management.entity.Course;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学生选课管理控制器（管理端）
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/admin/student-courses")
@Tag(name = "选课管理", description = "管理员/教师选课管理接口")
@Slf4j
@RequiredArgsConstructor
public class StudentCourseController {

    private final StudentCourseService studentCourseService;
    private final CourseService courseService;

    /**
     * 管理员代学生选课
     *
     * @param studentCourseRequest 选课请求
     * @return 操作结果
     */
    @PostMapping("/select")
    @SaCheckRole("admin")
    @Operation(summary = "管理员代选课", description = "管理员代替学生选择课程")
    public Result<Boolean> selectCourse(@RequestBody @Valid StudentCourseRequest studentCourseRequest) {
        log.info("管理员代选课请求: {}", studentCourseRequest);
        boolean result = studentCourseService.selectCourse(studentCourseRequest);
        return Result.success(result, "选课成功");
    }

    /**
     * 管理员帮学生退课
     *
     * @param enrollmentId 选课记录ID
     * @return 操作结果
     */
    @DeleteMapping("/drop/{enrollmentId}")
    @SaCheckRole("admin")
    @Operation(summary = "管理员退课", description = "管理员帮学生退选课程")
    public Result<Boolean> dropCourse(
            @Parameter(description = "选课记录ID") @PathVariable Long enrollmentId) {
        log.info("管理员退课请求: enrollmentId={}", enrollmentId);

        // 管理员退课，操作人是管理员
        Long adminId = StpUtil.getLoginIdAsLong();
        boolean result = studentCourseService.dropCourse(enrollmentId, adminId);

        return Result.success(result, "退课成功");
    }

    /**
     * 获取学生选课列表
     *
     * @param studentId 学生ID（可选）
     * @return 选课列表
     */
    @GetMapping("/student")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "获取学生选课列表", description = "获取指定学生的选课列表")
    public Result<List<StudentCourseVO>> getStudentCourseList(
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId) {
        List<StudentCourseVO> courseList = studentCourseService.getStudentCourseList(studentId);
        return Result.success(courseList);
    }

    /**
     * 获取课程选课学生列表
     *
     * @param courseId 课程ID
     * @return 选课学生列表
     */
    @GetMapping("/course/{courseId}")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "获取课程选课学生列表", description = "获取选择指定课程的学生列表")
    public Result<List<StudentCourseVO>> getCourseStudentList(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<StudentCourseVO> studentList = studentCourseService.getCourseStudentList(courseId);
        return Result.success(studentList);
    }

    /**
     * 分页查询选课记录
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param courseId 课程ID（可选）
     * @param studentId 学生ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "分页查询选课记录", description = "分页查询学生选课记录")
    public Result<IPage<StudentCourseVO>> getStudentCoursePage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId) {

        Page<com.example.lab_course_management.entity.StudentCourse> page = new Page<>(current, size);
        IPage<StudentCourseVO> result = studentCourseService.getStudentCoursePage(page, courseId, studentId);
        return Result.success(result);
    }

    /**
     * 录入学生成绩
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param usualGrade 平时成绩
     * @param finalGrade 期末成绩
     * @return 操作结果
     */
    @PostMapping("/grade/{courseId}/{studentId}")
    @SaCheckRole(value ={"admin", "teacher"} ,mode = SaMode.OR)
    @Operation(summary = "录入学生成绩", description = "为学生的课程录入成绩")
    public Result<Boolean> inputGrade(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "平时成绩") @RequestParam(required = false) BigDecimal usualGrade,
            @Parameter(description = "期末成绩") @RequestParam(required = false) BigDecimal finalGrade) {
        log.info("录入成绩请求: courseId={}, studentId={}, usualGrade={}, finalGrade={}",
            courseId, studentId, usualGrade, finalGrade);
        boolean result = studentCourseService.inputGrade(courseId, studentId, usualGrade, finalGrade);
        return Result.success(result, "成绩录入成功");
    }

    /**
     * 检查选课冲突
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 检查结果
     */
    @GetMapping("/check-conflict")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "检查选课冲突", description = "检查学生选择指定课程是否存在时间冲突")
    public Result<Boolean> checkStudentCourseConflict(
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId) {
        boolean hasConflict = studentCourseService.checkStudentCourseConflict(courseId, studentId);
        return Result.success(hasConflict, hasConflict ? "存在时间冲突" : "无时间冲突");
    }

    /**
     * 获取课程选课人数
     *
     * @param courseId 课程ID
     * @return 选课人数
     */
    @GetMapping("/count/{courseId}")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "获取课程选课人数", description = "获取选择指定课程的学生人数")
    public Result<Long> getCourseStudentCount(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        long count = studentCourseService.getCourseStudentCount(courseId);
        return Result.success(count);
    }

    /**
     * 检查学生是否已选择课程
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 检查结果
     */
    @GetMapping("/check-selected/{courseId}")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "检查是否已选课", description = "检查学生是否已选择指定课程")
    public Result<Boolean> isStudentSelectedCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId) {
        boolean isSelected = studentCourseService.isStudentSelectedCourse(courseId, studentId);
        return Result.success(isSelected, isSelected ? "已选择该课程" : "未选择该课程");
    }

    /**
     * 分页查询课程学生成绩列表
     *
     * @param courseGradeQuery 查询参数
     * @return 学生成绩分页结果
     */
    @GetMapping("/grades")
    @SaCheckRole(value={"admin", "teacher"}, mode = SaMode.OR)
    @Operation(summary = "查询课程学生成绩",
               description = "分页查询指定课程的学生成绩列表，支持学生姓名模糊查询和多种排序方式")
    @Parameter(name = "courseId", description = "课程ID", required = true, example = "1")
    @Parameter(name = "pageNum", description = "页码（从1开始）", required = false, example = "1")
    @Parameter(name = "pageSize", description = "每页大小（1-100）", required = false, example = "10")
    @Parameter(name = "studentName", description = "学生姓名（模糊查询）", required = false, example = "张")
    @Parameter(name = "sortBy", description = "排序字段：grade(总成绩)/usual_grade(平时成绩)/final_grade(期末成绩)",
                required = false, example = "grade")
    @Parameter(name = "sortOrder", description = "排序方式：asc(升序)/desc(降序)", required = false, example = "desc")
    public Result<PageResult<StudentCourseGradeVO>> getCourseGradeList(@Valid @Parameter(hidden = true) CourseGradeQuery courseGradeQuery) {
        log.info("查询课程学生成绩: {}", courseGradeQuery);

        // 权限验证：教师只能查看自己课程的
        String userRole = StpUtil.getRoleList().get(0);
        if (!"admin".equals(userRole)) {
            // 如果是教师，验证是否是该课程的教师
            Long currentUserId = StpUtil.getLoginIdAsLong();
            Course course = courseService.getById(courseGradeQuery.getCourseId());
            if (course == null) {
                return Result.error("课程不存在");
            }
            if (!currentUserId.equals(course.getTeacherId())) {
                return Result.error("无权限查看该课程的成绩");
            }
        }

        PageResult<StudentCourseGradeVO> result = studentCourseService.getCourseGradeListByPage(courseGradeQuery);
        return Result.success(result);
    }
}