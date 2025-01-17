package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    public Result<Page<JobDTO>> getJobs(JobQueryDTO queryDTO) {
        return Result.success(jobService.getJobs(queryDTO));
    }

    @PostMapping
    public Result<JobDTO> createJob(@RequestBody JobDTO jobDTO) {
        return Result.success(jobService.createJob(jobDTO));
    }

    @PutMapping("/{id}")
    public Result<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        return Result.success(jobService.updateJob(id, jobDTO));
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publishJob(@PathVariable Long id) {
        jobService.publishJob(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/close")
    public Result<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleJobStatus(@PathVariable Long id) {
        jobService.toggleJobStatus(id);
        return Result.success(null);
    }
} 