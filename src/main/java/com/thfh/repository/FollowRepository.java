package com.thfh.repository;

import com.thfh.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 查询是否已关注
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    // 查询用户的关注列表
    List<Follow> findByFollowerId(Long followerId);
    
    // 查询用户的粉丝列表
    List<Follow> findByFollowedId(Long followedId);
    
    // 统计用户的关注数
    long countByFollowerId(Long followerId);
    
    // 统计用户的粉丝数
    long countByFollowedId(Long followedId);
    
    // 删除关注关系
    void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
}