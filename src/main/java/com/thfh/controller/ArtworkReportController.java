package com.thfh.controller;

import com.thfh.dto.ArtworkReportRequest;
import com.thfh.service.ArtworkReportService;
import com.thfh.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/artwork/report")
public class ArtworkReportController {
    @Autowired
    private ArtworkReportService artworkReportService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    public Result<?> submitReport(@Validated @RequestBody ArtworkReportRequest request) {
        return artworkReportService.submitReport(request);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<?> getReportList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return artworkReportService.getReportList(page, size);
    }
} 