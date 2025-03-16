package com.thfh.repository;

import com.thfh.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findByUserId(Long userId, Pageable pageable);
    
    Page<Post> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    Page<Post> findByUserIdIn(List<Long> userIds, Pageable pageable);
    
    @Query("SELECT p FROM Post p JOIN User u ON p.userId = u.id WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR :title = '') AND " +
           "(LOWER(u.realName) LIKE LOWER(CONCAT('%', :userName, '%')) OR :userName = '')")
    Page<Post> findByTitleContainingAndUserRealNameContaining(String title, String userName, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + :delta WHERE p.id = :postId")
    void updateLikeCount(Long postId, int delta);
    
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + :delta WHERE p.id = :postId")
    void updateCommentCount(Long postId, int delta);
    
    @Modifying
    @Query("UPDATE Post p SET p.shareCount = p.shareCount + :delta WHERE p.id = :postId")
    void updateShareCount(Long postId, int delta);

    /**
     * 根据标签ID查找动态
     * @param tagId 标签ID
     * @param pageable 分页参数
     * @return 包含指定标签的动态列表
     */
    Page<Post> findByTagsId(Long tagId, Pageable pageable);
}