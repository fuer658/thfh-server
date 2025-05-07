package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PointsRecordDTO;
import com.thfh.dto.PointsAdjustDTO;
import com.thfh.dto.PointsQueryDTO;
import com.thfh.model.User;
import com.thfh.service.PointsService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 积分管理控制器
 * 提供用户积分的查询、调整和管理等功能
 */
@Api(tags = "积分管理", description = "提供用户积分的查询、调整和管理等功能")
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
    @ApiOperation(value = "获取积分记录列表", notes = "根据查询条件获取积分记录分页列表，支持按学员ID和积分类型筛选")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/records")
    public Result<Page<PointsRecordDTO>> getPointsRecords(
            @ApiParam(value = "查询条件，包含分页信息和筛选条件") PointsQueryDTO queryDTO) {
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
    @ApiOperation(value = "调整用户积分", notes = "管理员调整学员积分，支持增加或扣减积分")
    @ApiResponses({
        @ApiResponse(code = 200, message = "调整成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限调整积分"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @PostMapping("/adjust")
    public Result<PointsRecordDTO> adjustPoints(
            @ApiParam(value = "积分调整信息", required = true) @RequestBody PointsAdjustDTO adjustDTO) {
        return Result.success(pointsService.adjustPoints(adjustDTO));
    }

    /**
     * 获取当前登录用户的积分
     * @return 当前用户的积分数量，如果用户从未获得过积分则返回0
     * @throws RuntimeException 用户未登录时抛出异常
     */
    @ApiOperation(value = "获取当前登录用户的积分", notes = "获取当前登录用户的积分数量，如果用户从未获得过积分则返回0")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/current")
    public Result<Integer> getCurrentUserPoints() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return Result.success(currentUser.getPoints());
    }
}