package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}