package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.entity.Role;
import com.example.lab_course_management.mapper.RoleMapper;
import com.example.lab_course_management.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现
 *
 * @author dddwmx
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}