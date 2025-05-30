package com.thfh.repository;

import com.thfh.model.PostLike;
import com.thfh.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    /**
     * 根据帖子ID删除所有点赞记录
     * @param postId 帖子ID
     */
    @Modifying
    void deleteByPostId(Long postId);
    
    /**
     * 获取用户点赞的动态列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的动态列表
     */
    @Query("SELECT pl.post FROM PostLike pl WHERE pl.userId = ?1")
    Page<Post> findPostsByUserId(Long userId, Pageable pageable);
    
    /**
     * 获取用户点赞的动态ID列表
     * @param userId 用户ID
     * @return 用户点赞过的动态ID列表
     */
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.userId = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}