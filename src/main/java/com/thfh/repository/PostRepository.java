package com.thfh.repository;

import com.thfh.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    Page<Post> findByUserIdIn(List<Long> userIds, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + :delta WHERE p.id = :postId")
    void updateLikeCount(Long postId, int delta);
    
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + :delta WHERE p.id = :postId")
    void updateCommentCount(Long postId, int delta);
    
    @Modifying
    @Query("UPDATE Post p SET p.shareCount = p.shareCount + :delta WHERE p.id = :postId")
    void updateShareCount(Long postId, int delta);
}