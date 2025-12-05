package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.entity.UserRole;
import com.example.lab_course_management.mapper.UserRoleMapper;
import com.example.lab_course_management.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色服务实现
 *
 * @author dddwmx
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}