package com.thfh.service;

import com.thfh.dto.ArtworkReportRequest;
import com.thfh.dto.ArtworkReportResponse;
import com.thfh.model.ArtworkReport;
import com.thfh.repository.ArtworkReportRepository;
import com.thfh.common.Result;
import com.thfh.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ArtworkReportService {
    @Autowired
    private ArtworkReportRepository artworkReportRepository;

    public Result<?> submitReport(ArtworkReportRequest request) {
        // 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long reporterId = null;
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            // 这里假设UserDetails的username为用户ID字符串
            reporterId = Long.valueOf(authentication.getName());
        } else {
            throw new BusinessException("无法获取当前用户信息");
        }
        // 防止重复举报
        Optional<ArtworkReport> exist = artworkReportRepository.findByArtworkIdAndReporterId(request.getArtworkId(), reporterId);
        if (exist.isPresent()) {
            return Result.error("您已举报过该作品");
        }
        ArtworkReport report = new ArtworkReport();
        report.setArtworkId(request.getArtworkId());
        report.setReporterId(reporterId);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setCreateTime(LocalDateTime.now());
        report.setStatus("待处理");
        artworkReportRepository.save(report);
        return Result.success();
    }

    public Result<?> getReportList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ArtworkReport> reportPage = artworkReportRepository.findAll(pageable);
        com.thfh.common.CustomPage<ArtworkReportResponse> customPage = new com.thfh.common.CustomPage<>(reportPage.map(report -> {
            ArtworkReportResponse dto = new ArtworkReportResponse();
            BeanUtils.copyProperties(report, dto);
            return dto;
        }));
        return Result.success(customPage);
    }
} 