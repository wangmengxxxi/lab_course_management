package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.common.ErrorCode;
import com.example.lab_course_management.entity.ClassTimeSlot;
import com.example.lab_course_management.entity.Course;
import com.example.lab_course_management.entity.LabCourse;
import com.example.lab_course_management.entity.Laboratory;
import com.example.lab_course_management.exception.BussniesException;
import com.example.lab_course_management.mapper.LabCourseMapper;
import com.example.lab_course_management.model.dto.request.LabCourseRequest;
import com.example.lab_course_management.model.vo.LabCourseVO;
import com.example.lab_course_management.service.*;
import com.example.lab_course_management.utils.CourseScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验室排课服务实现类
 *
 * @author dddwmx
 */
@Service
@Slf4j
public class LabCourseServiceImpl extends ServiceImpl<LabCourseMapper, LabCourse> implements LabCourseService {

    @Lazy
    @Resource
    private CourseService courseService;

    @Resource
    private LaboratoryService laboratoryService;

    @Resource
    private ClassTimeSlotService classTimeSlotService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(LabCourseRequest labCourseRequest) {
        // 1. 参数校验
        validateScheduleRequest(labCourseRequest);

        // 2. 检查关联数据是否存在
        validateRelatedData(labCourseRequest);

        // 3. 检查排课冲突
        if (checkScheduleConflict(labCourseRequest, null)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "排课冲突：该实验室在该时间段已有课程安排");
        }

        // 4. 创建排课记录
        LabCourse labCourse = new LabCourse();
        BeanUtils.copyProperties(labCourseRequest, labCourse);
        labCourse.setIsDelete(0); // 显式设置未删除状态，避免null覆盖数据库默认值

        boolean saveResult = this.save(labCourse);
        if (!saveResult) {
            throw new BussniesException(ErrorCode.SYSTEM_ERROR, "排课失败，数据库错误");
        }

