package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.LabCourse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实验室排课Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface LabCourseMapper extends BaseMapper<LabCourse> {

}