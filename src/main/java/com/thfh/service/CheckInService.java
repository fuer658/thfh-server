package com.thfh.service;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import com.thfh.model.User;
import com.thfh.repository.CheckInRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * 签到服务类
 * 提供用户签到相关的业务逻辑处理，包括签到、补签、查询签到记录等功能
 */
@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 检查当前用户今日是否已签到
     * @return 如果今日已签到则返回true，否则返回false
     */
    public boolean isCheckedInToday() {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        
        // 检查今日是否已签到
        List<CheckIn> todayCheckIns = checkInRepository.findTodayCheckIn(userId);
        boolean checkInToday = !todayCheckIns.isEmpty();
        
        System.out.println("用户ID: " + userId + ", 今日是否已签到: " + checkInToday);
        
        return checkInToday;
    }

    /**
     * 用户签到
     * @return 签到记录
     * @throws RuntimeException 当用户已签到时抛出
     */
    @Transactional
    public CheckIn checkIn() {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // 检查今日是否已签到
        List<CheckIn> todayCheckIns = checkInRepository.findTodayCheckIn(userId);
        if (!todayCheckIns.isEmpty()) {
            throw new RuntimeException("今日已经签到过了");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setIsMakeup(false);
        return checkInRepository.save(checkIn);
    }

    /**
     * 用户补签
     * @param checkInDate 需要补签的日期
     * @return 补签记录
     * @throws RuntimeException 当补签卡不足、日期无效或已签到时抛出
     */
    @Transactional
    public CheckIn makeupCheckIn(LocalDate checkInDate) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // 检查用户是否有补签卡
        if (currentUser.getMakeupCards() <= 0) {
            throw new RuntimeException("补签卡数量不足");
        }

        // 检查是否为未来日期
        LocalDate today = LocalDate.now();
        if (checkInDate.isAfter(today)) {
            throw new RuntimeException("不能补签未来日期");
        }

        // 检查是否为当天日期
        if (checkInDate.isEqual(today)) {
            throw new RuntimeException("当天请使用普通签到功能");
        }

        // 检查补签日期是否已签到，使用日期范围查询
        LocalDateTime startOfDay = checkInDate.atStartOfDay();
        LocalDateTime endOfDay = checkInDate.plusDays(1).atStartOfDay().minusNanos(1);

        System.out.println("准备查询日期范围: 从" + startOfDay + " 到 " + endOfDay + " 用户ID: " + userId);
        List<CheckIn> existingCheckIns = checkInRepository.findByUserIdAndDateRange(userId, startOfDay, endOfDay);
        System.out.println("查询结果: " + (existingCheckIns == null ? "null" : existingCheckIns.size() + "条记录"));

        if (existingCheckIns != null && !existingCheckIns.isEmpty()) {
            System.out.println("已存在记录: " + existingCheckIns.get(0).getCheckInTime());
            throw new RuntimeException("该日期已经签到过了");
        }

        // 扣除补签卡
        currentUser.setMakeupCards(currentUser.getMakeupCards() - 1);
        userRepository.save(currentUser);

        // 创建补签记录
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        // 设置为指定日期的中午时间，避免时区问题
        LocalDateTime checkInDateTime = checkInDate.atTime(12, 0);
        System.out.println("设置补签时间: " + checkInDateTime);
        checkIn.setCheckInTime(checkInDateTime);
        checkIn.setIsMakeup(true);
        return checkInRepository.save(checkIn);
    }

    /**
     * 获取当前用户本月签到次数
     * @return 本月签到次数
     */
    public Long getMonthlyCheckInCount() {
        User currentUser = userService.getCurrentUser();
        return checkInRepository.countMonthlyCheckIns(currentUser.getId());
    }

    /**
     * 获取当前用户连续签到次数
     * @return 连续签到次数和今日是否已签到的状态
     */
    public Result<Object> getConsecutiveCheckInCount() {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        
        // 检查今日是否已签到
        boolean isCheckedInToday = isCheckedInToday();

        // 使用findRecentCheckIns代替findMonthlyCheckIns，以便能够跨月份计算连续签到次数
        List<CheckIn> recentCheckIns = checkInRepository.findRecentCheckIns(userId);
        if (recentCheckIns.isEmpty()) {
            JSONObject result = new JSONObject();
            result.put("consecutiveCount", 0);
            result.put("isCheckedInToday", isCheckedInToday);
            return Result.success(result.toJSONString());
        }

        // 获取最近一次签到日期
        LocalDate lastCheckInDate = recentCheckIns.get(0).getCheckInTime().toLocalDate();
        LocalDate today = LocalDate.now();

        System.out.println("用户ID: " + userId + ", 最近签到日期: " + lastCheckInDate + ", 今天日期: " + today);

        // 计算连续签到天数
        long consecutiveCount = 1; // 至少有一条记录，所以从1开始
        
        // 对于当前最新的签到记录，判断它是否是连续签到的起点
        // 最近签到时间可以是今天或者是之前的某一天
        LocalDate expectedPreviousDate = lastCheckInDate.minusDays(1);
        
        for (int i = 1; i < recentCheckIns.size(); i++) {
            LocalDate checkInDate = recentCheckIns.get(i).getCheckInTime().toLocalDate();
            
            System.out.println("检查签到记录 " + i + ": " + checkInDate + ", 期望日期: " + expectedPreviousDate);
            
            // 检查是否是期望的前一天
            if (checkInDate.equals(expectedPreviousDate)) {
                consecutiveCount++;
                expectedPreviousDate = expectedPreviousDate.minusDays(1);
            } else {
                // 如果不是连续的，就退出循环
                System.out.println("检测到非连续日期，中断计数");
                break;
            }
        }

        System.out.println("用户ID: " + userId + ", 连续签到天数: " + consecutiveCount + ", 今日是否已签到: " + isCheckedInToday);
        
        JSONObject result = new JSONObject();
        result.put("consecutiveCount", consecutiveCount);
        result.put("isCheckedInToday", isCheckedInToday);
        return Result.success(result.toJSONString());
    }

    /**
     * 为当前用户添加补签卡
     * @param count 需要添加的补签卡数量
     * @return 添加后的补签卡总数量
     * @throws RuntimeException 当补签卡数量不合法时抛出
     */
    @Transactional
    public Integer addMakeupCards(Integer count) {
        if (count <= 0) {
            throw new RuntimeException("补签卡数量必须大于0");
        }

        User currentUser = userService.getCurrentUser();

        // 检查补签卡数量是否为NULL，如果是则设置为0
        if (currentUser.getMakeupCards() == null) {
            currentUser.setMakeupCards(0);
        }

        // 增加补签卡数量
        currentUser.setMakeupCards(currentUser.getMakeupCards() + count);
        userRepository.save(currentUser);

        return currentUser.getMakeupCards();
    }

    /**
     * 获取当前用户的签到历史记录
     * @param pageable 分页参数
     * @return 分页的签到历史记录
     */
    public CustomPage<CheckIn> getUserCheckInHistory(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<CheckIn> page = checkInRepository.findByUserIdOrderByCheckInTimeDesc(currentUser.getId(), pageable);
        return new CustomPage<>(page);
    }
}