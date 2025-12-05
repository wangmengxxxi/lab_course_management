package com.example.lab_course_management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lab_course_management.common.PageResult;
import com.example.lab_course_management.entity.Laboratory;
import com.example.lab_course_management.entity.User;
import com.example.lab_course_management.mapper.LaboratoryMapper;
import com.example.lab_course_management.model.dto.query.BasePageQuery;
import com.example.lab_course_management.model.dto.request.LabAddRequest;
import com.example.lab_course_management.model.dto.request.LabUpdateRequest;
import com.example.lab_course_management.model.vo.LabVO;
import com.example.lab_course_management.service.LaboratoryService;
import com.example.lab_course_management.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实验室服务实现类
 *
 * @author dddwmx
 */
@Service
@RequiredArgsConstructor
public class LaboratoryServiceImpl extends ServiceImpl<LaboratoryMapper, Laboratory> implements LaboratoryService {

    private final UserService userService;

    @Override
    public Laboratory getLabById(Long labId) {
        if (labId == null) {
            return null;
        }
        return this.getById(labId);
    }

    @Override
    public boolean isLabExist(Long labId) {
        if (labId == null) {
            return false;
        }
        Laboratory lab = this.getById(labId);
        return lab != null;
    }

    @Override
    @Transactional
    public Long addLab(LabAddRequest labAddRequest) {
        // 1. 校验参数
        if (labAddRequest == null || labAddRequest.getLabName() == null || labAddRequest.getLocation() == null) {
            throw new IllegalArgumentException("实验室名称和位置不能为空");
        }

        // 2. 检查实验室名称是否已存在
        QueryWrapper<Laboratory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lab_name", labAddRequest.getLabName());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("实验室名称已存在");
        }

        // 3. 创建实验室对象
        Laboratory laboratory = new Laboratory();
        laboratory.setLabName(labAddRequest.getLabName());
        laboratory.setLocation(labAddRequest.getLocation());
        laboratory.setCapacity(labAddRequest.getCapacity());
        laboratory.setManagerId(labAddRequest.getManagerId());
        laboratory.setDescription(labAddRequest.getDescription());
        laboratory.setStatus(0); // 默认正常状态
        laboratory.setIsDelete(0); // 显式设置未删除状态，避免null覆盖数据库默认值

        // 4. 保存实验室
        boolean saveResult = this.save(laboratory);
        if (!saveResult) {
            throw new RuntimeException("新增实验室失败，数据库错误");
        }

        return laboratory.getLabId();
    }

    @Override
    public PageResult<LabVO> listLabsByPage(BasePageQuery basePageQuery) {
        // 1. 分页查询实验室
        Page<Laboratory> page = new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize());
        Page<Laboratory> labPage = this.page(page);

        // 2. 转换为LabVO
        List<LabVO> labVOList = labPage.getRecords().stream()
                .map(lab -> {
                    LabVO labVO = new LabVO();
                    BeanUtils.copyProperties(lab, labVO);

                    // 查询管理员姓名
                    if (lab.getManagerId() != null) {
                        User manager = userService.getUserById(lab.getManagerId());
                        if (manager != null) {
                            labVO.setManagerName(manager.getRealName());
                        }
                    }

                    return labVO;
                })
                .collect(Collectors.toList());

        // 3. 构建分页结果
        return PageResult.of(
                labVOList,
                labPage.getTotal(),
                labPage.getCurrent(),
                labPage.getSize()
        );
    }

    @Override
    @Transactional
    public Boolean updateLab(Long labId, LabUpdateRequest labUpdateRequest) {
        if (labId == null || labUpdateRequest == null) {
            throw new IllegalArgumentException("实验室ID和更新数据不能为空");
        }

        // 1. 查询实验室
        Laboratory laboratory = this.getById(labId);
        if (laboratory == null) {
            throw new IllegalArgumentException("实验室不存在");
        }

        // 2. 更新实验室信息
        if (hasText(labUpdateRequest.getLabName())) {
            laboratory.setLabName(labUpdateRequest.getLabName());
        }
        if (hasText(labUpdateRequest.getLocation())) {
            laboratory.setLocation(labUpdateRequest.getLocation());
        }
        if (labUpdateRequest.getCapacity() != null) {
            laboratory.setCapacity(labUpdateRequest.getCapacity());
        }
        if (labUpdateRequest.getManagerId() != null) {
            laboratory.setManagerId(labUpdateRequest.getManagerId());
        }
        if (hasText(labUpdateRequest.getDescription())) {
            laboratory.setDescription(labUpdateRequest.getDescription());
        }
        if (labUpdateRequest.getStatus() != null) {
            laboratory.setStatus(labUpdateRequest.getStatus());
        }

        return this.updateById(laboratory);
    }

    @Override
    @Transactional
    public Boolean deleteLab(Long labId) {
        if (labId == null) {
            throw new IllegalArgumentException("实验室ID不能为空");
        }

        // 检查是否有课程在使用这个实验室
        // TODO: 检查 LabCourse 表中是否有关联记录

        // 逻辑删除实验室
        return this.removeById(labId);
    }
}