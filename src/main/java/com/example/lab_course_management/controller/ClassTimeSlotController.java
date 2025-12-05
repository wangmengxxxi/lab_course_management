package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.entity.ClassTimeSlot;
import com.example.lab_course_management.service.ClassTimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 时间段管理控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/api/class-time-slots")
@Tag(name = "时间段管理", description = "时间段相关接口")
@Slf4j
@RequiredArgsConstructor
public class ClassTimeSlotController {

    private final ClassTimeSlotService classTimeSlotService;

    /**
     * 获取所有可用的时间段
     *
     * @return 时间段列表
     */
    @GetMapping("/list")
    @SaCheckRole({"admin", "teacher", "student"})
    @Operation(summary = "获取时间段列表", description = "获取所有可用的时间段列表，按开始时间排序")
    public Result<List<ClassTimeSlot>> getAllAvailableSlots() {
        log.info("获取所有可用时间段列表");
        List<ClassTimeSlot> slots = classTimeSlotService.getAllAvailableSlots();
        return Result.success(slots);
    }

//    /**
//     * 根据ID获取时间段信息
//     *
//     * @param slotId 时间段ID
//     * @return 时间段信息
//     */
//    @GetMapping("/{slotId}")
//    @SaCheckRole({"admin", "teacher", "student"})
//    @Operation(summary = "获取时间段详情", description = "根据ID获取指定时间段的详细信息")
//    public Result<ClassTimeSlot> getSlotById(
//            @Parameter(description = "时间段ID") @PathVariable Long slotId) {
//        log.info("获取时间段详情: slotId={}", slotId);
//        ClassTimeSlot slot = classTimeSlotService.getSlotById(slotId);
//        if (slot == null) {
//            return Result.error("时间段不存在");
//        }
//        return Result.success(slot);
//    }
//
//    /**
//     * 检查时间段是否存在
//     *
//     * @param slotId 时间段ID
//     * @return 检查结果
//     */
//    @GetMapping("/check/{slotId}")
//    @SaCheckRole({"admin", "teacher"})
//    @Operation(summary = "检查时间段是否存在", description = "验证指定ID的时间段是否存在")
//    public Result<Boolean> isSlotExist(
//            @Parameter(description = "时间段ID") @PathVariable Long slotId) {
//        log.info("检查时间段是否存在: slotId={}", slotId);
//        boolean exists = classTimeSlotService.isSlotExist(slotId);
//        return Result.success(exists, exists ? "时间段存在" : "时间段不存在");
//    }
}