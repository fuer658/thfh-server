package com.thfh.repository;

import com.thfh.model.CourseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 课程标签数据访问接口
 * 提供对课程标签(CourseTag)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface CourseTagRepository extends JpaRepository<CourseTag, Long> {
    
    /**
     * 根据标签名称查找标签
     * 
     * @param name 标签名称
     * @return 匹配的标签对象，如果不存在则返回null
     */
    Optional<CourseTag> findByName(String name);
}
