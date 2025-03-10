package com.thfh.repository;

import com.thfh.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Page<PostComment> findByPostIdOrderByCreateTimeDesc(Long postId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE PostComment pc SET pc.likeCount = pc.likeCount + :delta WHERE pc.id = :commentId")
    void updateLikeCount(Long commentId, int delta);
    
    long countByPostId(Long postId);
}