package com.thfh.repository;

import com.thfh.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 评论点赞数据访问接口
 */
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    
    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否存在点赞记录
     */
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    
    /**
     * 根据评论ID和用户ID查询点赞记录
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    CommentLike findByCommentIdAndUserId(Long commentId, Long userId);
    
    /**
     * 统计评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.id = :commentId")
    int countByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 删除评论的所有点赞记录
     * @param commentId 评论ID
     */
    void deleteByCommentId(Long commentId);
} 