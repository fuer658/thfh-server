package com.thfh.service;

import com.thfh.model.*;
import com.thfh.repository.PostRepository;
import com.thfh.repository.PostCommentRepository;
import com.thfh.repository.PostLikeRepository;
import com.thfh.repository.PostShareRepository;
import com.thfh.repository.AdminRepository;
import com.thfh.dto.FollowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostShareRepository postShareRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private AdminRepository adminRepository;

    
    /**
     * 发布动态
     */
    @Transactional
    public Post createPost(Post post) {
        User currentUser = userService.getCurrentUser();
        post.setUserId(currentUser.getId());
        return postRepository.save(post);
    }
    
    /**
     * 管理员发布动态
     * @param post 动态内容
     * @param userId 用户ID，表示以哪个用户的身份发布
     * @return 创建的动态
     */
    @Transactional
    public Post createPostByAdmin(Post post, Long userId) {
        // 获取当前认证的用户名
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("未登录");
        }
        
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("非管理员账号，无权操作"));
        
        // 验证目标用户是否存在
        User targetUser = userService.getUserById(userId);
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }
        
        // 设置动态的用户ID为目标用户ID
        post.setUserId(userId);
        return postRepository.save(post);
    }
    
    /**
     * 获取动态详情
     */
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("动态不存在"));
    }
    
    /**
     * 获取用户动态列表
     */
    public Page<Post> getUserPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 点赞动态
     */
    @Transactional
    public void likePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        if (postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), postId)) {
            throw new IllegalStateException("您已经点赞过该动态");
        }
        
        PostLike postLike = new PostLike();
        postLike.setUserId(currentUser.getId());
        postLike.setPostId(postId);
        postLikeRepository.save(postLike);
        
        postRepository.updateLikeCount(postId, 1);
    }
    
    /**
     * 取消点赞
     */
    @Transactional
    public void unlikePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        if (!postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), postId)) {
            throw new IllegalStateException("您还没有点赞该动态");
        }
        
        postLikeRepository.deleteByUserIdAndPostId(currentUser.getId(), postId);
        postRepository.updateLikeCount(postId, -1);
    }
    
    /**
     * 评论动态
     */
    @Transactional
    public PostComment commentPost(Long postId, String content) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content);
        
        PostComment savedComment = postCommentRepository.save(comment);
        postRepository.updateCommentCount(postId, 1);
        
        return savedComment;
    }
    
    /**
     * 获取动态评论列表
     */
    public Page<PostComment> getPostComments(Long postId, Pageable pageable) {
        return postCommentRepository.findByPostIdOrderByCreateTimeDesc(postId, pageable);
    }
    
    /**
     * 转发动态
     */
    @Transactional
    public void sharePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        if (postShareRepository.existsByUserIdAndPostId(currentUser.getId(), postId)) {
            throw new IllegalStateException("您已经转发过该动态");
        }
        
        PostShare postShare = new PostShare();
        postShare.setUserId(currentUser.getId());
        postShare.setPostId(postId);
        postShareRepository.save(postShare);
        
        postRepository.updateShareCount(postId, 1);
    }
    
    /**
     * 删除动态
     */
    @Transactional
    public void deletePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        if (!post.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("您没有权限删除该动态");
        }
        
        postRepository.deleteById(postId);
    }
    
    /**
     * 更新动态
     */
    @Transactional
    public Post updatePost(Long postId, Post updatedPost) {
        User currentUser = userService.getCurrentUser();
        Post post = getPost(postId);
        
        if (!post.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("您没有权限更新该动态");
        }
        
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setImageUrls(updatedPost.getImageUrls());
        
        return postRepository.save(post);
    }

    /**
     * 检查用户是否已点赞动态
     */
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * 获取所有动态列表
     */
    public Page<Post> getAllPosts(String title, String userName, Pageable pageable) {
        // 如果两个参数都为空，则返回所有动态
        if ((title == null || title.trim().isEmpty()) && (userName == null || userName.trim().isEmpty())) {
            return postRepository.findAll(pageable);
        }
        
        // 确保参数不为null，避免SQL错误
        String titleParam = (title == null) ? "" : title.trim();
        String userNameParam = (userName == null) ? "" : userName.trim();
        
        return postRepository.findByTitleContainingAndUserRealNameContaining(
            titleParam, userNameParam, pageable
        );
    }

    /**
     * 获取关注用户的动态列表
     */
    public Page<Post> getFollowingPosts(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        List<Long> followingIds = followService.getFollowingList(currentUser.getId())
                .stream()
                .map(FollowDTO::getFollowedId)
                .collect(Collectors.toList());
        return postRepository.findByUserIdIn(followingIds, pageable);
    }
    
    /**
     * 管理员删除动态
     * @param postId 动态ID
     */
    @Transactional
    public void deletePostByAdmin(Long postId) {
        // 获取当前认证的用户名
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("未登录");
        }
        
        String username = authentication.getName();
        
        // 验证当前用户是否为管理员
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("非管理员账号，无权操作"));
        
        // 检查动态是否存在
        Post post = getPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("动态不存在");
        }
        
        // 管理员可以删除任何动态
        postRepository.deleteById(postId);
    }
}