package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.entity.Permission;
import com.example.lab_course_management.mapper.PermissionMapper;
import com.example.lab_course_management.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现
 *
 * @author dddwmx
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}