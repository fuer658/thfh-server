package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.WorkDTO;
import com.thfh.dto.WorkQueryDTO;
import com.thfh.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/works")
public class WorkController {
    @Autowired
    private WorkService workService;

    @GetMapping
    public Result<Page<WorkDTO>> getWorks(WorkQueryDTO queryDTO) {
        return Result.success(workService.getWorks(queryDTO));
    }

    @PostMapping
    public Result<WorkDTO> createWork(@RequestBody WorkDTO workDTO) {
        return Result.success(workService.createWork(workDTO));
    }

    @PutMapping("/{id}")
    public Result<WorkDTO> updateWork(@PathVariable Long id, @RequestBody WorkDTO workDTO) {
        return Result.success(workService.updateWork(id, workDTO));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approveWork(@PathVariable Long id) {
        workService.approveWork(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/reject")
    public Result<Void> rejectWork(@PathVariable Long id) {
        workService.rejectWork(id);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleWorkStatus(@PathVariable Long id) {
        workService.toggleWorkStatus(id);
        return Result.success(null);
    }
} 