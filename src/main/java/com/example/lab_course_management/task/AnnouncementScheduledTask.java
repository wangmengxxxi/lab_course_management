package com.example.lab_course_management.task;

import com.example.lab_course_management.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公告定时任务
 *
 * @author dddwmx
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AnnouncementScheduledTask {

    private final AnnouncementService announcementService;

    /**
     * 每天凌晨0点执行定时发布公告任务
     * cron表达式：秒 分 时 日 月 星期
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void schedulePublishAnnouncements() {
        log.info("开始执行定时发布公告任务...");

        try {
            int publishedCount = announcementService.schedulePublishAnnouncements();
            log.info("定时发布公告任务执行完成，共发布了 {} 条公告", publishedCount);
        } catch (Exception e) {
            log.error("定时发布公告任务执行失败", e);
        }
    }
}