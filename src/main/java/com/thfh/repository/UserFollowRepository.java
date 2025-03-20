package com.thfh.repository;

import com.thfh.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Page<UserFollow> findByFollowerId(Long followerId, Pageable pageable);
    Page<UserFollow> findByFollowingId(Long followingId, Pageable pageable);
    long countByFollowerId(Long followerId);
    long countByFollowingId(Long followingId);
} 