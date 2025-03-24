package com.thfh.service;

import com.thfh.common.Result;
import com.thfh.model.CheckIn;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.thfh.common.CustomPage;

public interface CheckInService {
    CheckIn checkIn();
    CheckIn makeupCheckIn(LocalDate checkInDate);
    Long getMonthlyCheckInCount();
    Result<Long> getConsecutiveCheckInCount();
    CustomPage<CheckIn> getUserCheckInHistory(Pageable pageable);
    Integer addMakeupCards(Integer count);
}