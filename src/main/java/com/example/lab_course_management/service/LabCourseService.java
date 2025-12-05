package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.entity.LabCourse;
import com.example.lab_course_management.model.dto.request.LabCourseRequest;
import com.example.lab_course_management.model.vo.LabCourseVO;

import java.util.List;

/**
 * 实验室排课服务接口
 *
 * @author dddwmx
 */
public interface LabCourseService extends IService<LabCourse> {

    /**
     * 创建排课
     *
     * @param labCourseRequest 排课请求
     * @return 排课ID
     */
    Long createSchedule(LabCourseRequest labCourseRequest);

    /**
     * 删除排课
     *
     * @param scheduleId 排课ID
     * @return 操作结果
     */
    boolean deleteSchedule(Long scheduleId);

    /**
     * 更新排课
     *
     * @param scheduleId 排课ID
     * @param labCourseRequest 排课请求
     * @return 操作结果
     */
    boolean updateSchedule(Long scheduleId, LabCourseRequest labCourseRequest);

    /**
     * 根据ID获取排课信息
     *
     * @param scheduleId 排课ID
     * @return 排课信息
     */
    LabCourseVO getScheduleById(Long scheduleId);

    /**
     * 分页查询排课信息
     *
     * @param page 分页参数
     * @param labId 实验室ID（可选）
     * @param courseId 课程ID（可选）
     * @return 分页结果
     */
    IPage<LabCourseVO> getSchedulePage(Page<LabCourse> page, Long labId, Long courseId);

    /**
     * 检测排课冲突
     *
     * @param labCourseRequest 排课请求
     * @param excludeScheduleId 排除的排课ID（用于更新时排除自己）
     * @return true-有冲突，false-无冲突
     */
    boolean checkScheduleConflict(LabCourseRequest labCourseRequest, Long excludeScheduleId);

    /**
     * 获取实验室在指定时间段的排课列表
     *
     * @param labId 实验室ID
     * @param startWeek 开始周次
     * @param endWeek 结束周次
     * @param dayOfWeek 星期几
     * @return 排课列表
     */
    List<LabCourse> getLabSchedules(Long labId, Integer startWeek, Integer endWeek, Integer dayOfWeek);
}