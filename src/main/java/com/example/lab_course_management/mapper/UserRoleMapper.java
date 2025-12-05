package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}