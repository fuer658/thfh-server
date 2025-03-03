package com.thfh.controller;

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
    }
}