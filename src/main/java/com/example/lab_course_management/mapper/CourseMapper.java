package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 课程Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 获取热门课程TOP10
     */
    @Select("SELECT " +
            "  c.course_id as courseId, " +
            "  c.course_name as courseName, " +
            "  COUNT(sc.student_id) as enrollmentCount " +
            "FROM course c " +
            "LEFT JOIN student_course sc ON c.course_id = sc.course_id " +
            "WHERE c.is_delete = 0 " +
            "GROUP BY c.course_id, c.course_name " +
            "ORDER BY enrollmentCount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getTop10HotCourses();
}