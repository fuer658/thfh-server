package com.thfh.controller;

import com.thfh.model.CoursePointsPurchase;
import com.thfh.service.CoursePointsPurchaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 课程积分购买控制器
 */
@RestController
@RequestMapping("/api/course-purchase")
@Api(tags = "课程积分购买", description = "课程积分购买相关接口")
public class CoursePointsPurchaseController {

    @Autowired
    private CoursePointsPurchaseService purchaseService;

    /**
     * 使用积分购买课程
     */
    @PostMapping("/points/{courseId}")
    @ApiOperation(value = "积分购买课程", notes = "用户使用积分购买指定课程")
    public ResponseEntity<CoursePointsPurchase> purchaseCourseWithPoints(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId,
            Principal principal) {
        
        // 假设 Principal 中存的是用户ID的字符串形式
        Long userId = Long.parseLong(principal.getName());
        CoursePointsPurchase purchase = purchaseService.purchaseCourseWithPoints(userId, courseId);
        return ResponseEntity.ok(purchase);
    }

    /**
     * 获取用户购买记录
     */
    @GetMapping("/user/history")
    @ApiOperation(value = "获取用户购买记录", notes = "分页获取当前用户的课程购买记录")
    public ResponseEntity<Page<CoursePointsPurchase>> getUserPurchaseHistory(
            @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "每页条数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        
        Long userId = Long.parseLong(principal.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CoursePointsPurchase> purchaseHistory = purchaseService.getUserPurchaseRecords(userId, pageable);
        return ResponseEntity.ok(purchaseHistory);
    }

    /**
     * 检查用户是否已购买课程
     */
    @GetMapping("/check/{courseId}")
    @ApiOperation(value = "检查购买状态", notes = "检查当前用户是否已购买指定课程")
    public ResponseEntity<Map<String, Boolean>> checkPurchaseStatus(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId,
            Principal principal) {
        
        Long userId = Long.parseLong(principal.getName());
        boolean hasPurchased = purchaseService.hasPurchasedCourse(userId, courseId);
        
        // 使用Java 8兼容的方式创建Map
        Map<String, Boolean> result = new HashMap<>();
        result.put("purchased", hasPurchased);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取购买记录详情
     */
    @GetMapping("/{purchaseId}")
    @ApiOperation(value = "获取购买记录详情", notes = "根据购买记录ID获取详细信息")
    public ResponseEntity<?> getPurchaseDetails(
            @ApiParam(value = "购买记录ID", required = true) @PathVariable Long purchaseId,
            Principal principal) {
        
        return purchaseService.getPurchaseDetails(purchaseId)
                .map(purchase -> {
                    // 检查是否是自己的购买记录
                    Long userId = Long.parseLong(principal.getName());
                    if (!purchase.getUser().getId().equals(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问此购买记录");
                    }
                    return ResponseEntity.ok(purchase);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 管理员查看课程的所有购买记录
     */
    @GetMapping("/admin/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "管理员查看课程购买记录", notes = "管理员查看指定课程的所有购买记录")
    public ResponseEntity<List<CoursePointsPurchase>> getCoursePurchaseRecords(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId) {
        
        List<CoursePointsPurchase> purchases = purchaseService.getCoursePurchases(courseId);
        return ResponseEntity.ok(purchases);
    }

    /**
     * 管理员处理退款
     */
    @PostMapping("/admin/refund/{purchaseId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "处理退款", notes = "管理员处理购买记录的退款")
    public ResponseEntity<CoursePointsPurchase> processRefund(
            @ApiParam(value = "购买记录ID", required = true) @PathVariable Long purchaseId) {
        
        CoursePointsPurchase refundedPurchase = purchaseService.refundPurchase(purchaseId);
        return ResponseEntity.ok(refundedPurchase);
    }
} 