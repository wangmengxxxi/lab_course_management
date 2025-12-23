package com.example.lab_course_management.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.common.ErrorCode;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.*;
import com.example.lab_course_management.exception.BussniesException;
import com.example.lab_course_management.mapper.StudentCourseMapper;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.query.CourseGradeQuery;
import com.example.lab_course_management.model.dto.request.EnrollRequest;
import com.example.lab_course_management.model.dto.request.StudentCourseRequest;
import com.example.lab_course_management.model.dto.request.EnrollmentQueryRequest;
import com.example.lab_course_management.model.vo.CourseVO;
import com.example.lab_course_management.model.vo.StudentCourseGradeVO;
import com.example.lab_course_management.model.vo.StudentCourseVO;
import com.example.lab_course_management.service.*;
import com.example.lab_course_management.utils.StudentScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生选课服务实现类
 *
 * @author dddwmx
 */
@Service
@Slf4j
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {

    @Lazy
    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    @Resource
    private LabCourseService labCourseService;

    @Resource
    private ClassTimeSlotService classTimeSlotService;

    @Resource
    private LaboratoryService laboratoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectCourse(StudentCourseRequest studentCourseRequest) {
        // 1. 获取学生ID
        Long studentId = getStudentId(studentCourseRequest.getStudentId());

        // 2. 验证课程状态
        validateCourseStatus(studentCourseRequest.getCourseId());

        // 3. 检查是否重复选课
        if (isStudentSelectedCourse(studentCourseRequest.getCourseId(), studentId)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "不能重复选择同一门课程");
        }

        // 4. 检查课程容量
        validateCourseCapacity(studentCourseRequest.getCourseId());

        // 5. 检查时间冲突
        if (checkStudentCourseConflict(studentCourseRequest.getCourseId(), studentId)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "选课时间冲突：该课程时间与已选课程时间重叠");
        }

        // 6. 创建选课记录
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourseId(studentCourseRequest.getCourseId());
        studentCourse.setStudentId(studentId);

        return this.save(studentCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dropCourse(Long enrollmentId, Long operatorId) {
        // 1. 根据选课记录ID查询选课信息
        StudentCourse studentCourse = this.getById(enrollmentId);
        if (studentCourse == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "选课记录不存在");
        }

        // 2. 记录操作日志
        log.info("退课操作: enrollmentId={}, courseId={}, studentId={}, operatorId={}",
                enrollmentId, studentCourse.getCourseId(), studentCourse.getStudentId(), operatorId);

        // 3. 检查退课时间限制（例如：开课后不能退课）
        // TODO: 根据业务需求添加退课时间限制

        // 4. 物理删除选课记录
        boolean result = this.removeById(enrollmentId);

        if (result) {
            log.info("退课成功: enrollmentId={}, courseId={}, studentId={}",
                    enrollmentId, studentCourse.getCourseId(), studentCourse.getStudentId());
        } else {
            log.warn("退课失败: enrollmentId={}", enrollmentId);
        }

        return result;
    }

    @Override
    public List<StudentCourseVO> getStudentCourseList(Long studentId) {
        // 1. 获取学生ID
        studentId = getStudentId(studentId);

        // 2. 查询选课记录
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.orderByDesc("create_time");

        List<StudentCourse> studentCourses = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(studentCourses)) {
            return new ArrayList<>();
        }

        // 3. 转换为VO
        List<StudentCourseVO> voList = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            voList.add(convertToVO(studentCourse));
        }

        return voList;
    }

    @Override
    public List<StudentCourseVO> getCourseStudentList(Long courseId) {
        // 1. 查询选课记录
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.orderByDesc("create_time");

        List<StudentCourse> studentCourses = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(studentCourses)) {
            return new ArrayList<>();
        }

        // 2. 转换为VO
        List<StudentCourseVO> voList = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            voList.add(convertToVO(studentCourse));
        }

        return voList;
    }

    @Override
    public IPage<StudentCourseVO> getStudentCoursePage(Page<StudentCourse> page, Long courseId, Long studentId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();

        if (courseId != null) {
            queryWrapper.eq("course_id", courseId);
        }

        if (studentId != null) {
            queryWrapper.eq("student_id", studentId);
        }

        queryWrapper.orderByDesc("create_time");

        IPage<StudentCourse> studentCoursePage = this.page(page, queryWrapper);

        // 转换为VO
        IPage<StudentCourseVO> voPage = new Page<>();
        BeanUtils.copyProperties(studentCoursePage, voPage);

        List<StudentCourseVO> voList = new ArrayList<>();
        for (StudentCourse studentCourse : studentCoursePage.getRecords()) {
            voList.add(convertToVO(studentCourse));
        }
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inputGrade(Long courseId, Long studentId, BigDecimal usualGrade, BigDecimal finalGrade) {
        // 1. 检查选课记录是否存在
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", studentId);

        StudentCourse studentCourse = this.getOne(queryWrapper);
        if (studentCourse == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "选课记录不存在");
        }

        // 2. 成绩范围校验
        validateGradeRange(usualGrade, finalGrade);

        // 3. 更新成绩
        studentCourse.setUsualGrade(usualGrade);
        studentCourse.setFinalGrade(finalGrade);

        // 4. 计算总成绩（平时成绩30% + 期末成绩70%）
        BigDecimal totalGrade = calculateTotalGrade(usualGrade, finalGrade);
        studentCourse.setGrade(totalGrade);

        return this.updateById(studentCourse);
    }

    @Override
    public BigDecimal calculateTotalGrade(Long courseId, Long studentId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", studentId);

        StudentCourse studentCourse = this.getOne(queryWrapper);
        if (studentCourse == null || studentCourse.getUsualGrade() == null || studentCourse.getFinalGrade() == null) {
            return null;
        }

        return calculateTotalGrade(studentCourse.getUsualGrade(), studentCourse.getFinalGrade());
    }

    @Override
    public long getCourseStudentCount(Long courseId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);

        return this.count(queryWrapper);
    }

    @Override
    public boolean isStudentSelectedCourse(Long courseId, Long studentId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", studentId);

        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean checkStudentCourseConflict(Long courseId, Long studentId) {
        // 1. 获取新课程的排课信息
        QueryWrapper<LabCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        List<LabCourse> newCourseSchedules = labCourseService.list(queryWrapper);

        if (CollectionUtils.isEmpty(newCourseSchedules)) {
            return false; // 没有排课信息，无冲突
        }

        // 2. 获取学生已选课程的排课信息
        QueryWrapper<StudentCourse> studentCourseQueryWrapper = new QueryWrapper<>();
        studentCourseQueryWrapper.eq("student_id", studentId);
        List<StudentCourse> studentCourses = this.list(studentCourseQueryWrapper);

        if (CollectionUtils.isEmpty(studentCourses)) {
            return false; // 没有已选课程，无冲突
        }

        // 3. 检查时间冲突
        for (StudentCourse studentCourse : studentCourses) {
            QueryWrapper<LabCourse> existingCourseQueryWrapper = new QueryWrapper<>();
            existingCourseQueryWrapper.eq("course_id", studentCourse.getCourseId());
            List<LabCourse> existingCourseSchedules = labCourseService.list(existingCourseQueryWrapper);

            // 检查新课程与每个已选课程的时间冲突
            for (LabCourse newSchedule : newCourseSchedules) {
                for (LabCourse existingSchedule : existingCourseSchedules) {
                    if (StudentScheduleUtils.hasCourseConflict(newSchedule, existingSchedule)) {
                        return true; // 发现冲突
                    }
                }
            }
        }

        return false; // 无冲突
    }

    /**
     * 获取学生ID
     */
    private Long getStudentId(Long studentId) {
        if (studentId != null) {
            return studentId;
        }

        // 使用当前登录用户ID
        if (!StpUtil.isLogin()) {
            throw new BussniesException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 验证课程状态
     */
    private void validateCourseStatus(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            throw new BussniesException(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }

        if (course.getStatus() != 1) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "课程未发布，无法选课");
        }
    }

    /**
     * 验证课程容量
     */
    private void validateCourseCapacity(Long courseId) {
        long currentCount = getCourseStudentCount(courseId);
        Course course = courseService.getCourseById(courseId);

        if (course != null && course.getMaxStudents() != null && currentCount >= course.getMaxStudents()) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "课程人数已满，无法选课");
        }
    }

    /**
     * 验证成绩范围
     */
    private void validateGradeRange(BigDecimal usualGrade, BigDecimal finalGrade) {
        if (usualGrade != null && (usualGrade.compareTo(BigDecimal.ZERO) < 0 || usualGrade.compareTo(new BigDecimal("100")) > 0)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "平时成绩必须在0-100之间");
        }

        if (finalGrade != null && (finalGrade.compareTo(BigDecimal.ZERO) < 0 || finalGrade.compareTo(new BigDecimal("100")) > 0)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "期末成绩必须在0-100之间");
        }
    }

    /**
     * 计算总成绩
     */
    private BigDecimal calculateTotalGrade(BigDecimal usualGrade, BigDecimal finalGrade) {
        if (usualGrade == null || finalGrade == null) {
            return null;
        }

        // 平时成绩30% + 期末成绩70%
        BigDecimal usualWeight = new BigDecimal("0.3");
        BigDecimal finalWeight = new BigDecimal("0.7");

        BigDecimal totalGrade = usualGrade.multiply(usualWeight)
                .add(finalGrade.multiply(finalWeight))
                .setScale(2, RoundingMode.HALF_UP);

        return totalGrade;
    }

    /**
     * 转换为VO对象
     */
    private StudentCourseVO convertToVO(StudentCourse studentCourse) {
        StudentCourseVO vo = new StudentCourseVO();
        BeanUtils.copyProperties(studentCourse, vo);

        // 设置学生信息
        User student = userService.getUserById(studentCourse.getStudentId());
        if (student != null) {
            StudentCourseVO.StudentInfoVO studentInfoVO = new StudentCourseVO.StudentInfoVO();
            BeanUtils.copyProperties(student, studentInfoVO);
            vo.setStudent(studentInfoVO);
        }

        // 设置课程信息
        Course course = courseService.getCourseById(studentCourse.getCourseId());
        if (course != null) {
            StudentCourseVO.CourseInfoVO courseInfoVO = new StudentCourseVO.CourseInfoVO();
            BeanUtils.copyProperties(course, courseInfoVO);

            // 获取教师姓名
            User teacher = userService.getUserById(course.getTeacherId());
            if (teacher != null) {
                courseInfoVO.setTeacherName(teacher.getRealName());
            }

            vo.setCourse(courseInfoVO);
        }

        // 设置排课信息
        QueryWrapper<LabCourse> labCourseQuery = new QueryWrapper<>();
        labCourseQuery.eq("course_id", studentCourse.getCourseId());
        List<LabCourse> labCourses = labCourseService.list(labCourseQuery);
        
        if (!CollectionUtils.isEmpty(labCourses)) {
            LabCourse labCourse = labCourses.get(0); // 取第一个排课记录
            StudentCourseVO.ScheduleInfoVO scheduleInfoVO = new StudentCourseVO.ScheduleInfoVO();
            
            scheduleInfoVO.setLabId(labCourse.getLabId());
            scheduleInfoVO.setDayOfWeek(labCourse.getDayOfWeek());
            scheduleInfoVO.setSlotId(labCourse.getSlotId());
            scheduleInfoVO.setStartWeek(labCourse.getStartWeek());
            scheduleInfoVO.setEndWeek(labCourse.getEndWeek());
            
            // 获取星期几文本
            String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            if (labCourse.getDayOfWeek() >= 1 && labCourse.getDayOfWeek() <= 7) {
                scheduleInfoVO.setDayOfWeekText(weekDays[labCourse.getDayOfWeek()]);
            }
            
            // 获取实验室名称
            try {
                Laboratory lab = laboratoryService.getLabById(labCourse.getLabId());
                if (lab != null) {
                    scheduleInfoVO.setLabName(lab.getLabName());
                }
            } catch (Exception e) {
                log.warn("获取实验室信息失败: labId={}", labCourse.getLabId(), e);
            }
            
            // 获取时间段名称
            try {
                ClassTimeSlot timeSlot = classTimeSlotService.getSlotById(labCourse.getSlotId());
                if (timeSlot != null) {
                    scheduleInfoVO.setTimeSlotName(timeSlot.getSlotName());
                }
            } catch (Exception e) {
                log.warn("获取时间段信息失败: slotId={}", labCourse.getSlotId(), e);
            }
            
            vo.setScheduleInfo(scheduleInfoVO);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enrollCourse(EnrollRequest enrollRequest) {
        if (enrollRequest == null || enrollRequest.getCourseId() == null) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "课程ID不能为空");
        }

        Long studentId = getStudentId(null);
        Long courseId = enrollRequest.getCourseId();

        // 1. 检查是否已选择该课程
        if (isStudentSelectedCourse(courseId, studentId)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "已选择该课程");
        }

        // 2. 验证课程状态
        validateCourseStatus(courseId);

        // 3. 验证课程容量
        validateCourseCapacity(courseId);

        // 4. 检查时间冲突
        if (checkStudentCourseConflict(courseId, studentId)) {
            throw new BussniesException(ErrorCode.PARAMS_ERROR, "选课时间冲突");
        }

        // 5. 创建选课记录
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(courseId);

        boolean saveResult = this.save(studentCourse);
        if (!saveResult) {
            throw new BussniesException(ErrorCode.SYSTEM_ERROR, "选课失败");
        }

        // 返回选课结果
        return true;
    }

    @Override
    public PageResult<CourseVO> getMyScheduleByPage(BasePageQuery basePageQuery) {
        Long studentId = getStudentId(null);

        // 1. 查询学生已选课程
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .orderByDesc("create_time"); // 按选课时间倒序

        // 分页查询
        Page<StudentCourse> page = new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize());
        Page<StudentCourse> studentCoursePage = this.page(page, queryWrapper);

        // 2. 转换为StudentCourseVO (包含scheduleInfo)
        List<StudentCourseVO> studentCourseVOList = studentCoursePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 转换为CourseVO格式以保持接口兼容性,但添加scheduleInfo
        List<CourseVO> courseVOList = studentCourseVOList.stream()
                .map(studentCourseVO -> {
                    if (studentCourseVO.getCourse() == null) {
                        return null;
                    }
                    
                    CourseVO courseVO = new CourseVO();
                    // 复制课程基本信息
                    BeanUtils.copyProperties(studentCourseVO.getCourse(), courseVO);
                    
                    // 添加成绩信息
                    courseVO.setUsualGrade(studentCourseVO.getUsualGrade());
                    courseVO.setFinalGrade(studentCourseVO.getFinalGrade());
                    courseVO.setGrade(studentCourseVO.getGrade());
                    
                    // 添加排课信息 - 这是关键!
                    if (studentCourseVO.getScheduleInfo() != null) {
                        CourseVO.ScheduleInfoVO scheduleInfoVO = new CourseVO.ScheduleInfoVO();
                        BeanUtils.copyProperties(studentCourseVO.getScheduleInfo(), scheduleInfoVO);
                        courseVO.setScheduleInfo(scheduleInfoVO);
                    }
                    
                    return courseVO;
                })
                .filter(courseVO -> courseVO != null)
                .collect(Collectors.toList());

        // 4. 构建分页结果
        return PageResult.of(
                courseVOList,
                studentCoursePage.getTotal(),
                studentCoursePage.getCurrent(),
                studentCoursePage.getSize()
        );
    }

    @Override
    public PageResult<StudentCourseVO> listEnrollmentsByPage(EnrollmentQueryRequest queryRequest) {
        // 1. 构建查询条件
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();

        if (queryRequest.getCourseId() != null) {
            queryWrapper.eq("course_id", queryRequest.getCourseId());
        }
        if (queryRequest.getStudentId() != null) {
            queryWrapper.eq("student_id", queryRequest.getStudentId());
        }

        // 按选课时间倒序
        queryWrapper.orderByDesc("create_time");

        // 2. 分页查询
        Page<StudentCourse> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        Page<StudentCourse> studentCoursePage = this.page(page, queryWrapper);

        // 3. 转换为StudentCourseVO
        List<StudentCourseVO> studentCourseVOList = studentCoursePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 4. 构建分页结果
        return PageResult.of(
                studentCourseVOList,
                studentCoursePage.getTotal(),
                studentCoursePage.getCurrent(),
                studentCoursePage.getSize()
        );
    }

    @Override
    public PageResult<StudentCourseGradeVO> getCourseGradeListByPage(CourseGradeQuery courseGradeQuery) {
        log.info("查询课程学生成绩列表: courseId={}, pageNum={}, pageSize={}",
                courseGradeQuery.getCourseId(), courseGradeQuery.getPageNum(), courseGradeQuery.getPageSize());

        // 1. 构建查询条件
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseGradeQuery.getCourseId());

        // 2. 如果有学生姓名查询条件，需要关联用户表
        if (courseGradeQuery.getStudentName() != null && !courseGradeQuery.getStudentName().trim().isEmpty()) {
            // 获取该课程的所有选课记录，然后过滤学生姓名
            QueryWrapper<StudentCourse> nameQueryWrapper = new QueryWrapper<>();
            nameQueryWrapper.eq("course_id", courseGradeQuery.getCourseId());
            List<StudentCourse> allStudentCourses = this.list(nameQueryWrapper);

            // 获取学生ID列表
            List<Long> studentIds = allStudentCourses.stream()
                    .map(StudentCourse::getStudentId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(studentIds)) {
                return PageResult.of(new ArrayList<>(), 0L, (long) courseGradeQuery.getPageNum(), (long) courseGradeQuery.getPageSize());
            }

            // 查询用户表获取匹配的学生ID
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("user_id", studentIds);
            userQueryWrapper.like("real_name", courseGradeQuery.getStudentName().trim());
            List<User> matchedUsers = userService.list(userQueryWrapper);

            List<Long> matchedStudentIds = matchedUsers.stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(matchedStudentIds)) {
                return PageResult.of(new ArrayList<>(), 0L, (long) courseGradeQuery.getPageNum(), (long) courseGradeQuery.getPageSize());
            }

            queryWrapper.in("student_id", matchedStudentIds);
        }

        // 3. 添加排序条件
        if (courseGradeQuery.getSortBy() != null && !courseGradeQuery.getSortBy().trim().isEmpty()) {
            String sortOrder = courseGradeQuery.getSortOrder();
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(courseGradeQuery.getSortBy());
            } else {
                queryWrapper.orderByDesc(courseGradeQuery.getSortBy());
            }
        } else {
            // 默认按总成绩降序排序
            queryWrapper.orderByDesc("grade");
        }

        // 4. 分页查询
        Page<StudentCourse> page = new Page<>(courseGradeQuery.getPageNum(), courseGradeQuery.getPageSize());
        Page<StudentCourse> studentCoursePage = this.page(page, queryWrapper);

        // 5. 转换为StudentCourseGradeVO
        List<StudentCourseGradeVO> studentCourseGradeVOList = studentCoursePage.getRecords().stream()
                .map(this::convertToGradeVO)
                .collect(Collectors.toList());

        // 6. 构建分页结果
        return PageResult.of(
                studentCourseGradeVOList,
                studentCoursePage.getTotal(),
                studentCoursePage.getCurrent(),
                studentCoursePage.getSize()
        );
    }

    /**
     * 转换为学生成绩VO
     */
    private StudentCourseGradeVO convertToGradeVO(StudentCourse studentCourse) {
        StudentCourseGradeVO vo = new StudentCourseGradeVO();
        BeanUtils.copyProperties(studentCourse, vo);

        // 设置选课记录ID
        vo.setEnrollmentId(studentCourse.getEnrollmentId());

        // 查询课程名称
        Course course = courseService.getById(studentCourse.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
        }

        // 查询学生信息
        User student = userService.getById(studentCourse.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
            // User实体中没有userNumber字段，暂时设为账号
            vo.setStudentNumber(student.getAccount());
        }

        // 格式化选课时间
        if (studentCourse.getCreateTime() != null) {
            vo.setEnrollmentTime(studentCourse.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 计算成绩等级
        vo.setGradeLevel(calculateGradeLevel(studentCourse.getGrade()));

        return vo;
    }

    /**
     * 计算成绩等级
     */
    private String calculateGradeLevel(BigDecimal grade) {
        if (grade == null) {
            return "未录入";
        }

        double score = grade.doubleValue();
        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 70) {
            return "中等";
        } else if (score >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }
}