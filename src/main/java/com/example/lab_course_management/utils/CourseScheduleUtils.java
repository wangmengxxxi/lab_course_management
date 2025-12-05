package com.example.lab_course_management.utils;

import com.example.lab_course_management.entity.LabCourse;

/**
 * 排课冲突检测工具类
 *
 * @author dddwmx
 */
public class CourseScheduleUtils {

    /**
     * 检测时间区间是否有重叠
     *
     * @param startWeek1 新排课的开始周次
     * @param endWeek1 新排课的结束周次
     * @param startWeek2 已有排课的开始周次
     * @param endWeek2 已有排课的结束周次
     * @return true-有重叠，false-无重叠
     */
    public static boolean hasTimeOverlap(int startWeek1, int endWeek1, int startWeek2, int endWeek2) {
        return !(endWeek1 < startWeek2 || endWeek2 < startWeek1);
    }

    /**
     * 检测排课冲突（实验室+时间段+星期几+时间区间）
     *
     * @param newCourse 新排课信息
     * @param existingCourse 已有排课信息
     * @return true-有冲突，false-无冲突
     */
    public static boolean hasScheduleConflict(LabCourse newCourse, LabCourse existingCourse) {
        // 检查实验室是否相同
        if (!newCourse.getLabId().equals(existingCourse.getLabId())) {
            return false;
        }

        // 检查星期几是否相同
        if (!newCourse.getDayOfWeek().equals(existingCourse.getDayOfWeek())) {
            return false;
        }

        // 检查时间段是否相同
        if (!newCourse.getSlotId().equals(existingCourse.getSlotId())) {
            return false;
        }

        // 检查周次是否重叠
        return hasWeekOverlap(
                newCourse.getStartWeek(), newCourse.getEndWeek(),
                existingCourse.getStartWeek(), existingCourse.getEndWeek()
        );
    }

    /**
     * @deprecated 该方法已弃用，请使用 LabCourseService.checkScheduleConflict() 方法
     * 检测排课冲突（根据实验室ID、星期几、时间段、周次）
     *
     * @param labId 实验室ID
     * @param dayOfWeek 星期几
     * @param slotId 时间段ID
     * @param startWeek 开始周次
     * @param endWeek 结束周次
     * @return 冲突的课程列表
     */
    @Deprecated
    public static java.util.List<LabCourse> checkScheduleConflict(
            Long labId, Integer dayOfWeek, Long slotId, Integer startWeek, Integer endWeek) {
        // 该方法已弃用，请使用 LabCourseService.checkScheduleConflict() 方法
        // 这里应该查询数据库中是否有冲突的排课
        // 由于这是一个工具类，我们返回空列表，具体实现在Service中
        return new java.util.ArrayList<>();
    }

    /**
     * 检测时间段是否有重叠
     */
    private static boolean hasTimeSlotOverlap(int start1, int end1, int start2, int end2) {
        return !(end1 < start2 || end2 < start1);
    }

    /**
     * 检测周次范围是否有重叠
     */
    private static boolean hasWeekOverlap(Integer startWeek1, Integer endWeek1, Integer startWeek2, Integer endWeek2) {
        // 检查两个时间区间是否重叠
        if (startWeek1 == null || endWeek1 == null || startWeek2 == null || endWeek2 == null) {
            return false;
        }
        return !(endWeek1 < startWeek2 || endWeek2 < startWeek1);
    }

    /**
     * 获取星期几的文本描述
     *
     * @param dayOfWeek 星期几 1=周一 7=周日
     * @return 星期几文本
     */
    public static String getDayOfWeekText(Integer dayOfWeek) {
        if (dayOfWeek == null) {
            return "";
        }
        switch (dayOfWeek) {
            case 1: return "周一";
            case 2: return "周二";
            case 3: return "周三";
            case 4: return "周四";
            case 5: return "周五";
            case 6: return "周六";
            case 7: return "周日";
            default: return "";
        }
    }

    /**
     * 检查周次参数是否合法
     *
     * @param startWeek 开始周次
     * @param endWeek 结束周次
     * @return true-合法，false-不合法
     */
    public static boolean isValidWeekRange(Integer startWeek, Integer endWeek) {
        if (startWeek == null || endWeek == null) {
            return false;
        }
        return startWeek >= 1 && endWeek >= 1 && startWeek <= endWeek && endWeek <= 20;
    }
}