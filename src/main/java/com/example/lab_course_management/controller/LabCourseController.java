package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.request.LabCourseRequest;
import com.example.lab_course_management.model.vo.LabCourseVO;
import com.example.lab_course_management.service.LabCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 实验室排课控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/lab-course")
@Tag(name = "实验室排课管理", description = "实验室排课相关接口")
@Slf4j
@RequiredArgsConstructor
public class LabCourseController {

    private final LabCourseService labCourseService;

    /**
     * 创建排课
     *
     * @param labCourseRequest 排课请求
     * @return 排课ID
     */
    @PostMapping
    @SaCheckRole("admin")
    @Operation(summary = "创建排课", description = "管理员创建实验室排课")
    public Result<Long> createSchedule(@RequestBody @Valid LabCourseRequest labCourseRequest) {
        log.info("创建排课请求: {}", labCourseRequest);
        Long scheduleId = labCourseService.createSchedule(labCourseRequest);
        return Result.success(scheduleId, "排课成功");
    }

    /**
     * 删除排课
     *
     * @param scheduleId 排课ID
     * @return 操作结果
     */
    @DeleteMapping("/{scheduleId}")
    @SaCheckRole("admin")
    @Operation(summary = "删除排课", description = "管理员删除指定排课记录")
    public Result<Boolean> deleteSchedule(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        log.info("删除排课请求: scheduleId={}", scheduleId);
        boolean result = labCourseService.deleteSchedule(scheduleId);
        return Result.success(result, "删除成功");
    }

    /**
     * 更新排课
     *
     * @param scheduleId 排课ID
     * @param labCourseRequest 排课请求
     * @return 操作结果
     */
    @PutMapping("/{scheduleId}")
    @SaCheckRole("admin")
    @Operation(summary = "更新排课", description = "管理员更新指定排课记录")
    public Result<Boolean> updateSchedule(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId,
            @RequestBody @Valid LabCourseRequest labCourseRequest) {
        log.info("更新排课请求: scheduleId={}, {}", scheduleId, labCourseRequest);
        boolean result = labCourseService.updateSchedule(scheduleId, labCourseRequest);
        return Result.success(result, "更新成功");
    }

    /**
     * 根据ID获取排课信息
     *
     * @param scheduleId 排课ID
     * @return 排课信息
     */
    @GetMapping("/{scheduleId}")
    @SaCheckRole(value = {"admin", "teacher", "student"}, mode = SaMode.OR)
    @Operation(summary = "获取排课信息", description = "根据ID获取排课详细信息")
    public Result<LabCourseVO> getScheduleById(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        LabCourseVO schedule = labCourseService.getScheduleById(scheduleId);
        return Result.success(schedule);
    }

    /**
     * 分页查询排课信息
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param labId 实验室ID（可选）
     * @param courseId 课程ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @SaCheckRole(value = {"admin", "teacher", "student"}, mode = SaMode.OR)
    @Operation(summary = "分页查询排课", description = "分页查询排课信息")
    public Result<IPage<LabCourseVO>> getSchedulePage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "实验室ID") @RequestParam(required = false) Long labId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {

        Page<com.example.lab_course_management.entity.LabCourse> page = new Page<>(current, size);
        IPage<LabCourseVO> result = labCourseService.getSchedulePage(page, labId, courseId);
        return Result.success(result);
    }

    /**
     * 检测排课冲突
     *
     * @param labCourseRequest 排课请求
     * @return 检测结果
     */
    @PostMapping("/check-conflict")
    @SaCheckRole(value = {"admin", "teacher"}, mode = SaMode.OR)
    @Operation(summary = "检测排课冲突", description = "检测指定时间和实验室是否有排课冲突")
    public Result<Boolean> checkScheduleConflict(@RequestBody @Valid LabCourseRequest labCourseRequest) {
        log.info("检测排课冲突请求: {}", labCourseRequest);
        boolean hasConflict = labCourseService.checkScheduleConflict(labCourseRequest, null);
        return Result.success(hasConflict, hasConflict ? "存在排课冲突" : "无排课冲突");
    }

    /**
     * 获取实验室排课列表
     *
     * @param labId 实验室ID
     * @param startWeek 开始周次
     * @param endWeek 结束周次
     * @param dayOfWeek 星期几
     * @return 排课列表
     */
    @GetMapping("/lab/{labId}")
    @SaCheckRole(value = {"admin", "teacher", "student"}, mode = SaMode.OR)
    @Operation(summary = "获取实验室排课列表", description = "获取指定实验室在指定时间的排课列表")
    public Result<List<LabCourseVO>> getLabSchedules(
            @Parameter(description = "实验室ID") @PathVariable Long labId,
            @Parameter(description = "开始周次") @RequestParam Integer startWeek,
            @Parameter(description = "结束周次") @RequestParam Integer endWeek,
            @Parameter(description = "星期几") @RequestParam Integer dayOfWeek) {

        List<com.example.lab_course_management.entity.LabCourse> schedules =
            labCourseService.getLabSchedules(labId, startWeek, endWeek, dayOfWeek);

        List<LabCourseVO> voList = schedules.stream()
            .map(schedule -> labCourseService.getScheduleById(schedule.getId()))
            .collect(java.util.stream.Collectors.toList());

        return Result.success(voList);
    }
}