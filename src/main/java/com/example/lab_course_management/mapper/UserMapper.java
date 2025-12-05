package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}