package com.thfh.repository;

import com.thfh.model.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    /**
     * 根据帖子ID删除所有分享记录
     * @param postId 帖子ID
     */
    @Modifying
    void deleteByPostId(Long postId);
}