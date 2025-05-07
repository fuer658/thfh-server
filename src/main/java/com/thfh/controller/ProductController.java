package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ProductDTO;
import com.thfh.dto.ProductSearchDTO;
import com.thfh.model.Product;
import com.thfh.model.ProductStatus;
import com.thfh.model.User;
import com.thfh.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品管理控制器
 * 提供商品相关的API接口，包括商品的创建、查询、更新、删除以及点赞、收藏等功能
 */
@Api(tags = "商品管理", description = "商品相关的API接口，包括商品的创建、查询、更新、删除以及点赞、收藏等功能")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 创建商品
     * @param productDTO 商品信息
     * @return 创建的商品信息
     */
    @ApiOperation(value = "创建商品", notes = "管理员创建新商品，需要提供商品的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限创建商品")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> createProduct(
            @ApiParam(value = "商品信息", required = true) @Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.createProduct(productDTO));
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详细信息
     */
    @ApiOperation(value = "获取商品详情", notes = "根据商品ID获取商品的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @GetMapping("/{id}")
    public Result<ProductDTO> getProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        return Result.success(productService.getProduct(id));
    }

    /**
     * 获取所有商品
     * @param pageable 分页信息
     * @return 商品分页列表
     */
    @ApiOperation(value = "获取所有商品", notes = "分页获取所有商品的列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    @GetMapping
    public Result<Page<Product>> getAllProducts(
            @ApiParam(value = "分页信息") Pageable pageable) {
        return Result.success(productService.getAllProducts(pageable));
    }

    /**
     * 更新商品
     * @param id 商品ID
     * @param productDTO 更新的商品信息
     * @return 更新后的商品信息
     */
    @ApiOperation(value = "更新商品", notes = "管理员根据商品ID更新商品信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限更新商品"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> updateProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的商品信息", required = true) @Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.updateProduct(id, productDTO));
    }

    /**
     * 删除商品
     * @param id 商品ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除商品", notes = "管理员根据商品ID删除商品")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除商品"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }

    /**
     * 搜索商品
     * @param searchDTO 搜索条件
     * @return 符合条件的商品列表
     */
    @ApiOperation(value = "搜索商品", notes = "根据搜索条件查询符合条件的商品")
    @ApiResponses({
        @ApiResponse(code = 200, message = "搜索成功")
    })
    @GetMapping("/search")
    public Result<Page<Product>> searchProducts(
            @ApiParam(value = "搜索条件") @Valid ProductSearchDTO searchDTO) {
        return Result.success(productService.searchProducts(searchDTO));
    }

    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 商品状态
     * @return 更新后的商品信息
     */
    @ApiOperation(value = "更新商品状态", notes = "管理员更新商品的状态，如上架、下架等")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限更新商品状态"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> updateProductStatus(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @ApiParam(value = "商品状态", required = true) @RequestParam ProductStatus status) {
        return Result.success(productService.updateProductStatus(id, status));
    }

    /**
     * 获取指定状态的商品
     * @param status 商品状态
     * @param pageable 分页信息
     * @return 指定状态的商品列表
     */
    @ApiOperation(value = "获取指定状态的商品", notes = "根据商品状态分页获取商品列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    @GetMapping("/status/{status}")
    public Result<Page<Product>> getProductsByStatus(
            @ApiParam(value = "商品状态", required = true) @PathVariable ProductStatus status,
            @ApiParam(value = "分页信息") Pageable pageable) {
        return Result.success(productService.getProductsByStatus(status, pageable));
    }

    /**
     * 点赞商品
     * @param productId 商品ID
     * @param userDetails 当前登录用户信息
     * @return 操作结果
     */
    @ApiOperation(value = "点赞商品", notes = "用户对商品进行点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "点赞成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> likeProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable("id") Long productId,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.likeProduct(productId, user.getId());
        return Result.success(null);
    }

    /**
     * 取消点赞商品
     * @param productId 商品ID
     * @param userDetails 当前登录用户信息
     * @return 操作结果
     */
    @ApiOperation(value = "取消点赞商品", notes = "用户取消对商品的点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消点赞成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "商品不存在或未点赞")
    })
    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> unlikeProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable("id") Long productId,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unlikeProduct(productId, user.getId());
        return Result.success(null);
    }

    /**
     * 收藏商品
     * @param productId 商品ID
     * @param userDetails 当前登录用户信息
     * @return 操作结果
     */
    @ApiOperation(value = "收藏商品", notes = "用户收藏商品")
    @ApiResponses({
        @ApiResponse(code = 200, message = "收藏成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> favoriteProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable("id") Long productId,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.favoriteProduct(productId, user.getId());
        return Result.success(null);
    }

    /**
     * 取消收藏商品
     * @param productId 商品ID
     * @param userDetails 当前登录用户信息
     * @return 操作结果
     */
    @ApiOperation(value = "取消收藏商品", notes = "用户取消对商品的收藏")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消收藏成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "商品不存在或未收藏")
    })
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> unfavoriteProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable("id") Long productId,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unfavoriteProduct(productId, user.getId());
        return Result.success(null);
    }
    
    /**
     * 获取用户收藏的商品列表
     * @param pageable 分页信息
     * @param userDetails 当前登录用户信息
     * @return 用户收藏的商品列表
     */
    @ApiOperation(value = "获取用户收藏的商品列表", notes = "分页获取当前登录用户收藏的商品列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Page<Product>> getUserFavorites(
            @ApiParam(value = "分页信息") Pageable pageable,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return Result.success(productService.getUserFavorites(user.getId(), pageable));
    }
    
    /**
     * 检查商品收藏状态
     * @param productId 商品ID
     * @param userDetails 当前登录用户信息
     * @return 商品是否已被当前用户收藏
     */
    @ApiOperation(value = "检查商品收藏状态", notes = "检查当前登录用户是否已收藏指定商品")
    @ApiResponses({
        @ApiResponse(code = 200, message = "检查成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "商品不存在")
    })
    @GetMapping("/{id}/favorite/status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Map<String, Boolean>> checkFavoriteStatus(
            @ApiParam(value = "商品ID", required = true) @PathVariable("id") Long productId,
            @ApiParam(value = "当前登录用户信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        boolean isFavorited = productService.isProductFavorited(productId, user.getId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("favorited", isFavorited);
        return Result.success(response);
    }
} 