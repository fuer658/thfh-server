package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PointsRecordDTO;
import com.thfh.dto.PointsAdjustDTO;
import com.thfh.dto.PointsQueryDTO;
import com.thfh.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
public class PointsController {
    @Autowired
    private PointsService pointsService;

    @GetMapping("/records")
    public Result<Page<PointsRecordDTO>> getPointsRecords(PointsQueryDTO queryDTO) {
        return Result.success(pointsService.getPointsRecords(queryDTO));
    }

    @PostMapping("/adjust")
    public Result<PointsRecordDTO> adjustPoints(@RequestBody PointsAdjustDTO adjustDTO) {
        return Result.success(pointsService.adjustPoints(adjustDTO));
    }
} 