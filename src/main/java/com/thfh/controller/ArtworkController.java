package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ArtworkDTO;
import com.thfh.dto.ArtworkScoreDTO;
import com.thfh.dto.ArtworkUpdateDTO;
import com.thfh.dto.ShoppingCartDTO;
import com.thfh.dto.TagDTO;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.service.AdminService;
import com.thfh.service.ArtworkService;
import com.thfh.service.ArtworkScoreService;
import com.thfh.service.ShoppingCartService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 作品管理控制器
 * 提供作品相关的API接口，包括作品发布、评分、查询、删除和编辑等功能
 */
@Api(tags = "作品管理", description = "作品相关的API接口，包括作品发布、评分、查询、删除和编辑等功能")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtworkScoreService artworkScoreService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ShoppingCartService shoppingCartService;

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
    public Result<Void> createArtwork(
            @ApiParam(value = "作品信息", required = true) @Valid @RequestBody Artwork artwork,
            @ApiParam(hidden = true) @AuthenticationPrincipal User user) {
        artwork.setCreator(user);
        artwork.setCreateTime(LocalDateTime.now());
        artwork.setEnabled(true);
        artworkService.createArtwork(artwork);
        return Result.success(null);
    }

    /**
     * 为作品评分
     * @param artworkId 作品ID
     * @param score 评分（0-100分）
     * @param user 当前登录用户
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
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "评分信息", required = true) @Valid @RequestBody ArtworkScoreDTO scoreDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        artworkScoreService.scoreArtwork(artworkId, user.getId(), scoreDTO.getScore(), user, artwork);
        return Result.success(null);
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
    public Result<Map<String, Object>> getArtworkScore(@ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId) {
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
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "作品标题（可选）") @RequestParam(required = false) String title,
            @ApiParam(value = "学生ID（可选）") @RequestParam(required = false) Long studentId,
            @ApiParam(value = "是否启用（可选）") @RequestParam(required = false) Boolean enabled) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Artwork> artworkPage = artworkService.getArtworks(title, studentId, enabled, pageRequest);
        
        // 转换为DTO
        Page<ArtworkDTO> dtoPage = artworkPage.map(artwork -> {
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
        });
        
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
    public Result<Page<Artwork>> getMyArtworks(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "作品类型（可选）") @RequestParam(required = false) ArtworkType type,
            @ApiParam(value = "是否启用（可选）") @RequestParam(required = false) Boolean enabled,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
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
        
        return Result.success(artworks);
    }

    /**
     * 删除作品
     * @param artworkId 作品ID
     * @param user 当前登录用户
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
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        
        // 验证当前用户是否为作品创建者
        if (!artwork.getCreator().getId().equals(user.getId())) {
            throw new IllegalStateException("您没有权限删除该作品");
        }
        
        artworkService.deleteArtwork(artworkId);
        return Result.success(null);
    }

    /**
     * 管理员删除作品
     * @param artworkId 作品ID
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
    public Result<Void> adminDeleteArtwork(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        if (!adminService.isAdmin(username)) {
            throw new IllegalStateException("您没有管理员权限");
        }
        
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            throw new IllegalArgumentException("作品不存在");
        }
        
        artworkService.deleteArtwork(artworkId);
        return Result.success(null);
    }

    /**
     * 添加新标签
     * @param tagName 标签名称
     * @return 操作结果
     */
    @ApiOperation(value = "添加新标签", notes = "添加新的作品标签")
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加成功"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping("/tags")
    public Result<Void> addTag(
            @ApiParam(value = "标签信息", required = true) @Valid @RequestBody TagDTO tagDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        artworkService.addTag(tagDTO.getTagName());
        return Result.success(null);
    }

    /**
     * 删除标签
     * @param tagId 标签ID
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
            @ApiParam(value = "标签ID", required = true) @PathVariable Long tagId,
            @ApiParam(hidden = true) Authentication authentication) {
        artworkService.removeTag(tagId);
        return Result.success(null);
    }

    /**
     * 管理员编辑作品
     * @param artworkId 作品ID
     * @param updateDTO 更新的作品信息
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
    public Result<Void> adminUpdateArtwork(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "更新的作品信息", required = true) @Valid @RequestBody ArtworkUpdateDTO updateDTO,
            @ApiParam(hidden = true) Authentication authentication) {
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        if (!adminService.isAdmin(username)) {
            throw new IllegalStateException("您没有管理员权限");
        }
        
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            throw new IllegalArgumentException("作品不存在");
        }
        
        artworkService.updateArtwork(artworkId, updateDTO);
        return Result.success(null);
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
    public Result<Void> updateArtworkRecommendation(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "是否推荐（0-不推荐，1-推荐）", defaultValue = "0") @RequestParam(defaultValue = "0") Integer recommended) {
        artworkService.updateArtworkRecommendation(artworkId, recommended == 1);
        return Result.success(null);
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
    public Result<ArtworkDTO> getArtworkDetail(@ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId) {
        // 增加浏览量
        artworkService.incrementViewCount(artworkId);
        
        // 获取作品信息
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        
        // 转换为DTO
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
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "新价格", required = true) @RequestParam BigDecimal price) {
        artworkService.updateArtworkPrice(artworkId, price);
        return Result.success(null);
    }

    /**
     * 将作品添加到购物车
     * 
     * @param artworkId 作品ID
     * @param quantity 数量
     * @param authentication 认证信息
     * @return 更新后的购物车
     */
    @ApiOperation(value = "添加到购物车", notes = "将作品添加到购物车中")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/{artworkId}/addToCart")
    public Result<ShoppingCartDTO> addToCart(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "数量", defaultValue = "1") @RequestParam(defaultValue = "1") Integer quantity,
            @ApiParam(hidden = true) Authentication authentication) {
        // 检查作品是否存在
        if (!artworkService.getArtworkById(artworkId).isPresent()) {
            return Result.error("作品不存在");
        }
        
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.addToCart(user.getId(), artworkId, quantity);
        return Result.success(cart, "已添加到购物车");
    }
}