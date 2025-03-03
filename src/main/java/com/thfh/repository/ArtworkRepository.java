package com.thfh.repository;

import com.thfh.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // 基本的CRUD操作由JpaRepository提供
}