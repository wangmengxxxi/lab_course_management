package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.vo.HotCourseVO;
import com.example.lab_course_management.model.vo.LabUsageStatisticsVO;
import com.example.lab_course_management.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "数据统计", description = "统计数据相关接口")
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取实验室使用统计
     */
    @GetMapping("/lab-usage")
    @SaCheckRole(value={"admin", "teacher"},mode = SaMode.OR)
    @Operation(summary = "获取实验室使用统计", description = "获取各实验室使用率统计（管理员和教师）")
    public Result<LabUsageStatisticsVO> getLabUsageStatistics() {
        LabUsageStatisticsVO labUsageData = statisticsService.getLabUsageStatistics();
        return Result.success(labUsageData);
    }

    /**
     * 获取热门课程TOP10
     */
    @GetMapping("/hot-courses")
    @SaCheckRole(value={"admin", "teacher", "student"},mode = SaMode.OR)
    @Operation(summary = "获取热门课程TOP10", description = "获取选课人数最多的10门课程（所有角色）")
    public Result<List<HotCourseVO>> getTop10HotCourses() {
        List<HotCourseVO> hotCourses = statisticsService.getTop10HotCourses();
        return Result.success(hotCourses);
    }
}