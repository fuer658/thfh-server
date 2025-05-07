package com.thfh.controller;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import com.thfh.model.User;
import com.thfh.service.CheckInService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 签到管理控制器
 * 提供用户签到、补签和签到记录查询等功能
 */
@Api(tags = "签到管理", description = "用户签到、补签和签到记录查询等功能")
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
    @ApiOperation(value = "用户签到", notes = "当前登录用户进行每日签到")
    @ApiResponses({
        @ApiResponse(code = 200, message = "签到成功"),
        @ApiResponse(code = 400, message = "今日已签到"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
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
    @ApiOperation(value = "用户补签", notes = "使用补签卡为指定日期进行补签")
    @ApiResponses({
        @ApiResponse(code = 200, message = "补签成功"),
        @ApiResponse(code = 400, message = "该日期已签到或补签卡不足"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping("/makeup")
    public Result<CheckIn> makeupCheckIn(
            @ApiParam(value = "补签日期", required = true, example = "2023-01-01") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate) {
        return Result.success(checkInService.makeupCheckIn(checkInDate));
    }

    /**
     * 获取当前用户本月签到次数
     * @return 本月签到总次数
     */
    @ApiOperation(value = "获取本月签到次数", notes = "获取当前登录用户在本月的签到总次数")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/monthly-count")
    public Result<Long> getMonthlyCheckInCount() {
        return Result.success(checkInService.getMonthlyCheckInCount());
    }

    /**
     * 检查当前用户今日是否已签到
     * @return 今日是否已签到，true表示已签到，false表示未签到
     */
    @ApiOperation(value = "检查今日是否已签到", notes = "检查当前登录用户今日是否已完成签到")
    @ApiResponses({
        @ApiResponse(code = 200, message = "检查成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/today-status")
    public Result<Boolean> isCheckedInToday() {
        return Result.success(checkInService.isCheckedInToday());
    }

    /**
     * 获取当前用户连续签到次数
     * @return 连续签到天数和今日是否已签到的状态
     */
    @ApiOperation(value = "获取连续签到次数", notes = "获取当前登录用户的连续签到天数和今日签到状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
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
    @ApiOperation(value = "获取签到历史记录", notes = "获取当前登录用户的签到历史记录，支持分页")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/history")
    public Result<CustomPage<CheckIn>> getUserCheckInHistory(
            @ApiParam(value = "分页参数") Pageable pageable) {
        return Result.success(checkInService.getUserCheckInHistory(pageable));
    }

    /**
     * 获取当前用户的补签卡数量
     * @return 补签卡数量
     */
    @ApiOperation(value = "获取补签卡数量", notes = "获取当前登录用户拥有的补签卡数量")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
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
    @ApiOperation(value = "添加补签卡", notes = "为当前登录用户添加指定数量的补签卡")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping("/makeup-cards")
    public Result<Integer> addMakeupCards(
            @ApiParam(value = "补签卡数量", required = true, example = "1") @RequestParam Integer count) {
        return Result.success(checkInService.addMakeupCards(count));
    }
}