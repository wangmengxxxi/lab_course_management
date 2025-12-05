package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.LabAddRequest;
import com.example.lab_course_management.model.dto.request.LabUpdateRequest;
import com.example.lab_course_management.model.vo.LabVO;
import com.example.lab_course_management.service.LaboratoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 实验室控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/labs")
@Tag(name = "实验室管理", description = "实验室管理接口")
@Slf4j
@RequiredArgsConstructor
public class LabController {

    private final LaboratoryService laboratoryService;

    @PostMapping
    @SaCheckRole("admin")
    @Operation(summary = "新增实验室", description = "新增实验室接口")
    public Result<Long> addLab(@Valid @RequestBody LabAddRequest labAddRequest) {
        log.info("新增实验室: {}", labAddRequest.getLabName());

        try {
            Long labId = laboratoryService.addLab(labAddRequest);
            log.info("新增实验室成功, labId: {}", labId);
            return Result.success(labId, "新增实验室成功");
        } catch (Exception e) {
            log.error("新增实验室失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    @SaCheckRole("admin")
    @Operation(summary = "分页查询实验室", description = "分页查询实验室列表接口")
    public Result<PageResult<LabVO>> listLabsByPage(@Valid BasePageQuery basePageQuery) {
        log.info("分页查询实验室: page={}, size={}", basePageQuery.getPageNum(), basePageQuery.getPageSize());

        try {
            PageResult<LabVO> pageResult = laboratoryService.listLabsByPage(basePageQuery);
            log.info("分页查询实验室成功: total={}, current={}", pageResult.getTotal(), pageResult.getCurrent());
            return Result.success(pageResult, "查询成功");
        } catch (Exception e) {
            log.error("分页查询实验室失败: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    @Operation(summary = "删除实验室", description = "删除实验室接口")
    public Result<Void> deleteLab(@PathVariable Long id) {
        log.info("删除实验室: {}", id);

        try {
            Boolean result = laboratoryService.deleteLab(id);
            if (result) {
                log.info("删除实验室成功: {}", id);
                return Result.success("删除实验室成功");
            } else {
                log.warn("删除实验室失败: {}", id);
                return Result.error("删除实验室失败");
            }
        } catch (Exception e) {
            log.error("删除实验室失败: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @SaCheckRole("admin")
    @Operation(summary = "更新实验室", description = "更新实验室信息接口")
    public Result<Void> updateLab(@PathVariable Long id, @Valid @RequestBody LabUpdateRequest labUpdateRequest) {
        log.info("更新实验室: {}", id);

        try {
            Boolean result = laboratoryService.updateLab(id, labUpdateRequest);
            if (result) {
                log.info("更新实验室成功: {}", id);
                return Result.success("更新实验室成功");
            } else {
                log.warn("更新实验室失败: {}", id);
                return Result.error("更新实验室失败");
            }
        } catch (Exception e) {
            log.error("更新实验室失败: {}", e.getMessage());
            throw e;
        }
    }
}