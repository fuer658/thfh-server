package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ArtworkDTO;
import com.thfh.dto.ArtworkScoreDTO;
import com.thfh.dto.ArtworkUpdateDTO;
import com.thfh.dto.FollowDTO;
import com.thfh.dto.TagDTO;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.service.AdminService;
import com.thfh.service.ArtworkService;
import com.thfh.service.ArtworkScoreService;
import com.thfh.service.UserService;
import com.thfh.service.FollowService;
import com.thfh.exception.ResourceNotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

/**
 * 作品管理控制器
 * 提供作品相关的API接口，包括作品发布、评分、查询、删除和编辑等功能
 */
@Api(tags = "作品管理", description = "作品相关的API接口，包括作品发布、评分、查询、删除和编辑等功能")
@RestController
@RequestMapping("/api/artworks")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ArtworkController {

    private final UserService userService;
    private final ArtworkService artworkService;
    private final ArtworkScoreService artworkScoreService;
    private final AdminService adminService;
    private final FollowService followService;

    /**
     * 发布作品
     * @param artwork 作品信息
     * @param user 当前登录用户
     * @return 创建的作品
     */
    @ApiOperation(value = "发布作品", notes = "发布新的作品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "发布成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<ArtworkDTO> createArtwork(
            @ApiParam(value = "作品信息", required = true) @Valid @RequestBody Artwork artwork,
            @ApiParam(hidden = true) @AuthenticationPrincipal User user) {
        // 判断用户是否为空，如果为空则通过service获取当前用户
        if (user == null) {
            user = userService.getCurrentUser();
            if (user == null) {
                return Result.unauthorized("用户未登录或会话已过期，请重新登录");
            }
        }
        
        log.info("用户 {} 正在创建新作品", user.getUsername());
        
        artwork.setCreator(user);
        artwork.setCreateTime(LocalDateTime.now());
        artwork.setEnabled(true);
        Artwork createdArtwork = artworkService.createArtwork(artwork);
        
        log.info("用户 {} 成功创建作品: {}", user.getUsername(), createdArtwork.getId());
        
        // 将实体转换为DTO后返回，避免懒加载问题
        ArtworkDTO artworkDTO = convertToArtworkDTO(createdArtwork);
        return Result.success(artworkDTO, "作品发布成功");
    }

    /**
     * 为作品评分
     * @param artworkId 作品ID
     * @param scoreDTO 评分信息
     * @param authentication 认证信息
     * @return 评分结果
     */
    @ApiOperation(value = "为作品评分", notes = "为指定作品进行评分")
    @ApiResponses({
            @ApiResponse(code = 200, message = "评分成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/{artworkId}/score")
    public Result<Void> scoreArtwork(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(value = "评分信息", required = true) @Valid @RequestBody ArtworkScoreDTO scoreDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("正在为作品 {} 评分: {}", artworkId, scoreDTO.getScore());
        
        User user = userService.getCurrentUser();
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在，ID: " + artworkId));
                
        artworkScoreService.scoreArtwork(artworkId, user.getId(), scoreDTO.getScore(), user, artwork);
        
        log.info("用户 {} 成功为作品 {} 评分", user.getUsername(), artworkId);
        return Result.success(null, "评分成功");
    }

    /**
     * 获取作品的评分信息
     * @param artworkId 作品ID
     * @return 评分信息
     */
    @ApiOperation(value = "获取作品的评分信息", notes = "获取指定作品的平均评分和评分人数")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @GetMapping("/{artworkId}/score")
    public Result<Map<String, Object>> getArtworkScore(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId) {
        log.debug("获取作品 {} 的评分信息", artworkId);
        
        // 验证作品是否存在
        artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在，ID: " + artworkId));
                
        Map<String, Object> scoreInfo = new HashMap<>();
        scoreInfo.put("averageScore", artworkScoreService.getArtworkAverageScore(artworkId));
        scoreInfo.put("scoreCount", artworkScoreService.getArtworkScoreCount(artworkId));
        
        return Result.success(scoreInfo);
    }

    /**
     * 获取作品列表（分页）
     * @param page 页码
     * @param size 每页数量
     * @param title 作品标题（可选）
     * @param studentId 学生ID（可选）
     * @param enabled 是否启用（可选）
     * @return 作品列表
     */
    @ApiOperation(value = "获取作品列表", notes = "根据查询条件获取作品分页列表，支持按标题、学生ID和启用状态筛选")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })
    @GetMapping
    public Result<Page<ArtworkDTO>> getArtworks(
            @ApiParam(value = "页码", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @PositiveOrZero(message = "页码必须大于或等于0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") int size,
            @ApiParam(value = "作品标题（可选）") @RequestParam(required = false) String title,
            @ApiParam(value = "学生ID（可选）") @RequestParam(required = false) Long studentId,
            @ApiParam(value = "是否启用（可选）") @RequestParam(required = false) Boolean enabled) {
        log.debug("获取作品列表: 页码={}, 大小={}, 标题={}, 学生ID={}, 启用状态={}", page, size, title, studentId, enabled);
        
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Artwork> artworkPage = artworkService.getArtworks(title, studentId, enabled, pageRequest);
        
        // 转换为DTO
        Page<ArtworkDTO> dtoPage = convertToArtworkDTOPage(artworkPage);
        
        return Result.success(dtoPage);
    }

    /**
     * 获取我的作品列表
     * @param authentication 认证信息
     * @param type 作品类型（可选）
     * @param enabled 是否启用（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 我的作品列表
     */
    @ApiOperation(value = "获取我的作品列表", notes = "获取当前用户发布的作品分页列表，支持按作品类型和启用状态筛选")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/my")
    public Result<Page<ArtworkDTO>> getMyArtworks(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "作品类型（可选）") @RequestParam(required = false) ArtworkType type,
            @ApiParam(value = "是否启用（可选）") @RequestParam(required = false) Boolean enabled,
            @ApiParam(value = "页码", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @PositiveOrZero(message = "页码必须大于或等于0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") int size) {
        log.debug("获取当前用户作品列表: 页码={}, 大小={}, 类型={}, 启用状态={}", page, size, type, enabled);
        
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<Artwork> artworks;
        if (type != null && enabled != null) {
            artworks = artworkService.getUserArtworksByTypeAndEnabled(user.getId(), type, enabled, pageRequest);
        } else if (type != null) {
            artworks = artworkService.getUserArtworksByType(user.getId(), type, pageRequest);
        } else if (enabled != null) {
            artworks = artworkService.getUserArtworksByEnabled(user.getId(), enabled, pageRequest);
        } else {
            artworks = artworkService.getUserArtworks(user.getId(), pageRequest);
        }
        
        // 转换为DTO
        Page<ArtworkDTO> dtoPage = convertToArtworkDTOPage(artworks);
        
        return Result.success(dtoPage);
    }

    /**
     * 删除作品
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 删除结果
     */
    @ApiOperation(value = "删除作品", notes = "根据作品ID删除作品，仅作品创建者可操作")
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 403, message = "没有权限删除该作品"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @DeleteMapping("/{artworkId}")
    public Result<Void> deleteArtwork(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("用户请求删除作品: {}", artworkId);
        
        User user = userService.getCurrentUser();
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在，ID: " + artworkId));
        
        // 验证当前用户是否为作品创建者
        if (!artwork.getCreator().getId().equals(user.getId())) {
            log.warn("用户 {} 尝试删除不属于他的作品 {}", user.getUsername(), artworkId);
            return Result.error(HttpStatus.FORBIDDEN.value(), "您没有权限删除该作品");
        }
        
        artworkService.deleteArtwork(artworkId);
        log.info("用户 {} 成功删除作品 {}", user.getUsername(), artworkId);
        
        return Result.success(null, "作品删除成功");
    }

    /**
     * 管理员删除作品
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 删除结果
     */
    @ApiOperation(value = "管理员删除作品", notes = "管理员根据作品ID删除作品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 403, message = "没有管理员权限"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @DeleteMapping("/admin/{artworkId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> adminDeleteArtwork(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("管理员请求删除作品: {}", artworkId);
        
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        if (!adminService.isAdmin(username)) {
            log.warn("用户 {} 尝试使用管理员权限删除作品 {}", username, artworkId);
            return Result.error(HttpStatus.FORBIDDEN.value(), "您没有管理员权限");
        }
        
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            throw new ResourceNotFoundException("作品不存在，ID: " + artworkId);
        }
        
        artworkService.deleteArtwork(artworkId);
        log.info("管理员 {} 成功删除作品 {}", username, artworkId);
        
        return Result.success(null, "作品删除成功");
    }

    /**
     * 添加新标签
     * @param tagDTO 标签信息
     * @param authentication 认证信息
     * @return 操作结果
     */
    @ApiOperation(value = "添加新标签", notes = "添加新的作品标签")
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public Result<Void> addTag(
            @ApiParam(value = "标签信息", required = true) @Valid @RequestBody TagDTO tagDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("添加新标签: {}", tagDTO.getTagName());
        
        artworkService.addTag(tagDTO.getTagName());
        
        return Result.success(null, "标签添加成功");
    }

    /**
     * 删除标签
     * @param tagId 标签ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @ApiOperation(value = "删除标签", notes = "根据标签ID删除作品标签")
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "标签不存在")
    })
    @DeleteMapping("/tags/{tagId}")
    public Result<Void> removeTag(
            @ApiParam(value = "标签ID", required = true) 
            @PathVariable @Positive(message = "标签ID必须为正数") Long tagId,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("删除标签: {}", tagId);
        
        artworkService.removeTag(tagId);
        
        return Result.success(null, "标签删除成功");
    }

    /**
     * 管理员编辑作品
     * @param artworkId 作品ID
     * @param updateDTO 更新的作品信息
     * @param authentication 认证信息
     * @return 更新结果
     */
    @ApiOperation(value = "管理员编辑作品", notes = "管理员根据作品ID编辑作品信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 403, message = "没有管理员权限"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PutMapping("/admin/{artworkId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> adminUpdateArtwork(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(value = "更新的作品信息", required = true) @Valid @RequestBody ArtworkUpdateDTO updateDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        log.info("管理员正在更新作品 {}: {}", artworkId, updateDTO);
        
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        if (!adminService.isAdmin(username)) {
            log.warn("用户 {} 尝试使用管理员权限更新作品 {}", username, artworkId);
            return Result.error(HttpStatus.FORBIDDEN.value(), "您没有管理员权限");
        }
        
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            throw new ResourceNotFoundException("作品不存在，ID: " + artworkId);
        }
        
        artworkService.updateArtwork(artworkId, updateDTO);
        log.info("管理员 {} 成功更新作品 {}", username, artworkId);
        
        return Result.success(null, "作品更新成功");
    }

    /**
     * 修改作品推荐状态
     * @param artworkId 作品ID
     * @param recommended 是否推荐（0-不推荐，1-推荐）
     * @return 更新结果
     */
    @ApiOperation(value = "修改作品推荐状态", notes = "修改作品的推荐状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PutMapping("/{artworkId}/recommend")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateArtworkRecommendation(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(value = "是否推荐（0-不推荐，1-推荐）", defaultValue = "0") 
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "推荐值必须为0或1") Integer recommended) {
        log.info("修改作品 {} 的推荐状态为: {}", artworkId, recommended);
        
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            throw new ResourceNotFoundException("作品不存在，ID: " + artworkId);
        }
        
        artworkService.updateArtworkRecommendation(artworkId, recommended == 1);
        
        return Result.success(null, recommended == 1 ? "作品已设为推荐" : "作品已取消推荐");
    }

    /**
     * 获取作品详情
     * @param artworkId 作品ID
     * @return 作品详情
     */
    @ApiOperation(value = "获取作品详情", notes = "根据作品ID获取作品详细信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @GetMapping("/{artworkId}")
    public Result<ArtworkDTO> getArtworkDetail(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId) {
        log.debug("获取作品详情: {}", artworkId);
        
        // 增加浏览量
        artworkService.incrementViewCount(artworkId);
        
        // 获取作品信息
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在，ID: " + artworkId));
        
        // 转换为DTO
        ArtworkDTO dto = convertToArtworkDTO(artwork);
        
        return Result.success(dto);
    }

    /**
     * 修改商业作品价格
     * @param artworkId 作品ID
     * @param price 新价格
     * @return 更新结果
     */
    @ApiOperation(value = "修改商业作品价格", notes = "修改商业作品的价格")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PutMapping("/{artworkId}/price")
    public Result<Void> updateArtworkPrice(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(value = "新价格", required = true) 
            @RequestParam @NotNull(message = "价格不能为空") @PositiveOrZero(message = "价格必须大于或等于0") BigDecimal price) {
        log.info("修改作品 {} 的价格为: {}", artworkId, price);
        
        // 检查作品是否存在
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("作品不存在，ID: " + artworkId));
        
        // 检查当前用户是否有权限修改价格
        User user = userService.getCurrentUser();
        if (!artwork.getCreator().getId().equals(user.getId()) && !adminService.isAdmin(user.getUsername())) {
            log.warn("用户 {} 尝试修改不属于他的作品 {} 的价格", user.getUsername(), artworkId);
            return Result.error(HttpStatus.FORBIDDEN.value(), "您没有权限修改该作品的价格");
        }
        
        artworkService.updateArtworkPrice(artworkId, price);
        
        return Result.success(null, "价格更新成功");
    }

    /**
     * 获取已关注用户的作品列表
     * @param authentication 认证信息
     * @param page 页码
     * @param size 每页数量
     * @return 关注用户的作品列表
     */
    @ApiOperation(value = "获取已关注用户的作品列表", notes = "获取当前用户关注的所有用户的作品列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/following")
    public Result<Page<ArtworkDTO>> getFollowingUserArtworks(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "页码", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @PositiveOrZero(message = "页码必须大于或等于0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") int size) {
        log.debug("获取已关注用户的作品列表: 页码={}, 大小={}", page, size);
        
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 获取已关注的用户ID列表
        List<Long> followingIds = followService.getFollowingList(user.getId())
                                             .stream()
                                             .map(FollowDTO::getFollowedId)
                                             .collect(Collectors.toList());
        
        if (followingIds.isEmpty()) {
            // 如果没有关注任何用户，返回空页面
            return Result.success(Page.empty());
        }
        
        // 获取所有已关注用户的作品
        Page<Artwork> artworkPage = artworkService.getArtworksByCreatorIds(followingIds, pageRequest);
        
        // 转换为DTO
        Page<ArtworkDTO> dtoPage = convertToArtworkDTOPage(artworkPage);
        
        return Result.success(dtoPage);
    }

    /**
     * 根据用户ID获取作品列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @param enabled 是否启用（可选）
     * @return 作品列表
     */
    @ApiOperation(value = "获取指定用户的作品列表", notes = "根据用户ID获取作品分页列表，支持按启用状态筛选")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<Page<ArtworkDTO>> getUserArtworks(
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId,
            @ApiParam(value = "是否启用（可选）") @RequestParam(required = false) Boolean enabled,
            @ApiParam(value = "页码", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @PositiveOrZero(message = "页码必须大于或等于0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") int size) {
        log.debug("获取用户ID={}的作品列表: 页码={}, 大小={}, 启用状态={}", userId, page, size, enabled);
        
        // 验证用户是否存在
        userService.getUserById(userId);
                
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<Artwork> artworks;
        if (enabled != null) {
            artworks = artworkService.getUserArtworksByEnabled(userId, enabled, pageRequest);
        } else {
            artworks = artworkService.getArtworksByUserId(userId, pageRequest);
        }
        
        // 转换为DTO
        Page<ArtworkDTO> dtoPage = convertToArtworkDTOPage(artworks);
        
        return Result.success(dtoPage);
    }

    /**
     * 作者更新作品信息
     * @param artworkId 作品ID
     * @param updateDTO 更新的作品信息
     * @param user 当前登录用户
     * @return 更新结果
     */
    @ApiOperation(value = "作者更新作品", notes = "作者更新自己的作品信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 403, message = "没有权限更新该作品"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PutMapping("/{artworkId}")
    public Result<Void> updateArtwork(
            @ApiParam(value = "作品ID", required = true) 
            @PathVariable @Positive(message = "作品ID必须为正数") Long artworkId,
            @ApiParam(value = "更新的作品信息", required = true) @Valid @RequestBody ArtworkUpdateDTO updateDTO,
            @ApiParam(hidden = true) @AuthenticationPrincipal User user) {
        // 检查用户是否为空，如果为空则尝试通过service获取当前用户
        if (user == null) {
            user = userService.getCurrentUser();
            if (user == null) {
                log.error("用户未登录或会话已过期");
                return Result.unauthorized("用户未登录或会话已过期，请重新登录");
            }
        }
        
        log.info("用户 {} 正在更新作品 {}: {}", user.getUsername(), artworkId, updateDTO);
        
        try {
            // 使用新的userUpdateArtwork方法，确保用户权限检查和更新操作
            artworkService.userUpdateArtwork(artworkId, updateDTO, user);
            log.info("用户 {} 成功更新作品 {}", user.getUsername(), artworkId);
            return Result.success(null, "作品更新成功");
        } catch (IllegalArgumentException e) {
            log.warn("用户 {} 更新作品 {} 失败: {}", user.getUsername(), artworkId, e.getMessage());
            // 根据错误信息返回适当的状态码
            if (e.getMessage().contains("不存在")) {
                return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
            } else if (e.getMessage().contains("没有权限")) {
                return Result.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
            } else {
                return Result.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            }
        }
    }
    
    /**
     * 将Artwork转换为ArtworkDTO
     * @param artwork 作品实体
     * @return 作品DTO
     */
    private ArtworkDTO convertToArtworkDTO(Artwork artwork) {
        ArtworkDTO dto = new ArtworkDTO();
        BeanUtils.copyProperties(artwork, dto, "creator", "tags");
        
        // 设置创建者信息
        if (artwork.getCreator() != null) {
            dto.setCreatorId(artwork.getCreator().getId());
            dto.setCreatorName(artwork.getCreator().getUsername());
            dto.setCreatorAvatar(artwork.getCreator().getAvatar());
        }
        
        // 转换标签
        if (artwork.getTags() != null) {
            Set<TagDTO> tagDTOs = artwork.getTags().stream()
                .map(tag -> {
                    TagDTO tagDTO = new TagDTO();
                    tagDTO.setId(tag.getId());
                    tagDTO.setTagName(tag.getName());
                    return tagDTO;
                })
                .collect(Collectors.toSet());
            dto.setTags(tagDTOs);
        }
        
        return dto;
    }
    
    /**
     * 将Artwork页面转换为ArtworkDTO页面
     * @param artworkPage 作品页面
     * @return 作品DTO页面
     */
    private Page<ArtworkDTO> convertToArtworkDTOPage(Page<Artwork> artworkPage) {
        return artworkPage.map(this::convertToArtworkDTO);
    }
}