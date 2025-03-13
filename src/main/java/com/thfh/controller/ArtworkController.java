package com.thfh.controller;

import com.thfh.common.CustomPage;
import com.thfh.common.Result;
import com.thfh.dto.ArtworkScoreDTO;
import com.thfh.dto.TagDTO;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.service.ArtworkService;
import com.thfh.service.ArtworkScoreService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtworkScoreService artworkScoreService;

    /**
     * 发布作品
     * @param artwork 作品信息
     * @param user 当前登录用户
     * @return 创建的作品
     */
    @PostMapping
    public Result<Void> createArtwork(
            @Valid @RequestBody Artwork artwork,
            @AuthenticationPrincipal User user) {
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
    @PostMapping("/{artworkId}/score")
    public Result<Void> scoreArtwork(
            @PathVariable Long artworkId,
            @Valid @RequestBody ArtworkScoreDTO scoreDTO,
            Authentication authentication) {
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
    @GetMapping("/{artworkId}/score")
    public Result<Map<String, Object>> getArtworkScore(@PathVariable Long artworkId) {
        Map<String, Object> scoreInfo = new HashMap<>();
        scoreInfo.put("averageScore", artworkScoreService.getArtworkAverageScore(artworkId));
        scoreInfo.put("scoreCount", artworkScoreService.getArtworkScoreCount(artworkId));
        return Result.success(scoreInfo);
    }

    /**
     * 获取作品列表
     * @param page 页码
     * @param size 每页大小
     * @return 作品分页列表
     */
    @GetMapping
    public Result<CustomPage<Artwork>> getArtworks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Artwork> artworkPage = artworkService.getAllArtworks(pageRequest);
        return Result.success(new CustomPage<>(artworkPage));
    }

    /**
     * 获取当前用户的作品列表
     * @param user 当前登录用户
     * @param type 作品类型（可选）
     * @param enabled 是否启用（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 作品分页列表
     */
    @GetMapping("/my")
    public Result<CustomPage<Artwork>> getMyArtworks(
            Authentication authentication,
            @RequestParam(required = false) ArtworkType type,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

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

        return Result.success(new CustomPage<>(artworks));
    }

    /**
     * 删除作品
     * @param artworkId 作品ID
     * @param user 当前登录用户
     * @return 删除结果
     */
    @DeleteMapping("/{artworkId}")
    public Result<Void> deleteArtwork(
            @PathVariable Long artworkId,
            Authentication authentication) {
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
     * 添加新标签
     * @param tagName 标签名称
     * @return 操作结果
     */
    @PostMapping("/tags")
    public Result<Void> addTag(
            @Valid @RequestBody TagDTO tagDTO,
            Authentication authentication) {
        artworkService.addTag(tagDTO.getTagName());
        return Result.success(null);
    }

    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 操作结果
     */
    @DeleteMapping("/tags/{tagId}")
    public Result<Void> removeTag(
            @PathVariable Long tagId,
            Authentication authentication) {
        artworkService.removeTag(tagId);
        return Result.success(null);
    }

    /**
     * 修改作品推荐状态
     * @param artworkId 作品ID
     * @param recommended 是否推荐（0-不推荐，1-推荐）
     * @return 更新结果
     */
    @PutMapping("/{artworkId}/recommend")
    public Result<Void> updateArtworkRecommendation(
            @PathVariable Long artworkId,
            @RequestParam(defaultValue = "0") Integer recommended) {
        artworkService.updateArtworkRecommendation(artworkId, recommended == 1);
        return Result.success(null);
    }

    /**
     * 修改商业作品价格
     * @param artworkId 作品ID
     * @param price 新价格
     * @return 更新结果
     */
    @PutMapping("/{artworkId}/price")
    public Result<Void> updateArtworkPrice(
            @PathVariable Long artworkId,
            @RequestParam BigDecimal price) {
        artworkService.updateArtworkPrice(artworkId, price);
        return Result.success(null);
    }
}