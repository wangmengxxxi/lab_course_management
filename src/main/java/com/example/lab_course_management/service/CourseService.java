package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.Course;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.CourseAddRequest;
import com.example.lab_course_management.model.dto.request.CourseUpdateRequest;
import com.example.lab_course_management.model.dto.request.ScheduleRequest;
import com.example.lab_course_management.model.vo.CourseDetailVO;
import com.example.lab_course_management.model.vo.CourseVO;

/**
 * 课程服务接口
 *
 * @author dddwmx
 */
public interface CourseService extends IService<Course> {

    /**
     * 根据ID获取课程信息
     *
     * @param courseId 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long courseId);

    /**
     * 检查课程是否存在
     *
     * @param courseId 课程ID
     * @return true-存在，false-不存在
     */
    boolean isCourseExist(Long courseId);

    /**
     * 教师发布课程
     *
     * @param courseAddRequest 课程添加请求
     * @return 课程ID
     */
    Long addCourse(CourseAddRequest courseAddRequest);

    /**
     * 管理员审批课程(仅改变状态)
     *
     * @param courseId 课程ID
     * @param status 状态(1-通过, 2-拒绝)
     * @return 是否成功
     */
    Boolean approveCourse(Long courseId, Integer status);

    /**
     * 管理员排课审批
     *
     * @param courseId 课程ID
     * @param scheduleRequest 排课请求
     * @return 是否成功
     */
    Boolean approveCourseSchedule(Long courseId, ScheduleRequest scheduleRequest);

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    CourseDetailVO getCourseDetail(Long courseId);

    /**
     * 分页查询课程列表
     *
     * @param coursePageQuery 分页查询参数(支持状态筛选)
     * @return 课程分页结果
     */
    PageResult<CourseVO> listCoursesByPage(com.example.lab_course_management.model.dto.query.CoursePageQuery coursePageQuery);

    /**
     * 删除课程
     *
     * @param courseId 课程ID
     * @param currentUserId 当前操作用户ID
     * @return 是否成功
     */
    Boolean deleteCourse(Long courseId, Long currentUserId);

    /**
     * 更新课程信息
     *
     * @param courseId 课程ID
     * @param courseUpdateRequest 课程更新请求
     * @param currentUserId 当前操作用户ID
     * @return 是否成功
     */
    Boolean updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest, Long currentUserId);
}