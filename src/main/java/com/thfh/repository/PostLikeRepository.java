package com.thfh.repository;

import com.thfh.model.PostLike;
import com.thfh.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    /**
     * 获取用户点赞的动态列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的动态列表
     */
    @Query("SELECT pl.post FROM PostLike pl WHERE pl.userId = ?1")
    Page<Post> findPostsByUserId(Long userId, Pageable pageable);
}