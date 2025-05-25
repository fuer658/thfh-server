package com.thfh.controller;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import com.thfh.model.User;
import com.thfh.service.CheckInService;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 签到管理控制器
 * 提供用户签到、补签和签到记录查询等功能
 */
@Tag(name = "签到管理", description = "用户签到、补签和签到记录查询等功能")
@RestController
@RequestMapping("/api/check-in")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private UserService userService;

    /**
     * 当前登录用户进行签到
     * @return 签到记录，包含签到时间等信息
     */
    @Operation(summary = "用户签到", description = "当前登录用户进行每日签到")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "签到成功"),
        @ApiResponse(responseCode = "400", description = "今日已签到"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping
    public Result<CheckIn> checkIn() {
        return Result.success(checkInService.checkIn());
    }

    /**
     * 使用补签卡进行历史日期补签
     * @param checkInDate 需要补签的日期（格式：yyyy-MM-dd）
     * @return 补签记录，包含补签时间等信息
     */
    @Operation(summary = "用户补签", description = "使用补签卡为指定日期进行补签")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "补签成功"),
        @ApiResponse(responseCode = "400", description = "该日期已签到或补签卡不足"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping("/makeup")
    public Result<CheckIn> makeupCheckIn(
            @Parameter(description = "补签日期", required = true, example = "2023-01-01") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate) {
        return Result.success(checkInService.makeupCheckIn(checkInDate));
    }

    /**
     * 获取当前用户本月签到次数
     * @return 本月签到总次数
     */
    @Operation(summary = "获取本月签到次数", description = "获取当前登录用户在本月的签到总次数")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/monthly-count")
    public Result<Long> getMonthlyCheckInCount() {
        return Result.success(checkInService.getMonthlyCheckInCount());
    }

    /**
     * 检查当前用户今日是否已签到
     * @return 今日是否已签到，true表示已签到，false表示未签到
     */
    @Operation(summary = "检查今日是否已签到", description = "检查当前登录用户今日是否已完成签到")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "检查成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/today-status")
    public Result<Boolean> isCheckedInToday() {
        return Result.success(checkInService.isCheckedInToday());
    }

    /**
     * 获取当前用户连续签到次数
     * @return 连续签到天数和今日是否已签到的状态
     */
    @Operation(summary = "获取连续签到次数", description = "获取当前登录用户的连续签到天数和今日签到状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/consecutive-count")
    public Result<Object> getConsecutiveCheckInCount() {
        return checkInService.getConsecutiveCheckInCount();
    }

    /**
     * 获取当前用户的签到历史记录
     * @param pageable 分页参数
     * @return 分页的签到历史记录
     */
    @Operation(summary = "获取签到历史记录", description = "获取当前登录用户的签到历史记录，支持分页")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/history")
    public Result<CustomPage<CheckIn>> getUserCheckInHistory(
            @Parameter(description = "分页参数") Pageable pageable) {
        return Result.success(checkInService.getUserCheckInHistory(pageable));
    }

    /**
     * 获取当前用户的补签卡数量
     * @return 补签卡数量
     */
    @Operation(summary = "获取补签卡数量", description = "获取当前登录用户拥有的补签卡数量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/makeup-cards")
    public Result<Integer> getUserMakeupCards() {
        User currentUser = userService.getCurrentUser();
        return Result.success(currentUser.getMakeupCards());
    }

    /**
     * 为当前用户添加补签卡
     * @param count 需要添加的补签卡数量
     * @return 添加后的补签卡总数量
     */
    @Operation(summary = "添加补签卡", description = "为当前登录用户添加指定数量的补签卡")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping("/makeup-cards")
    public Result<Integer> addMakeupCards(
            @Parameter(description = "补签卡数量", required = true, example = "1") @RequestParam Integer count) {
        return Result.success(checkInService.addMakeupCards(count));
    }
}