package com.thfh.controller;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import com.thfh.model.User;
import com.thfh.service.CheckInService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    @PostMapping
    public Result<CheckIn> checkIn() {
        return Result.success(checkInService.checkIn());
    }

    /**
     * 使用补签卡进行历史日期补签
     * @param checkInDate 需要补签的日期（格式：yyyy-MM-dd）
     * @return 补签记录，包含补签时间等信息
     */
    @PostMapping("/makeup")
    public Result<CheckIn> makeupCheckIn(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate) {
        return Result.success(checkInService.makeupCheckIn(checkInDate));
    }

    /**
     * 获取当前用户本月签到次数
     * @return 本月签到总次数
     */
    @GetMapping("/monthly-count")
    public Result<Long> getMonthlyCheckInCount() {
        return Result.success(checkInService.getMonthlyCheckInCount());
    }

    /**
     * 检查当前用户今日是否已签到
     * @return 今日是否已签到，true表示已签到，false表示未签到
     */
    @GetMapping("/today-status")
    public Result<Boolean> isCheckedInToday() {
        return Result.success(checkInService.isCheckedInToday());
    }

    /**
     * 获取当前用户连续签到次数
     * @return 连续签到天数和今日是否已签到的状态
     */
    @GetMapping("/consecutive-count")
    public Result<Object> getConsecutiveCheckInCount() {
        return checkInService.getConsecutiveCheckInCount();
    }

    /**
     * 获取当前用户的签到历史记录
     * @param pageable 分页参数
     * @return 分页的签到历史记录
     */
    @GetMapping("/history")
    public Result<CustomPage<CheckIn>> getUserCheckInHistory(
            Pageable pageable) {
        return Result.success(checkInService.getUserCheckInHistory(pageable));
    }

    /**
     * 获取当前用户的补签卡数量
     * @return 补签卡数量
     */
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
    @PostMapping("/makeup-cards")
    public Result<Integer> addMakeupCards(
            @RequestParam Integer count) {
        return Result.success(checkInService.addMakeupCards(count));
    }
}