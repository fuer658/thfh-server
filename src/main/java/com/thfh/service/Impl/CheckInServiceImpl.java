package com.thfh.service.Impl;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import com.thfh.model.User;
import com.thfh.repository.CheckInRepository;
import com.thfh.repository.UserRepository;
import com.thfh.service.CheckInService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
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

    @Override
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

    @Override
    public Long getMonthlyCheckInCount() {
        User currentUser = userService.getCurrentUser();
        return checkInRepository.countMonthlyCheckIns(currentUser.getId());
    }

    @Override
    public Result<Long> getConsecutiveCheckInCount() {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        
        List<CheckIn> monthlyCheckIns = checkInRepository.findMonthlyCheckIns(userId);
        if (monthlyCheckIns.isEmpty()) {
            return Result.success(0L);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastCheckInDate = monthlyCheckIns.get(0).getCheckInTime().toLocalDate();
        
        if (!lastCheckInDate.equals(today)) {
            return Result.success(0L);
        }

        long consecutiveCount = 1;
        for (int i = 1; i < monthlyCheckIns.size(); i++) {
            LocalDate currentDate = monthlyCheckIns.get(i).getCheckInTime().toLocalDate();
            LocalDate previousDate = monthlyCheckIns.get(i - 1).getCheckInTime().toLocalDate();
            
            if (currentDate.plusDays(1).equals(previousDate)) {
                consecutiveCount++;
            } else {
                break;
            }
        }

        return Result.success(consecutiveCount);
    }

    @Override
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

    @Override
    public CustomPage<CheckIn> getUserCheckInHistory(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<CheckIn> page = checkInRepository.findByUserIdOrderByCheckInTimeDesc(currentUser.getId(), pageable);
        return new CustomPage<>(page);
    }
}