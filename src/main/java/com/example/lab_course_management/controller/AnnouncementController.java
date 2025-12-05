package com.example.lab_course_management.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.lab_course_management.common.Result;
import com.example.lab_course_management.entity.Announcement;
import com.example.lab_course_management.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告控制器
 *
 * @author dddwmx
 */
@RestController
@RequestMapping("/announcement")
@Tag(name = "公告管理", description = "公告相关接口")
@Slf4j
@RequiredArgsConstructor
public class AnnouncementController {


    private final AnnouncementService announcementService;

    /**
     * 创建公告
     *
     * @param title 标题
     * @param content 内容
     * @param scheduleTime 定时发布时间（可选）
     * @return 公告ID
     */
    @PostMapping
    @Operation(summary = "创建公告", description = "创建新公告")
    @SaCheckRole("admin")
    public Result<Long> createAnnouncement(
            @Parameter(description = "公告标题") @RequestParam String title,
            @Parameter(description = "公告内容") @RequestParam String content,
            @Parameter(description = "定时发布时间") @RequestParam(required = false) LocalDateTime scheduleTime) {
        log.info("创建公告请求: title={}, scheduleTime={}", title, scheduleTime);

        // 获取当前登录用户ID作为发布者ID
        cn.dev33.satoken.stp.StpUtil.checkLogin();
        Long publisherId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();

        Long announcementId = announcementService.createAnnouncement(title, content, publisherId, scheduleTime);
        return Result.success(announcementId, "公告创建成功");
    }

    /**
     * 发布公告
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    @PutMapping("/publish/{announcementId}")
    @Operation(summary = "发布公告", description = "将公告状态改为已发布")
    @SaCheckRole("admin")
    public Result<Boolean> publishAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {
        log.info("发布公告请求: announcementId={}", announcementId);
        boolean result = announcementService.publishAnnouncement(announcementId);
        return Result.success(result, "公告发布成功");
    }

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/{announcementId}")
    @Operation(summary = "删除公告", description = "删除指定公告")
    @SaCheckRole("admin")
    public Result<Boolean> deleteAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {
        log.info("删除公告请求: announcementId={}", announcementId);
        boolean result = announcementService.deleteAnnouncement(announcementId);
        return Result.success(result, "公告删除成功");
    }

    /**
     * 分页查询公告
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param status 状态筛选（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询公告", description = "分页查询公告列表")
    @SaCheckRole("admin")
    public Result<IPage<Announcement>> getAnnouncementPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "公告状态") @RequestParam(required = false) Integer status) {

        Page<Announcement> page = new Page<>(current, size);
        IPage<Announcement> result = announcementService.getAnnouncementPage(page, status);
        return Result.success(result);
    }

    /**
     * 获取已发布的公告列表
     *
     * @return 已发布公告列表
     */
    @GetMapping("/published")
    @Operation(summary = "获取已发布公告", description = "获取所有已发布的公告列表")
    @SaCheckRole("admin")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 手动执行定时发布公告任务
     *
     * @return 发布的公告数量
     */
    @PostMapping("/schedule-publish")
    @Operation(summary = "手动执行定时发布", description = "手动执行定时发布公告任务")
    @SaCheckRole("admin")
    public Result<Integer> schedulePublishAnnouncements() {
        log.info("手动执行定时发布公告任务");
        int count = announcementService.schedulePublishAnnouncements();
        return Result.success(count, "共发布了 " + count + " 条公告");
    }
}