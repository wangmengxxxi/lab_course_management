package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.EnrollRequest;
import com.example.lab_course_management.service.StudentCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生选课控制器（学生端）
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/enrollments")
@Tag(name = "学生选课", description = "学生选课相关接口")
@Slf4j
@RequiredArgsConstructor
public class EnrollmentController {

    private final StudentCourseService studentCourseService;

    @PostMapping
    @SaCheckRole("student")
    @Operation(summary = "学生选课", description = "学生选课接口")
    public Result<Void> enrollCourse(@Valid @RequestBody EnrollRequest enrollRequest) {
        log.info("学生选课: courseId={}", enrollRequest.getCourseId());

        try {
            // 设置学生ID为当前登录用户ID
            Long studentId = StpUtil.getLoginIdAsLong();

            // 检查选课时间冲突
            boolean hasConflict = studentCourseService.checkStudentCourseConflict(enrollRequest.getCourseId(), studentId);
            if (hasConflict) {
                log.warn("学生选课失败，时间冲突: courseId={}, studentId={}", enrollRequest.getCourseId(), studentId);
                return Result.error("选课失败，课程时间冲突");
            }

            // 检查是否已选择该课程
            boolean hasSelected = studentCourseService.isStudentSelectedCourse(enrollRequest.getCourseId(), studentId);
            if (hasSelected) {
                log.warn("学生选课失败，已选择该课程: courseId={}, studentId={}", enrollRequest.getCourseId(), studentId);
                return Result.error("选课失败，已选择该课程");
            }

            Boolean result = studentCourseService.enrollCourse(enrollRequest);
            if (result) {
                log.info("学生选课成功: courseId={}, studentId={}",
                        enrollRequest.getCourseId(), studentId);
                return Result.success("选课成功");
            } else {
                log.warn("学生选课失败: courseId={}, studentId={}", enrollRequest.getCourseId(), studentId);
                return Result.error("选课失败");
            }
        } catch (Exception e) {
            log.error("学生选课失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/my")
    @SaCheckRole("student")
    @Operation(summary = "分页查询我的课表", description = "分页查询我的课表接口")
    public Result<PageResult<?>> getMyScheduleByPage(@Valid BasePageQuery basePageQuery) {
        log.info("分页查询我的课表: page={}, size={}", basePageQuery.getPageNum(), basePageQuery.getPageSize());

        try {
            PageResult<?> pageResult = studentCourseService.getMyScheduleByPage(basePageQuery);
            log.info("分页查询我的课表成功: total={}, current={}", pageResult.getTotal(), pageResult.getCurrent());
            return Result.success(pageResult, "查询成功");
        } catch (Exception e) {
            log.error("分页查询我的课表失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("student")
    @Operation(summary = "学生退课", description = "学生退课接口")
    public Result<Void> dropCourse(@PathVariable Long id) {
        log.info("学生退课: enrollmentId={}", id);

        try {
            // 学生退课，操作人是学生自己
            Long operatorId = StpUtil.getLoginIdAsLong();
            Boolean result = studentCourseService.dropCourse(id, operatorId);
            if (result) {
                log.info("学生退课成功: enrollmentId={}, operatorId={}", id, operatorId);
                return Result.success("退课成功");
            } else {
                log.warn("学生退课失败: enrollmentId={}, operatorId={}", id, operatorId);
                return Result.error("退课失败");
            }
        } catch (Exception e) {
            log.error("学生退课失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/check-conflict/{courseId}")
    @SaCheckRole("student")
    @Operation(summary = "检查选课冲突", description = "检查学生选择指定课程是否存在时间冲突")
    public Result<Boolean> checkCourseConflict(@PathVariable Long courseId) {
        log.info("检查选课冲突: courseId={}", courseId);

        try {
            Long studentId = StpUtil.getLoginIdAsLong();
            boolean hasConflict = studentCourseService.checkStudentCourseConflict(courseId, studentId);
            log.info("检查选课冲突完成: courseId={}, hasConflict={}", courseId, hasConflict);
            return Result.success(hasConflict, hasConflict ? "存在时间冲突" : "无时间冲突");
        } catch (Exception e) {
            log.error("检查选课冲突失败: {}", e.getMessage());
            throw e;
        }
    }
}