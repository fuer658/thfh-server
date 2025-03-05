package com.thfh.controller;

<<<<<<< HEAD
import com.thfh.common.Result;
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
            @RequestParam BigDecimal score,
            @AuthenticationPrincipal User user) {
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        artworkScoreService.scoreArtwork(artworkId, user.getId(), score, user, artwork);
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
     * 获取当前用户的作品列表
     * @param user 当前登录用户
     * @param type 作品类型（可选）
     * @param enabled 是否启用（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 作品分页列表
     */
    @GetMapping("/my")
    public ResponseEntity<Page<Artwork>> getMyArtworks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) ArtworkType type,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

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

        return ResponseEntity.ok(artworks);
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
=======
import com.thfh.common.R;
import com.thfh.model.Artwork;
import com.thfh.repository.ArtworkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/works")
public class ArtworkController {

    @Resource
    private ArtworkRepository artworkRepository;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int pageNum,
                 @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<Artwork> page = artworkRepository.findAll(
                PageRequest.of(pageNum - 1, pageSize)
            );
            return R.ok().data("total", page.getTotalElements())
                    .data("records", page.getContent());
        } catch (Exception e) {
            return R.error("获取作品列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return artworkRepository.findById(id)
                .map(artwork -> R.ok().data("artwork", artwork))
                .orElse(R.error("作品不存在"));
    }

    @PostMapping
    public R save(@RequestBody Artwork artwork) {
        try {
            Artwork saved = artworkRepository.save(artwork);
            return R.ok().data("artwork", saved);
        } catch (Exception e) {
            return R.error("保存作品失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody Artwork artwork) {
        try {
            if (!artworkRepository.existsById(id)) {
                return R.error("作品不存在");
            }
            artwork.setId(id);
            Artwork updated = artworkRepository.save(artwork);
            return R.ok().data("artwork", updated);
        } catch (Exception e) {
            return R.error("更新作品失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        try {
            if (!artworkRepository.existsById(id)) {
                return R.error("作品不存在");
            }
            artworkRepository.deleteById(id);
            return R.ok();
        } catch (Exception e) {
            return R.error("删除作品失败: " + e.getMessage());
        }
>>>>>>> 6472c74f10f442d46f9f829f2fc3f09f89d23212
    }
}