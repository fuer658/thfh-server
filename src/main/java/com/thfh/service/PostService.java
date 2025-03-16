package com.thfh.service;

import com.thfh.model.*;
import com.thfh.repository.*;
import com.thfh.dto.FollowDTO;
import com.thfh.dto.PostDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private PostTagService postTagService;
    
    /**
     * 发布动态
     */
    @Transactional
    public Post createPost(Post post) {
        User currentUser = userService.getCurrentUser();
        post.setUserId(currentUser.getId());
        Post savedPost = postRepository.save(post);
        
        // 处理标签
        if (post.getTagIds() != null && !post.getTagIds().isEmpty()) {
            for (Long tagId : post.getTagIds()) {
                PostTag tag = postTagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + tagId));
                savedPost.getTags().add(tag);
            }
            savedPost = postRepository.save(savedPost);
        }
        
        return savedPost;
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
        adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("非管理员账号，无权操作"));
        
        // 验证目标用户是否存在
        User targetUser = userService.getUserById(userId);
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }
        
        // 设置动态的用户ID为目标用户ID
        post.setUserId(userId);
        Post savedPost = postRepository.save(post);
        
        // 处理标签（管理员可以直接添加标签，无需权限检查）
        if (post.getTagIds() != null && !post.getTagIds().isEmpty()) {
            for (Long tagId : post.getTagIds()) {
                PostTag tag = postTagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + tagId));
                savedPost.getTags().add(tag);
            }
            savedPost = postRepository.save(savedPost);
        }
        
        return savedPost;
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
        // 确认动态存在
        getPost(postId);
        
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
        // 确认动态存在
        getPost(postId);
        
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
    public PostComment commentPost(Long postId, String content, Long parentId) {
        User currentUser = userService.getCurrentUser();
        // 确认动态存在
        Post post = getPost(postId);
        
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content);
        
        // 设置评论层级
        if (parentId != null) {
            PostComment parentComment = postCommentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("父评论不存在"));
            
            // 检查父评论是否属于同一个动态
            if (!parentComment.getPostId().equals(postId)) {
                throw new IllegalArgumentException("父评论不属于该动态");
            }
            
            // 设置父评论ID和层级
            comment.setParentId(parentId);
            comment.setLevel(parentComment.getLevel() + 1);
            
            // 检查是否超过最大层级（这里限制最多3层）
            if (comment.getLevel() > 3) {
                throw new IllegalArgumentException("评论层级超过限制");
            }
        }
        
        PostComment savedComment = postCommentRepository.save(comment);
        postRepository.updateCommentCount(postId, 1);
        
        return savedComment;
    }
    
    /**
     * 获取评论树结构
     */
    public Page<PostComment> getPostCommentTree(Long postId, Pageable pageable) {
        // 获取一级评论
        Page<PostComment> rootComments = postCommentRepository
                .findByPostIdAndParentIdIsNullOrderByCreateTimeDesc(postId, pageable);
        
        // 获取每个一级评论的子评论
        rootComments.getContent().forEach(comment -> {
            List<PostComment> children = postCommentRepository
                    .findByParentIdOrderByCreateTimeAsc(comment.getId());
            comment.setChildren(children);
        });
        
        return rootComments;
    }
    
    /**
     * 获取评论列表（扁平结构，按层级排序）
     */
    public Page<PostComment> getPostComments(Long postId, Pageable pageable) {
        return postCommentRepository.findByPostIdOrderByLevelAscCreateTimeDesc(postId, pageable);
    }
    
    /**
     * 转发动态
     */
    @Transactional
    public void sharePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        // 确认动态存在
        getPost(postId);
        
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

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        BeanUtils.copyProperties(post, dto);
        if (post.getUser() != null) {
            dto.setUserName(post.getUser().getUsername());
            dto.setUserRealName(post.getUser().getRealName());
            dto.setUserAvatar(post.getUser().getAvatar());
        }
        return dto;
    }

    public Page<PostDTO> getAllPosts(String title, String userName, Pageable pageable) {
        Specification<Post> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (title != null && !title.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
            }
            if (userName != null && !userName.isEmpty()) {
                Join<Post, User> userJoin = root.join("user");
                predicate = cb.and(predicate, 
                    cb.or(
                        cb.like(userJoin.get("username"), "%" + userName + "%"),
                        cb.like(userJoin.get("realName"), "%" + userName + "%")
                    )
                );
            }
            return predicate;
        };

        Page<Post> posts = postRepository.findAll(spec, pageable);
        List<PostDTO> dtoList = posts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, posts.getTotalElements());
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
        adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("非管理员账号，无权操作"));
        
        // 检查动态是否存在
        Post post = getPost(postId);
        if (post == null) {
            throw new IllegalArgumentException("动态不存在");
        }
        
        // 管理员可以删除任何动态
        postRepository.deleteById(postId);
    }

    /**
     * 为动态添加标签
     * @param postId 动态ID
     * @param tagId 标签ID
     * @return 更新后的动态
     */
    @Transactional
    public Post addTag(Long postId, Long tagId) {
        Post post = getPost(postId);
        User currentUser = userService.getCurrentUser();
        
        // 检查权限（如果是管理员则允许操作）
        boolean isAdmin = adminRepository.findByUsername(currentUser.getUsername()).isPresent();
        if (!isAdmin && !post.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("您没有权限为该动态添加标签");
        }
        
        // 获取标签
        PostTag tag = postTagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
        
        // 添加标签到动态
        post.getTags().add(tag);
        return postRepository.save(post);
    }
    
    /**
     * 从动态中移除标签
     * @param postId 动态ID
     * @param tagId 标签ID
     * @return 更新后的动态
     */
    @Transactional
    public Post removeTag(Long postId, Long tagId) {
        Post post = getPost(postId);
        User currentUser = userService.getCurrentUser();
        
        // 检查权限
        if (!post.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("您没有权限从该动态移除标签");
        }
        
        // 移除标签
        post.setTags(post.getTags().stream()
                .filter(tag -> !tag.getId().equals(tagId))
                .collect(Collectors.toSet()));
        
        return postRepository.save(post);
    }
    
    /**
     * 获取动态的所有标签
     * @param postId 动态ID
     * @return 标签集合
     */
    public Set<PostTag> getPostTags(Long postId) {
        Post post = getPost(postId);
        return post.getTags();
    }
    
    /**
     * 根据标签查找动态
     * @param tagId 标签ID
     * @param pageable 分页参数
     * @return 包含指定标签的动态列表
     */
    public Page<Post> findPostsByTag(Long tagId, Pageable pageable) {
        return postRepository.findByTagsId(tagId, pageable);
    }
}