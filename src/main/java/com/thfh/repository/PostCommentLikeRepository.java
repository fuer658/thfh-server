package com.thfh.repository;

import com.thfh.model.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
    long countByCommentId(Long commentId);
    
    /**
     * 删除指定评论的所有点赞记录
     * @param commentId 评论ID
     */
    @Modifying
    void deleteByCommentId(Long commentId);
}
