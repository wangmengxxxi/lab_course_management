package com.example.lab_course_management.utils;


import com.example.lab_course_management.entity.ClassTimeSlot;
import com.example.lab_course_management.entity.LabCourse;

/**
 * 学生选课冲突检测工具类
 *
 * @author dddwmx
 */
public class StudentScheduleUtils {

    /**
     * 检测两门课程是否存在时间冲突
     *
     * @param newCourse 新课程排课信息
     * @param existingCourse 已选课程排课信息
     * @return true-有冲突，false-无冲突
     */
    public static boolean hasCourseConflict(LabCourse newCourse, LabCourse existingCourse) {
        // 检查星期几是否相同
        if (!newCourse.getDayOfWeek().equals(existingCourse.getDayOfWeek())) {
            return false;
        }

        // 检查时间段是否相同
        if (!newCourse.getSlotId().equals(existingCourse.getSlotId())) {
            return false;
        }

        // 检查周次区间是否重叠
        return CourseScheduleUtils.hasTimeOverlap(
            newCourse.getStartWeek(), newCourse.getEndWeek(),
            existingCourse.getStartWeek(), existingCourse.getEndWeek()
        );
    }

    /**
     * 检测学生选课时间冲突
     *
     * @param studentCourses 学生已选课程列表
     * @param newSchedule 新课程排课信息
     * @return true-有冲突，false-无冲突
     */
    public static boolean hasStudentScheduleConflict(java.util.List<LabCourse> studentCourses, LabCourse newSchedule) {
        for (LabCourse existingSchedule : studentCourses) {
            if (hasCourseConflict(newSchedule, existingSchedule)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化时间段信息
     *
     * @param timeSlot 时间段
     * @return 格式化后的时间段文本
     */
    public static String formatTimeSlot(ClassTimeSlot timeSlot) {
        if (timeSlot == null) {
            return "";
        }
        return String.format("%s (%s-%s)",
            timeSlot.getSlotName(),
            timeSlot.getStartTime().toString(),
            timeSlot.getEndTime().toString()
        );
    }

    /**
     * 生成课程排课描述
     *
     * @param labCourse 排课信息
     * @param dayOfWeekText 星期几文本
     * @param timeSlot 时间段信息
     * @return 排课描述
     */
    public static String generateScheduleDescription(LabCourse labCourse, String dayOfWeekText, ClassTimeSlot timeSlot) {
        return String.format("第%d-%d周 %s %s",
            labCourse.getStartWeek(),
            labCourse.getEndWeek(),
            dayOfWeekText,
            formatTimeSlot(timeSlot)
        );
    }
}