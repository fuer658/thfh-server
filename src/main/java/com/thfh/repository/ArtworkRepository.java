package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // 基本的CRUD操作由JpaRepository提供

    // 根据创建者ID查询作品，支持分页
    Page<Artwork> findByCreatorId(Long creatorId, Pageable pageable);

    // 根据创建者ID和作品类型查询作品，支持分页
    Page<Artwork> findByCreatorIdAndType(Long creatorId, ArtworkType type, Pageable pageable);

    // 根据创建者ID和启用状态查询作品，支持分页
    Page<Artwork> findByCreatorIdAndEnabled(Long creatorId, Boolean enabled, Pageable pageable);

    // 根据创建者ID、作品类型和启用状态查询作品，支持分页
    Page<Artwork> findByCreatorIdAndTypeAndEnabled(Long creatorId, ArtworkType type, Boolean enabled, Pageable pageable);
}