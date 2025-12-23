package com.example.lab_course_management.service;

import com.example.lab_course_management.model.vo.HotCourseVO;
import com.example.lab_course_management.model.vo.LabUsageStatisticsVO;
import com.example.lab_course_management.model.vo.OverviewStatisticsVO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取系统概览统计
     */
    OverviewStatisticsVO getOverviewStatistics();

    /**
     * 获取实验室使用统计
     */
    LabUsageStatisticsVO getLabUsageStatistics();

    /**
     * 获取热门课程TOP10
     */
    List<HotCourseVO> getTop10HotCourses();
}