package com.thfh.repository;

import com.thfh.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户关注关系数据访问接口
 * 提供对用户关注关系(Follow)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    /**
     * 查询是否已关注
     * 
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return 包含关注关系的Optional对象，如果不存在则为空
     */
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    /**
     * 查询用户的关注列表
     * 
     * @param followerId 关注者ID
     * @return 用户关注的用户列表
     */
    List<Follow> findByFollowerId(Long followerId);
    
    /**
     * 查询用户的粉丝列表
     * 
     * @param followedId 被关注者ID
     * @return 关注该用户的用户列表
     */
    List<Follow> findByFollowedId(Long followedId);
    
    /**
     * 统计用户的关注数
     * 
     * @param followerId 关注者ID
     * @return 用户关注的数量
     */
    long countByFollowerId(Long followerId);
    
    /**
     * 统计用户的粉丝数
     * 
     * @param followedId 被关注者ID
     * @return 用户的粉丝数量
     */
    long countByFollowedId(Long followedId);
    
    /**
     * 删除关注关系
     * 
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     */
    void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
}