package com.thfh.repository;

import com.thfh.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    // 查询一级评论（parentId为null的评论）
    Page<PostComment> findByPostIdAndParentIdIsNullOrderByCreateTimeDesc(Long postId, Pageable pageable);
    
    // 查询子评论
    List<PostComment> findByParentIdOrderByCreateTimeAsc(Long parentId);
    
    // 查询所有评论（按层级排序）
    Page<PostComment> findByPostIdOrderByLevelAscCreateTimeDesc(Long postId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE PostComment pc SET pc.likeCount = pc.likeCount + :delta WHERE pc.id = :commentId")
    void updateLikeCount(Long commentId, int delta);
    
    long countByPostId(Long postId);
    
    // 查询指定评论的所有子评论数量
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.parentId = :commentId")
    long countByParentId(Long commentId);
    
    // 查询评论的最大层级
    @Query("SELECT MAX(pc.level) FROM PostComment pc WHERE pc.postId = :postId")
    Integer findMaxLevelByPostId(Long postId);
}