package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PointsRecordDTO;
import com.thfh.dto.PointsAdjustDTO;
import com.thfh.dto.PointsQueryDTO;
import com.thfh.model.User;
import com.thfh.service.PointsService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
public class PointsController {
    @Autowired
    private PointsService pointsService;

    @Autowired
    private UserService userService;

    /**
     * 获取积分记录列表
     * @param queryDTO 查询条件，包含以下字段：
     *                - pageNum：当前页码（从1开始）
     *                - pageSize：每页显示记录数
     *                - studentId：学员ID，可选，用于筛选特定学员的积分记录
     *                - type：积分类型，可选，用于筛选特定类型的积分记录
     * @return 积分记录分页列表，包含积分记录的详细信息
     */
    @GetMapping("/records")
    public Result<Page<PointsRecordDTO>> getPointsRecords(PointsQueryDTO queryDTO) {
        return Result.success(pointsService.getPointsRecords(queryDTO));
    }

    /**
     * 调整用户积分
     * @param adjustDTO 积分调整信息，包含以下字段：
     *                - studentId：学员ID，必填，用于指定要调整积分的学员
     *                - points：调整的积分数量，必填，正数表示增加积分，负数表示扣减积分
     *                - description：调整说明，必填，用于记录积分调整的原因和备注
     * @return 积分调整记录，包含调整后的积分信息
     */
    @PostMapping("/adjust")
    public Result<PointsRecordDTO> adjustPoints(@RequestBody PointsAdjustDTO adjustDTO) {
        return Result.success(pointsService.adjustPoints(adjustDTO));
    }

    /**
     * 获取当前登录用户的积分
     * @return 当前用户的积分数量，如果用户从未获得过积分则返回0
     * @throws RuntimeException 用户未登录时抛出异常
     */
    @GetMapping("/current")
    public Result<Integer> getCurrentUserPoints() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return Result.success(currentUser.getPoints());
    }
}