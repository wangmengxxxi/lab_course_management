package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.Course;
import com.example.lab_course_management.entity.LabCourse;
import com.example.lab_course_management.entity.Laboratory;
import com.example.lab_course_management.entity.User;
import com.example.lab_course_management.entity.StudentCourse;
import com.example.lab_course_management.entity.ClassTimeSlot;
import com.example.lab_course_management.mapper.CourseMapper;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.CourseAddRequest;
import com.example.lab_course_management.model.dto.request.CourseUpdateRequest;
import com.example.lab_course_management.model.dto.request.LabCourseRequest;
import com.example.lab_course_management.model.dto.request.ScheduleRequest;
import com.example.lab_course_management.model.vo.CourseDetailVO;
import com.example.lab_course_management.model.vo.CourseVO;
import com.example.lab_course_management.service.*;
import com.example.lab_course_management.utils.CourseScheduleUtils;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.util.StringUtils.hasText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 *
 * @author dddwmx
 */
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private ClassTimeSlotService classTimeSlotService;

    @Resource
    private LaboratoryService laboratoryService;

    @Resource
    private UserService userService;

    @Resource
    private StudentCourseService studentCourseService;

    @Lazy
    @Resource
    private LabCourseService labCourseService;

    @Override
    public Course getCourseById(Long courseId) {
        if (courseId == null) {
            return null;
        }
        return this.getById(courseId);
    }

    @Override
    public boolean isCourseExist(Long courseId) {
        if (courseId == null) {
            return false;
        }
        Course course = this.getById(courseId);
        return course != null;
    }

    @Override
    @Transactional
    public Long addCourse(CourseAddRequest courseAddRequest) {
        // 1. 校验参数
        if (courseAddRequest == null || courseAddRequest.getCourseName() == null || courseAddRequest.getSemester() == null) {
            throw new IllegalArgumentException("课程名称和学期不能为空");
        }

        // 2. 检查教师ID是否存在
        if (courseAddRequest.getTeacherId() != null) {
            // TODO: 验证教师角色和存在性
        }

        // 3. 创建课程对象
        Course course = new Course();
        course.setCourseName(courseAddRequest.getCourseName());
        course.setTeacherId(courseAddRequest.getTeacherId());
        course.setSemester(courseAddRequest.getSemester());
        course.setDescription(courseAddRequest.getDescription());
        course.setCredit(courseAddRequest.getCredit());
        course.setMaxStudents(courseAddRequest.getMaxStudents());
        course.setStatus(0); // 默认待审批状态
        course.setIsDelete(0); // 显式设置未删除状态，避免null覆盖数据库默认值

        // 4. 保存课程
        boolean saveResult = this.save(course);
        if (!saveResult) {
            throw new RuntimeException("发布课程失败，数据库错误");
        }

        return course.getCourseId();
    }

    @Override
    @Transactional
    public Boolean approveCourseSchedule(Long courseId, ScheduleRequest scheduleRequest) {
        // 1. 校验参数
        if (courseId == null || scheduleRequest == null || scheduleRequest.getLabId() == null) {
            throw new IllegalArgumentException("课程ID和实验室ID不能为空");
        }

        // 2. 查询课程是否存在
        Course course = this.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        // 3. 检查实验室是否存在
        if (!laboratoryService.isLabExist(scheduleRequest.getLabId())) {
            throw new IllegalArgumentException("实验室不存在");
        }

        // 4. 检查时间段是否存在
        if (!classTimeSlotService.isSlotExist(scheduleRequest.getSlotId())) {
            throw new IllegalArgumentException("时间段不存在");
        }

        // 5. 检查排课冲突 - 使用LabCourseService的冲突检测方法
        // 构造LabCourseRequest对象用于冲突检测
        LabCourseRequest labCourseRequest = new LabCourseRequest();
        labCourseRequest.setCourseId(courseId);
        labCourseRequest.setLabId(scheduleRequest.getLabId());
        labCourseRequest.setSlotId(scheduleRequest.getSlotId());
        labCourseRequest.setStartWeek(scheduleRequest.getStartWeek());
        labCourseRequest.setEndWeek(scheduleRequest.getEndWeek());
        labCourseRequest.setDayOfWeek(scheduleRequest.getDayOfWeek());

        // 检查冲突，传入null作为excludeScheduleId（因为这是新建排课）
        if (labCourseService.checkScheduleConflict(labCourseRequest, null)) {
            throw new RuntimeException("排课冲突：该实验室在该时间段已有课程安排");
        }

        // 6. 创建实验室课程关联
        LabCourse labCourse = new LabCourse();
        labCourse.setCourseId(courseId);
        labCourse.setLabId(scheduleRequest.getLabId());
        labCourse.setTeacherId(scheduleRequest.getTeacherId());
        labCourse.setSlotId(scheduleRequest.getSlotId());
        labCourse.setStartWeek(scheduleRequest.getStartWeek());
        labCourse.setEndWeek(scheduleRequest.getEndWeek());
        labCourse.setDayOfWeek(scheduleRequest.getDayOfWeek());
        labCourse.setIsDelete(0);
        boolean saveResult = labCourseService.save(labCourse);
        if (!saveResult) {
            throw new RuntimeException("排课失败，数据库错误");
        }

        // 7. 更新课程状态为已发布
        course.setStatus(1); // 已发布
        boolean updateResult = this.updateById(course);
        if (!updateResult) {
            throw new RuntimeException("更新课程状态失败");
        }

        return true;
    }

    @Override
    public CourseDetailVO getCourseDetail(Long courseId) {
        if (courseId == null) {
            return null;
        }

        // 1. 查询课程信息
        Course course = this.getById(courseId);
        if (course == null) {
            return null;
        }

        // 2. 创建CourseDetailVO
        CourseDetailVO courseDetailVO = new CourseDetailVO();
        BeanUtils.copyProperties(course, courseDetailVO);

        // 3. 获取教师姓名
        if (course.getTeacherId() != null) {
            User teacher = userService.getUserById(course.getTeacherId());
            if (teacher != null) {
                courseDetailVO.setTeacherName(teacher.getRealName());
            }
        }

        // 4. 获取已选学生数
        long enrolledCount = studentCourseService.getCourseStudentCount(courseId);
        courseDetailVO.setEnrolledStudents((int) enrolledCount);

        // 5. 计算剩余名额
        if (course.getMaxStudents() != null) {
            courseDetailVO.setRemainingSlots(course.getMaxStudents() - (int) enrolledCount);
        }

        // 6. 设置状态描述
        String statusDesc = course.getStatus() == 0 ? "待审批" : "已发布";
        courseDetailVO.setStatusDescription(statusDesc);

        // 7. 获取实验室排课信息
        QueryWrapper<LabCourse> labCourseQuery = new QueryWrapper<>();
        labCourseQuery.eq("course_id", courseId);
        List<LabCourse> labCourses = labCourseService.list(labCourseQuery);

        List<CourseDetailVO.LabScheduleVO> labScheduleVOs = labCourses.stream()
                .map(labCourse -> {
                    CourseDetailVO.LabScheduleVO scheduleVO = new CourseDetailVO.LabScheduleVO();
                    scheduleVO.setLabId(labCourse.getLabId());
                    scheduleVO.setDayOfWeek(labCourse.getDayOfWeek());
                    scheduleVO.setTeacherId(labCourse.getTeacherId());
                    scheduleVO.setSlotId(labCourse.getSlotId());
                    scheduleVO.setStartWeek(labCourse.getStartWeek());
                    scheduleVO.setEndWeek(labCourse.getEndWeek());

                    // 获取实验室详细信息
                    Laboratory lab = laboratoryService.getLabById(labCourse.getLabId());
                    if (lab != null) {
                        scheduleVO.setLabName(lab.getLabName());
                        scheduleVO.setLabLocation(lab.getLocation());
                    }

                    // 设置描述信息
                    scheduleVO.setDayOfWeekDescription(getDayOfWeekDescription(labCourse.getDayOfWeek()));
                    scheduleVO.setTimeSlotDescription(getTimeSlotDescription(labCourse.getSlotId()));

                    return scheduleVO;
                })
                .collect(Collectors.toList());

        courseDetailVO.setLabSchedules(labScheduleVOs);

        // 8. 检查当前学生是否已选课（仅学生视角）
        try {
            if (StpUtil.isLogin()) {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                boolean isEnrolled = studentCourseService.isStudentSelectedCourse(courseId, currentUserId);
                courseDetailVO.setIsEnrolled(isEnrolled);
            }
        } catch (Exception e) {
            // 忽略权限检查异常
        }

        return courseDetailVO;
    }

    @Override
    public PageResult<CourseVO> listCoursesByPage(BasePageQuery basePageQuery) {
        // 1. 分页查询课程
        Page<Course> page = new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize());
        Page<Course> coursePage = this.page(page);

        // 2. 转换为CourseVO
        List<CourseVO> courseVOList = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO courseVO = new CourseVO();
                    BeanUtils.copyProperties(course, courseVO);

                    // 获取教师姓名
                    if (course.getTeacherId() != null) {
                        User teacher = userService.getUserById(course.getTeacherId());
                        if (teacher != null) {
                            courseVO.setTeacherName(teacher.getRealName());
                        }
                    }

                    // 获取已选学生数
                    long enrolledCount = studentCourseService.getCourseStudentCount(course.getCourseId());
                    courseVO.setEnrolledStudents((int) enrolledCount);

                    return courseVO;
                })
                .collect(Collectors.toList());

        // 3. 构建分页结果
        return PageResult.of(
                courseVOList,
                coursePage.getTotal(),
                coursePage.getCurrent(),
                coursePage.getSize()
        );
    }

    @Override
    @Transactional
    public Boolean deleteCourse(Long courseId, Long currentUserId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }

        // 1. 查询课程
        Course course = this.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        // 2. 检查权限（管理员可以删除所有课程，教师只能删除自己创建的课程）
        if (currentUserId != null && !course.getTeacherId().equals(currentUserId)) {
            // 检查是否为管理员
            try {
                List<String> roles = StpUtil.getRoleList();
                if (!roles.contains("admin")) {
                    throw new IllegalArgumentException("无权限删除此课程");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("无权限删除此课程");
            }
        }

        // 3. 检查是否有学生已选课
        long enrolledCount = studentCourseService.getCourseStudentCount(courseId);
        if (enrolledCount > 0) {
            throw new IllegalArgumentException("已有学生选课，无法删除课程");
        }

        // 4. 删除实验室排课关联
        QueryWrapper<LabCourse> labCourseQuery = new QueryWrapper<>();
        labCourseQuery.eq("course_id", courseId);
        labCourseService.remove(labCourseQuery);

        // 5. 逻辑删除课程
        boolean deleteResult = this.removeById(courseId);
        return deleteResult;
    }

    @Override
    @Transactional
    public Boolean updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest, Long currentUserId) {
        if (courseId == null || courseUpdateRequest == null) {
            throw new IllegalArgumentException("课程ID和更新数据不能为空");
        }

        // 1. 查询课程
        Course course = this.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        // 2. 检查权限（管理员可以更新所有课程，教师只能更新自己创建的课程）
        if (currentUserId != null && !course.getTeacherId().equals(currentUserId)) {
            try {
                List<String> roles = StpUtil.getRoleList();
                if (!roles.contains("admin")) {
                    throw new IllegalArgumentException("无权限修改此课程");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("无权限修改此课程");
            }
        }

        // 3. 更新课程信息
        if (hasText(courseUpdateRequest.getCourseName())) {
            course.setCourseName(courseUpdateRequest.getCourseName());
        }
        if (courseUpdateRequest.getTeacherId() != null) {
            course.setTeacherId(courseUpdateRequest.getTeacherId());
        }
        if (hasText(courseUpdateRequest.getSemester())) {
            course.setSemester(courseUpdateRequest.getSemester());
        }
        if (hasText(courseUpdateRequest.getDescription())) {
            course.setDescription(courseUpdateRequest.getDescription());
        }
        if (courseUpdateRequest.getCredit() != null) {
            course.setCredit(courseUpdateRequest.getCredit());
        }
        if (courseUpdateRequest.getMaxStudents() != null) {
            course.setMaxStudents(courseUpdateRequest.getMaxStudents());
        }
        if (courseUpdateRequest.getStatus() != null) {
            course.setStatus(courseUpdateRequest.getStatus());
        }

        return this.updateById(course);
    }

    /**
     * 获取星期几描述
     */
    private String getDayOfWeekDescription(Integer dayOfWeek) {
        String[] days = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 7 ? days[dayOfWeek] : "";
    }

    /**
     * 获取时间段描述
     */
    private String getTimeSlotDescription(Long slotId) {
        if (slotId == null) {
            return "";
        }
        ClassTimeSlot timeSlot = classTimeSlotService.getSlotById(slotId);
        if (timeSlot != null) {
            return timeSlot.getSlotName();
        }
        return "";
    }
}