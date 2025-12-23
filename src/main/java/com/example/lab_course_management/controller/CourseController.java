package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.query.CoursePageQuery;
import com.example.lab_course_management.model.dto.request.CourseAddRequest;
import com.example.lab_course_management.model.dto.request.CourseUpdateRequest;
import com.example.lab_course_management.model.dto.request.ScheduleRequest;
import com.example.lab_course_management.model.vo.CourseDetailVO;
import com.example.lab_course_management.model.vo.CourseVO;
import com.example.lab_course_management.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 课程控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "课程管理", description = "课程管理接口")
@Slf4j
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @SaCheckRole("teacher")
    @Operation(summary = "教师发布课程", description = "教师发布课程接口")
    public Result<Long> addCourse(@Valid @RequestBody CourseAddRequest courseAddRequest) {
        log.info("教师发布课程: {}", courseAddRequest.getCourseName());

        try {
            // 设置教师ID为当前登录用户ID
            Long teacherId = StpUtil.getLoginIdAsLong();
            courseAddRequest.setTeacherId(teacherId);

            Long courseId = courseService.addCourse(courseAddRequest);
            log.info("教师发布课程成功, courseId: {}", courseId);
            return Result.success(courseId, "发布课程成功");
        } catch (Exception e) {
            log.error("教师发布课程失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    @SaCheckRole(value={"admin", "teacher", "student"},mode = SaMode.OR)
    @Operation(summary = "分页查询课程", description = "分页查询课程列表接口,支持按状态筛选")
    public Result<PageResult<CourseVO>> listCoursesByPage(@Valid CoursePageQuery coursePageQuery) {
        log.info("分页查询课程: page={}, size={}, status={}", 
                coursePageQuery.getPageNum(), coursePageQuery.getPageSize(), coursePageQuery.getStatus());

        try {
            PageResult<CourseVO> pageResult = courseService.listCoursesByPage(coursePageQuery);
            log.info("分页查询课程成功: total={}, current={}", pageResult.getTotal(), pageResult.getCurrent());
            return Result.success(pageResult, "查询成功");
        } catch (Exception e) {
            log.error("分页查询课程失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    @SaCheckRole(value = {"admin", "teacher", "student"},mode = SaMode.OR)
    @Operation(summary = "获取课程详情", description = "根据课程ID获取课程详情接口")
    public Result<CourseDetailVO> getCourseDetail(@PathVariable Long id) {
        log.info("获取课程详情: {}", id);

        try {
            CourseDetailVO courseDetail = courseService.getCourseDetail(id);
            if (courseDetail == null) {
                return Result.error("课程不存在");
            }
            log.info("获取课程详情成功: {}", id);
            return Result.success(courseDetail, "查询成功");
        } catch (Exception e) {
            log.error("获取课程详情失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "删除课程", description = "删除课程接口")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        log.info("删除课程: {}", id);

        try {
            // 教师只能删除自己创建的课程
            Long currentUserId = StpUtil.getLoginIdAsLong();
            Boolean result = courseService.deleteCourse(id, currentUserId);
            if (result) {
                log.info("删除课程成功: {}", id);
                return Result.success("删除课程成功");
            } else {
                log.warn("删除课程失败: {}", id);
                return Result.error("删除课程失败");
            }
        } catch (Exception e) {
            log.error("删除课程失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @SaCheckRole({"admin", "teacher"})
    @Operation(summary = "更新课程", description = "更新课程信息接口")
    public Result<Void> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateRequest courseUpdateRequest) {
        log.info("更新课程: {}", id);

        try {
            // 教师只能更新自己创建的课程
            Long currentUserId = StpUtil.getLoginIdAsLong();
            Boolean result = courseService.updateCourse(id, courseUpdateRequest, currentUserId);
            if (result) {
                log.info("更新课程成功: {}", id);
                return Result.success("更新课程成功");
            } else {
                log.warn("更新课程失败: {}", id);
                return Result.error("更新课程失败");
            }
        } catch (Exception e) {
            log.error("更新课程失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/approve-simple")
    @SaCheckRole("admin")
    @Operation(summary = "管理员审批课程(仅改变状态)", description = "管理员审批课程通过,不进行排课")
    public Result<Void> approveCourse(@PathVariable Long id) {
        log.info("管理员审批课程通过: courseId={}", id);

        try {
            Boolean result = courseService.approveCourse(id, 1);
            if (result) {
                log.info("管理员审批课程通过成功: courseId={}", id);
                return Result.success("审批通过");
            } else {
                log.warn("管理员审批课程通过失败: courseId={}", id);
                return Result.error("审批失败");
            }
        } catch (Exception e) {
            log.error("管理员审批课程通过失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/reject")
    @SaCheckRole("admin")
    @Operation(summary = "管理员拒绝课程", description = "管理员拒绝课程申请")
    public Result<Void> rejectCourse(@PathVariable Long id) {
        log.info("管理员拒绝课程: courseId={}", id);

        try {
            Boolean result = courseService.approveCourse(id, 2);
            if (result) {
                log.info("管理员拒绝课程成功: courseId={}", id);
                return Result.success("已拒绝");
            } else {
                log.warn("管理员拒绝课程失败: courseId={}", id);
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("管理员拒绝课程失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/approve")
    @SaCheckRole("admin")
    @Operation(summary = "管理员排课审批", description = "管理员排课审批接口")
    public Result<Void> approveCourseSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleRequest scheduleRequest) {
        log.info("管理员排课审批: courseId={}, labId={}, dayOfWeek={}, slotId={}, startWeek={}, endWeek={}",
                 id, scheduleRequest.getLabId(), scheduleRequest.getDayOfWeek(),
                 scheduleRequest.getSlotId(), scheduleRequest.getStartWeek(), scheduleRequest.getEndWeek());

        try {
            Boolean result = courseService.approveCourseSchedule(id, scheduleRequest);
            if (result) {
                log.info("管理员排课审批成功: courseId={}", id);
                return Result.success("排课审批成功");
            } else {
                log.warn("管理员排课审批失败: courseId={}", id);
                return Result.error("排课审批失败");
            }
        } catch (Exception e) {
            log.error("管理员排课审批失败: {}", e.getMessage());
            throw e;
        }
    }
}