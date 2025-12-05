package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.entity.ClassTimeSlot;

import java.util.List;

/**
 * 时间段服务接口
 *
 * @author dddwmx
 */
public interface ClassTimeSlotService extends IService<ClassTimeSlot> {

    /**
     * 根据ID获取时间段信息
     *
     * @param slotId 时间段ID
     * @return 时间段信息
     */
    ClassTimeSlot getSlotById(Long slotId);

    /**
     * 检查时间段是否存在
     *
     * @param slotId 时间段ID
     * @return true-存在，false-不存在
     */
    boolean isSlotExist(Long slotId);

    /**
     * 获取所有可用的时间段
     *
     * @return 时间段列表
     */
    List<ClassTimeSlot> getAllAvailableSlots();
}