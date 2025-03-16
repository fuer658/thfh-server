package com.thfh.repository;

import com.thfh.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 帖子标签数据访问接口
 * 提供对帖子标签(PostTag)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    
    /**
     * 根据标签名称查找标签
     * 
     * @param name 标签名称
     * @return 匹配的标签对象，如果不存在则返回null
     */
    Optional<PostTag> findByName(String name);
} 