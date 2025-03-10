package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PointsRecordDTO;
import com.thfh.dto.PointsAdjustDTO;
import com.thfh.dto.PointsQueryDTO;
import com.thfh.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 积分管理控制器
 * 提供积分记录查询和积分调整等功能
 */
@RestController
@RequestMapping("/api/points")
public class PointsController {
    @Autowired
    private PointsService pointsService;

    /**
     * 获取积分记录列表
     * @param queryDTO 查询条件，包含用户ID、时间范围和分页信息等
     * @return 积分记录分页列表
     */
    @GetMapping("/records")
    public Result<Page<PointsRecordDTO>> getPointsRecords(PointsQueryDTO queryDTO) {
        return Result.success(pointsService.getPointsRecords(queryDTO));
    }

    /**
     * 调整用户积分
     * @param adjustDTO 积分调整信息，包含用户ID、调整数量和调整原因等
     * @return 积分调整记录
     */
    @PostMapping("/adjust")
    public Result<PointsRecordDTO> adjustPoints(@RequestBody PointsAdjustDTO adjustDTO) {
        return Result.success(pointsService.adjustPoints(adjustDTO));
    }
} 