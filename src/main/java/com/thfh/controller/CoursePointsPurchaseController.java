package com.thfh.controller;

import com.thfh.model.CoursePointsPurchase;
import com.thfh.model.User;
import com.thfh.service.CoursePointsPurchaseService;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "课程积分购买", description = "课程积分购买相关接口")
public class CoursePointsPurchaseController {

    @Autowired
    private CoursePointsPurchaseService purchaseService;
    
    @Autowired
    private UserService userService;

    /**
     * 使用积分购买课程
     */
    @PostMapping("/points/{courseId}")
    @Operation(summary = "积分购买课程", description = "用户使用积分购买指定课程")
    public ResponseEntity<CoursePointsPurchase> purchaseCourseWithPoints(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        
        // 使用userService获取当前用户
        User currentUser = userService.getCurrentUser();
        CoursePointsPurchase purchase = purchaseService.purchaseCourseWithPoints(currentUser.getId(), courseId);
        return ResponseEntity.ok(purchase);
    }

    /**
     * 获取用户购买记录
     */
    @GetMapping("/user/history")
    @Operation(summary = "获取用户购买记录", description = "分页获取当前用户的课程购买记录")
    public ResponseEntity<Page<CoursePointsPurchase>> getUserPurchaseHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        
        // 使用userService获取当前用户
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CoursePointsPurchase> purchaseHistory = purchaseService.getUserPurchaseRecords(currentUser.getId(), pageable);
        return ResponseEntity.ok(purchaseHistory);
    }

    /**
     * 检查用户是否已购买课程
     */
    @GetMapping("/check/{courseId}")
    @Operation(summary = "检查购买状态", description = "检查当前用户是否已购买指定课程")
    public ResponseEntity<Map<String, Boolean>> checkPurchaseStatus(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        
        // 使用userService获取当前用户
        User currentUser = userService.getCurrentUser();
        boolean hasPurchased = purchaseService.hasPurchasedCourse(currentUser.getId(), courseId);
        
        // 使用Java 8兼容的方式创建Map
        Map<String, Boolean> result = new HashMap<>();
        result.put("purchased", hasPurchased);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取购买记录详情
     */
    @GetMapping("/{purchaseId}")
    @Operation(summary = "获取购买记录详情", description = "根据购买记录ID获取详细信息")
    public ResponseEntity<?> getPurchaseDetails(
            @Parameter(description = "购买记录ID", required = true) @PathVariable Long purchaseId) {
        
        // 使用userService获取当前用户
        User currentUser = userService.getCurrentUser();
        
        return purchaseService.getPurchaseDetails(purchaseId)
                .map(purchase -> {
                    // 检查是否是自己的购买记录
                    if (!purchase.getUser().getId().equals(currentUser.getId())) {
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
    @Operation(summary = "管理员查看课程购买记录", description = "管理员查看指定课程的所有购买记录")
    public ResponseEntity<List<CoursePointsPurchase>> getCoursePurchaseRecords(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        
        List<CoursePointsPurchase> purchases = purchaseService.getCoursePurchases(courseId);
        return ResponseEntity.ok(purchases);
    }

    /**
     * 管理员处理退款
     */
    @PostMapping("/admin/refund/{purchaseId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "处理退款", description = "管理员处理购买记录的退款")
    public ResponseEntity<CoursePointsPurchase> processRefund(
            @Parameter(description = "购买记录ID", required = true) @PathVariable Long purchaseId) {
        
        CoursePointsPurchase refundedPurchase = purchaseService.refundPurchase(purchaseId);
        return ResponseEntity.ok(refundedPurchase);
    }
} 