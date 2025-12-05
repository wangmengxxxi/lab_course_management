package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.lab_course_management.entity.Course;
import com.example.lab_course_management.mapper.CourseMapper;
import com.example.lab_course_management.mapper.LaboratoryMapper;
import com.example.lab_course_management.model.vo.HotCourseVO;
import com.example.lab_course_management.model.vo.LabUsageStatisticsVO;
import com.example.lab_course_management.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 统计服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final CourseMapper courseMapper;
    private final LaboratoryMapper laboratoryMapper;

    
    @Override
    public LabUsageStatisticsVO getLabUsageStatistics() {
        log.info("获取实验室使用统计");

        LabUsageStatisticsVO result = new LabUsageStatisticsVO();

        // 获取实验室使用情况
        List<Map<String, Object>> labUsageData = laboratoryMapper.getLabUsageStats();

        List<LabUsageStatisticsVO.LabUsageVO> labUsageList = new ArrayList<>();
        Integer totalCapacity = 0;
        Integer totalActualUsage = 0;
        Integer totalIdleCapacity = 0;

        for (Map<String, Object> data : labUsageData) {
            LabUsageStatisticsVO.LabUsageVO labUsage = new LabUsageStatisticsVO.LabUsageVO();

            Long labId = ((Number) data.get("labId")).longValue();
            String labName = (String) data.get("labName");
            Integer capacity = ((Number) data.get("capacity")).intValue();
            Integer actualUsage = ((Number) data.get("actualUsage")).intValue();

            labUsage.setLabId(labId);
            labUsage.setLabName(labName);
            labUsage.setCapacity(capacity);
            labUsage.setActualUsage(actualUsage);

            // 计算使用率和空闲容量
            Double usageRate = capacity > 0 ? (actualUsage * 100.0 / capacity) : 0.0;
            Integer idleCapacity = capacity - actualUsage;

            labUsage.setUsageRate(usageRate);
            labUsage.setIdleCapacity(idleCapacity);

            labUsageList.add(labUsage);

            totalCapacity += capacity;
            totalActualUsage += actualUsage;
            totalIdleCapacity += idleCapacity;
        }

        // 计算总体统计
        Double averageUsageRate = totalCapacity > 0 ? (totalActualUsage * 100.0 / totalCapacity) : 0.0;
        Double idleRate = totalCapacity > 0 ? (totalIdleCapacity * 100.0 / totalCapacity) : 0.0;

        result.setLabUsageList(labUsageList);
        result.setAverageUsageRate(Math.round(averageUsageRate * 100.0) / 100.0);
        result.setIdleRate(Math.round(idleRate * 100.0) / 100.0);

        return result;
    }

    @Override
    public List<HotCourseVO> getTop10HotCourses() {
        log.info("获取热门课程TOP10");

        List<Map<String, Object>> hotCourseData = courseMapper.getTop10HotCourses();
        List<HotCourseVO> hotCourses = new ArrayList<>();

        for (Map<String, Object> data : hotCourseData) {
            HotCourseVO hotCourse = new HotCourseVO();

            Long courseId = ((Number) data.get("courseId")).longValue();
            String courseName = (String) data.get("courseName");
            Integer enrollmentCount = ((Number) data.get("enrollmentCount")).intValue();

            hotCourse.setCourseId(courseId);
            hotCourse.setCourseName(courseName);
            hotCourse.setEnrollmentCount(enrollmentCount);
            // TODO: 可以关联查询获取教师名称
            hotCourse.setTeacherName("教师名称");

            hotCourses.add(hotCourse);
        }

        return hotCourses;
    }
}