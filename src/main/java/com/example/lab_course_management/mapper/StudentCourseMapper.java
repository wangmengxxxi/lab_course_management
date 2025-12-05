package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.StudentCourse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生选课Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {
}