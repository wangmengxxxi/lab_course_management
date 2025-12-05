package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.entity.ClassTimeSlot;
import com.example.lab_course_management.mapper.ClassTimeSlotMapper;
import com.example.lab_course_management.service.ClassTimeSlotService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 时间段服务实现类
 *
 * @author dddwmx
 */
@Service
public class ClassTimeSlotServiceImpl extends ServiceImpl<ClassTimeSlotMapper, ClassTimeSlot> implements ClassTimeSlotService {

    @Override
    public ClassTimeSlot getSlotById(Long slotId) {
        if (slotId == null) {
            return null;
        }
        return this.getById(slotId);
    }

    @Override
    public boolean isSlotExist(Long slotId) {
        if (slotId == null) {
            return false;
        }
        ClassTimeSlot slot = this.getById(slotId);
        return slot != null;
    }

    @Override
    public List<ClassTimeSlot> getAllAvailableSlots() {
        QueryWrapper<ClassTimeSlot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByAsc("start_time");
        return this.list(queryWrapper);
    }
}