package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.entity.Announcement;
import com.example.lab_course_management.mapper.AnnouncementMapper;
import com.example.lab_course_management.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现类
 *
 * @author dddwmx
 */
@Service
@Slf4j
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAnnouncement(String title, String content, Long publisherId, LocalDateTime scheduleTime) {
        if (title == null || title.trim().isEmpty()) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.PARAMS_ERROR, "公告标题不能为空");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.PARAMS_ERROR, "公告内容不能为空");
        }

        if (publisherId == null) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.PARAMS_ERROR, "发布者ID不能为空");
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setPublisherId(publisherId);
        announcement.setScheduleTime(scheduleTime);
        announcement.setStatus(0); // 草稿状态
        announcement.setIsDelete(0); // 显式设置未删除状态，避免null覆盖数据库默认值

        boolean saveResult = this.save(announcement);
        if (!saveResult) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.SYSTEM_ERROR, "创建公告失败");
        }

        return announcement.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishAnnouncement(Long announcementId) {
        if (announcementId == null) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.PARAMS_ERROR, "公告ID不能为空");
        }

        UpdateWrapper<Announcement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", announcementId);
        updateWrapper.set("status", 1); // 已发布状态

        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int schedulePublishAnnouncements() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        queryWrapper.le("schedule_time", LocalDateTime.now()); // 发布时间小于等于当前时间
        List<Announcement> announcements = this.list(queryWrapper);
        if (announcements.isEmpty()) {
            return 0;
        }
        int publishedCount = 0;
        for (Announcement announcement : announcements) {
            try {
                boolean result = publishAnnouncement(announcement.getId());
                if (result) {
                    publishedCount++;
                    log.info("发布公告成功: ID={}, 标题={}", announcement.getId(), announcement.getTitle());
                }
            } catch (Exception e) {
                log.error("发布公告失败: ID={}, 标题={}, 错误信息={}",
                    announcement.getId(), announcement.getTitle(), e.getMessage(), e);
            }
        }

        log.info("定时发布公告完成，共发布 {} 条公告", publishedCount);
        return publishedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Long announcementId) {
        if (announcementId == null) {
            throw new com.example.lab_course_management.exception.BussniesException(
                com.example.lab_course_management.common.ErrorCode.PARAMS_ERROR, "公告ID不能为空");
        }

        return this.removeById(announcementId);
    }

    @Override
    public IPage<Announcement> getAnnouncementPage(Page<Announcement> page, Integer status) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");

        return this.page(page, queryWrapper);
    }

    @Override
    public List<Announcement> getPublishedAnnouncements() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 已发布状态
        queryWrapper.orderByDesc("create_time");

        return this.list(queryWrapper);
    }
}