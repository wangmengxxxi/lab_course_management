package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.StudentCourse;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.query.CourseGradeQuery;
import com.example.lab_course_management.model.dto.request.EnrollRequest;
import com.example.lab_course_management.model.dto.request.EnrollmentQueryRequest;
import com.example.lab_course_management.model.dto.request.StudentCourseRequest;
import com.example.lab_course_management.model.vo.CourseVO;
import com.example.lab_course_management.model.vo.StudentCourseGradeVO;
import com.example.lab_course_management.model.vo.StudentCourseVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学生选课服务接口
 *
 * @author dddwmx
 */
public interface StudentCourseService extends IService<StudentCourse> {

    /**
     * 学生选课
     *
     * @param studentCourseRequest 选课请求
     * @return 操作结果
     */
    boolean selectCourse(StudentCourseRequest studentCourseRequest);

    /**
     * 退课
     *
     * @param enrollmentId 选课记录ID（student_course表的主键id）
     * @param operatorId 操作人ID（用于记录谁执行了退课操作）
     * @return 操作结果
     */
    boolean dropCourse(Long enrollmentId, Long operatorId);

    /**
     * 获取学生选课列表
     *
     * @param studentId 学生ID（可选，如果不传则使用当前登录用户ID）
     * @return 选课列表
     */
    List<StudentCourseVO> getStudentCourseList(Long studentId);

    /**
     * 获取课程选课学生列表
     *
     * @param courseId 课程ID
     * @return 选课学生列表
     */
    List<StudentCourseVO> getCourseStudentList(Long courseId);

    /**
     * 分页查询选课记录
     *
     * @param page 分页参数
     * @param courseId 课程ID（可选）
     * @param studentId 学生ID（可选）
     * @return 分页结果
     */
    IPage<StudentCourseVO> getStudentCoursePage(Page<StudentCourse> page, Long courseId, Long studentId);

    /**
     * 录入学生成绩
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param usualGrade 平时成绩
     * @param finalGrade 期末成绩
     * @return 操作结果
     */
    boolean inputGrade(Long courseId, Long studentId, BigDecimal usualGrade, BigDecimal finalGrade);

    /**
     * 计算学生总成绩
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 总成绩
     */
    BigDecimal calculateTotalGrade(Long courseId, Long studentId);

    /**
     * 获取课程选课人数
     *
     * @param courseId 课程ID
     * @return 选课人数
     */
    long getCourseStudentCount(Long courseId);

    /**
     * 检查学生是否已选择课程
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return true-已选择，false-未选择
     */
    boolean isStudentSelectedCourse(Long courseId, Long studentId);

    /**
     * 检查学生选课是否存在时间冲突
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return true-有冲突，false-无冲突
     */
    boolean checkStudentCourseConflict(Long courseId, Long studentId);

    /**
     * 学生选课
     *
     * @param enrollRequest 选课请求
     * @return 是否成功
     */
    Boolean enrollCourse(EnrollRequest enrollRequest);

    /**
     * 分页查询我的课表
     *
     * @param basePageQuery 分页查询参数
     * @return 课程分页结果
     */
    PageResult<CourseVO> getMyScheduleByPage(BasePageQuery basePageQuery);

    /**
     * 分页查询选课记录（管理员用）
     *
     * @param queryRequest 查询请求参数
     * @return 选课记录分页结果
     */
    PageResult<StudentCourseVO> listEnrollmentsByPage(EnrollmentQueryRequest queryRequest);

    /**
     * 分页查询课程学生成绩列表
     *
     * @param courseGradeQuery 查询参数
     * @return 学生成绩分页结果
     */
    PageResult<StudentCourseGradeVO> getCourseGradeListByPage(CourseGradeQuery courseGradeQuery);
}