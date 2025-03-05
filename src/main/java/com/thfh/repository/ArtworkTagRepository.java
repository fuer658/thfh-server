package com.thfh.repository;

import com.thfh.model.ArtworkTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkTagRepository extends JpaRepository<ArtworkTag, Long> {
    // 根据标签名称查找标签
    ArtworkTag findByName(String name);
}