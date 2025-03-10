package com.thfh.repository;

import com.thfh.model.ArtworkTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 艺术作品标签数据访问接口
 * 提供对艺术作品标签(ArtworkTag)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface ArtworkTagRepository extends JpaRepository<ArtworkTag, Long> {
    
    /**
     * 根据标签名称查找标签
     * 
     * @param name 标签名称
     * @return 匹配的标签对象，如果不存在则返回null
     */
    ArtworkTag findByName(String name);
}