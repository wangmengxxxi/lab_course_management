package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.entity.Announcement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务接口
 *
 * @author dddwmx
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 创建公告
     *
     * @param title 标题
     * @param content 内容
     * @param publisherId 发布者ID
     * @param scheduleTime 定时发布时间（可选）
     * @return 公告ID
     */
    Long createAnnouncement(String title, String content, Long publisherId, LocalDateTime scheduleTime);

    /**
     * 发布公告（状态改为已发布）
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    boolean publishAnnouncement(Long announcementId);

    /**
     * 定时发布公告
     * 扫描所有到达发布时间的草稿公告并发布
     *
     * @return 发布的公告数量
     */
    int schedulePublishAnnouncements();

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    boolean deleteAnnouncement(Long announcementId);

    /**
     * 分页查询公告
     *
     * @param page 分页参数
     * @param status 状态筛选（可选）
     * @return 分页结果
     */
    IPage<Announcement> getAnnouncementPage(Page<Announcement> page, Integer status);

    /**
     * 获取已发布的公告列表
     *
     * @return 已发布公告列表
     */
    List<Announcement> getPublishedAnnouncements();
}