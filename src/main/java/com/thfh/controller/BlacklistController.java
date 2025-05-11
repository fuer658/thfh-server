package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.BlacklistDTO;
import com.thfh.service.BlacklistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Api(tags = "黑名单管理")
@RestController
@RequestMapping("/api/blacklist")
public class BlacklistController {
    @Autowired
    private BlacklistService blacklistService;

    @ApiOperation("拉黑用户")
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @PostMapping("/add")
    public Result<Void> addToBlacklist(
            @ApiParam("被拉黑用户ID") @RequestParam Long blockedId,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        blacklistService.addToBlacklist(userId, blockedId);
        return Result.success();
    }

    @ApiOperation("移除黑名单")
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @PostMapping("/remove")
    public Result<Void> removeFromBlacklist(
            @ApiParam("被移除黑名单用户ID") @RequestParam Long blockedId,
            Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        blacklistService.removeFromBlacklist(userId, blockedId);
        return Result.success();
    }

    @ApiOperation("查询我的黑名单列表")
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @GetMapping("/list")
    public Result<List<BlacklistDTO>> getBlacklist(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        List<BlacklistDTO> list = blacklistService.getBlacklist(userId);
        return Result.success(list);
    }
} 