        return labCourse.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long scheduleId) {
        if (scheduleId == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "排课ID不能为空");
        }

        LabCourse labCourse = this.getById(scheduleId);
        if (labCourse == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "排课记录不存在");
        }

        return this.removeById(scheduleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(Long scheduleId, LabCourseRequest labCourseRequest) {
        // 1. 参数校验
        if (scheduleId == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "排课ID不能为空");
        }

        validateScheduleRequest(labCourseRequest);

        // 2. 检查排课记录是否存在
        LabCourse existingSchedule = this.getById(scheduleId);
        if (existingSchedule == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "排课记录不存在");
        }

        // 3. 检查关联数据是否存在
        validateRelatedData(labCourseRequest);

        // 4. 检查排课冲突
        if (checkScheduleConflict(labCourseRequest, scheduleId)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "排课冲突：该实验室在该时间段已有课程安排");
        }

        // 5. 更新排课记录
        LabCourse labCourse = new LabCourse();
        BeanUtils.copyProperties(labCourseRequest, labCourse);
        labCourse.setId(scheduleId);

        return this.updateById(labCourse);
    }

    @Override
    public LabCourseVO getScheduleById(Long scheduleId) {
        if (scheduleId == null) {
            return null;
        }

        LabCourse labCourse = this.getById(scheduleId);
        if (labCourse == null) {
            return null;
        }

        return convertToVO(labCourse);
    }

    @Override
    public IPage<LabCourseVO> getSchedulePage(Page<LabCourse> page, Long labId, Long courseId) {
        QueryWrapper<LabCourse> queryWrapper = new QueryWrapper<>();

        if (labId != null) {
            queryWrapper.eq("lab_id", labId);
        }

        if (courseId != null) {
            queryWrapper.eq("course_id", courseId);
        }

        queryWrapper.orderByDesc("create_time");

        IPage<LabCourse> labCoursePage = this.page(page, queryWrapper);

        // 转换为VO
        IPage<LabCourseVO> voPage = new Page<>();
        BeanUtils.copyProperties(labCoursePage, voPage);

        List<LabCourseVO> voList = new ArrayList<>();
        for (LabCourse labCourse : labCoursePage.getRecords()) {
            voList.add(convertToVO(labCourse));
        }
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public boolean checkScheduleConflict(LabCourseRequest labCourseRequest, Long excludeScheduleId) {
        QueryWrapper<LabCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lab_id", labCourseRequest.getLabId());
        queryWrapper.eq("slot_id", labCourseRequest.getSlotId());
        queryWrapper.eq("day_of_week", labCourseRequest.getDayOfWeek());
        
        // 排除指定的排课记录（用于更新时）
        if (excludeScheduleId != null) {
            queryWrapper.ne("id", excludeScheduleId);
        }

        List<LabCourse> existingSchedules = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(existingSchedules)) {
            return false;
        }


        // 创建新的排课对象用于冲突检测
        LabCourse newLabCourse = new LabCourse();
        BeanUtils.copyProperties(labCourseRequest, newLabCourse);

        // 检查时间区间是否重叠
        for (LabCourse existing : existingSchedules) {
            if (CourseScheduleUtils.hasScheduleConflict(newLabCourse, existing)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<LabCourse> getLabSchedules(Long labId, Integer startWeek, Integer endWeek, Integer dayOfWeek) {
        QueryWrapper<LabCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lab_id", labId);
        queryWrapper.eq("day_of_week", dayOfWeek);

        // 查询时间区间有重叠的排课
        queryWrapper.and(wrapper ->
            wrapper.le("start_week", endWeek).ge("end_week", startWeek)
        );

        queryWrapper.orderByAsc("start_week", "slot_id");

        return this.list(queryWrapper);
    }

    /**
     * 校验排课请求参数
     */
    private void validateScheduleRequest(LabCourseRequest labCourseRequest) {
        if (labCourseRequest == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "排课请求不能为空");
        }

        // 校验周次范围
        if (!CourseScheduleUtils.isValidWeekRange(labCourseRequest.getStartWeek(), labCourseRequest.getEndWeek())) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "周次范围不合法，开始周次不能大于结束周次且范围应在1-20之间");
        }
    }

    /**
     * 校验关联数据是否存在
     */
    private void validateRelatedData(LabCourseRequest labCourseRequest) {
        // 检查课程是否存在
        if (!courseService.isCourseExist(labCourseRequest.getCourseId())) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }

        // 检查实验室是否存在
        if (!laboratoryService.isLabExist(labCourseRequest.getLabId())) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "实验室不存在");
        }

        // 检查时间段是否存在
        if (!classTimeSlotService.isSlotExist(labCourseRequest.getSlotId())) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "时间段不存在");
        }
    }

    /**
     * 转换为VO对象
     */
    private LabCourseVO convertToVO(LabCourse labCourse) {
        LabCourseVO vo = new LabCourseVO();
        BeanUtils.copyProperties(labCourse, vo);

        // 设置星期几文本
        vo.setDayOfWeekText(CourseScheduleUtils.getDayOfWeekText(labCourse.getDayOfWeek()));

        // 设置课程信息
        Course course = courseService.getCourseById(labCourse.getCourseId());
        if (course != null) {
            LabCourseVO.CourseInfoVO courseInfoVO = new LabCourseVO.CourseInfoVO();
            BeanUtils.copyProperties(course, courseInfoVO);
            
            // 设置教师姓名
            if (course.getTeacherId() != null) {
                try {
                    com.example.lab_course_management.entity.User teacher = userService.getUserById(course.getTeacherId());
                    if (teacher != null) {
                        courseInfoVO.setTeacherName(teacher.getRealName());
                    }
                } catch (Exception e) {
                    log.warn("获取教师信息失败: teacherId={}", course.getTeacherId(), e);
                }
            }
            
            vo.setCourse(courseInfoVO);
        }

        // 设置实验室信息
        Laboratory lab = laboratoryService.getLabById(labCourse.getLabId());
        if (lab != null) {
            LabCourseVO.LabInfoVO labInfoVO = new LabCourseVO.LabInfoVO();
            BeanUtils.copyProperties(lab, labInfoVO);
            vo.setLaboratory(labInfoVO);
        }

        // 设置时间段信息
        ClassTimeSlot timeSlot = classTimeSlotService.getSlotById(labCourse.getSlotId());
        if (timeSlot != null) {
            LabCourseVO.TimeSlotInfoVO timeSlotInfoVO = new LabCourseVO.TimeSlotInfoVO();
            BeanUtils.copyProperties(timeSlot, timeSlotInfoVO);
            timeSlotInfoVO.setStartTime(timeSlot.getStartTime().toString());
            timeSlotInfoVO.setEndTime(timeSlot.getEndTime().toString());
            vo.setTimeSlot(timeSlotInfoVO);
        }

        return vo;
    }
}