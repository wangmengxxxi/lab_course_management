package com.example.lab_course_management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.Laboratory;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.LabAddRequest;
import com.example.lab_course_management.model.dto.request.LabUpdateRequest;
import com.example.lab_course_management.model.vo.LabVO;

/**
 * 实验室服务接口
 *
 * @author dddwmx
 */
public interface LaboratoryService extends IService<Laboratory> {

    /**
     * 根据ID获取实验室信息
     *
     * @param labId 实验室ID
     * @return 实验室信息
     */
    Laboratory getLabById(Long labId);

    /**
     * 检查实验室是否存在
     *
     * @param labId 实验室ID
     * @return true-存在，false-不存在
     */
    boolean isLabExist(Long labId);

    /**
     * 新增实验室
     *
     * @param labAddRequest 实验室添加请求
     * @return 实验室ID
     */
    Long addLab(LabAddRequest labAddRequest);

    /**
     * 分页查询实验室列表
     *
     * @param basePageQuery 分页查询参数
     * @return 实验室分页结果
     */
    PageResult<LabVO> listLabsByPage(BasePageQuery basePageQuery);

    /**
     * 更新实验室信息
     *
     * @param labId 实验室ID
     * @param labUpdateRequest 实验室更新请求
     * @return 是否成功
     */
    Boolean updateLab(Long labId, LabUpdateRequest labUpdateRequest);

    /**
     * 删除实验室
     *
     * @param labId 实验室ID
     * @return 是否成功
     */
    Boolean deleteLab(Long labId);
}