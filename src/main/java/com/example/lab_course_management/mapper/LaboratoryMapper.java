package com.example.lab_course_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lab_course_management.entity.Laboratory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 实验室Mapper
 *
 * @author dddwmx
 */
@Mapper
public interface LaboratoryMapper extends BaseMapper<Laboratory> {

    /**
     * 获取实验室使用统计（基于当前时间的课程）
     */
    @Select("SELECT " +
            "  l.lab_id as labId, " +
            "  l.lab_name as labName, " +
            "  l.capacity, " +
            "  COUNT(lc.id) as actualUsage " +
            "FROM laboratory l " +
            "LEFT JOIN lab_course lc ON l.lab_id = lc.lab_id " +
            "WHERE l.is_delete = 0 " +
            "GROUP BY l.lab_id, l.lab_name, l.capacity " +
            "ORDER BY actualUsage DESC")
    List<Map<String, Object>> getLabUsageStats();
}