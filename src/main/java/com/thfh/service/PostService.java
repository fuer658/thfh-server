package com.thfh.service;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashSet;
import java.util.Arrays;

import com.thfh.model.*;
import com.thfh.repository.*;
import com.thfh.dto.FollowDTO;
import com.thfh.dto.PostCommentDTO;
import com.thfh.dto.PostDTO;
import com.thfh.repository.PostCommentLikeRepository;
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

import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import com.thfh.dto.PostReportRequest;
import com.thfh.repository.PostReportRepository;
import com.thfh.model.PostReport;
import java.time.LocalDateTime;
import javax.persistence.criteria.JoinType;

@Slf4j

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
    private PostCommentLikeRepository postCommentLikeRepository;

    @Autowired
    private PostReportRepository postReportRepository;

    @Autowired
    private PostTagService postTagService;

    /**
     * 验证排序字段是否有效，如果无效则回退到默认排序字段"createTime"
     * @param sortBy 排序字段
     * @return 验证后的排序字段
     */
    public String validateSortField(String sortBy) {
        // 定义允许的排序字段
        Set<String> validSortFields = new HashSet<>(Arrays.asList(
            "createTime", "updateTime", "likeCount", "commentCount", "shareCount", "viewCount", "title"
        ));
        
        // 如果提供的排序字段有效，则使用它，否则回退到默认排序字段
        return validSortFields.contains(sortBy) ? sortBy : "createTime";
    }

    /**
     * 发布动态
     */
    @Transactional
    public Post createPost(Post post) {
        User currentUser = userService.getCurrentUser();
        post.setUserId(currentUser.getId());
        
        // 处理标签名称，确保所有标签都被持久化
        if (post.getTagNames() != null && !post.getTagNames().isEmpty()) {
            for (String tagName : post.getTagNames()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    PostTag tag = postTagService.findOrCreateTag(tagName);
                    // 确保标签已持久化并有ID
                    if (tag.getId() == null) {
                        tag = postTagRepository.save(tag);
                    }
                    post.getTags().add(tag);
                }
            }
        }
        
        // 处理标签ID
        if (post.getTagIds() != null && !post.getTagIds().isEmpty()) {
            for (Long tagId : post.getTagIds()) {
                PostTag tag = postTagRepository.findById(tagId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PARAMETER_ERROR, "标签不存在: " + tagId));
                post.getTags().add(tag);
            }
        }
        
        // 保存帖子（此时所有标签都已持久化）
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
        adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("非管理员账号，无权操作"));

        // 验证目标用户是否存在
        User targetUser = userService.getUserById(userId);
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        // 设置动态的用户ID为目标用户ID
        post.setUserId(userId);
        
        // 处理标签名称，确保所有标签都被持久化
        if (post.getTagNames() != null && !post.getTagNames().isEmpty()) {
            for (String tagName : post.getTagNames()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    PostTag tag = postTagService.findOrCreateTag(tagName);
                    // 确保标签已持久化并有ID
                    if (tag.getId() == null) {
                        tag = postTagRepository.save(tag);
                    }
                    post.getTags().add(tag);
                }
            }
        }
        
        // 处理标签ID
        if (post.getTagIds() != null && !post.getTagIds().isEmpty()) {
            for (Long tagId : post.getTagIds()) {
                PostTag tag = postTagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + tagId));
                post.getTags().add(tag);
            }
        }
        
        // 保存帖子（此时所有标签都已持久化）
        return postRepository.save(post);
    }

    /**
     * 获取动态详情
     */
    public Post getPost(Long postId) {
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
            return cb.equal(root.get("id"), postId);
        };

        List<Post> posts = postRepository.findAll(spec);
        if (posts.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "动态不存在");
        }
        return posts.get(0);
    }

    /**
     * 获取用户动态列表
     */
    public Page<Post> getUserPosts(Long userId, Pageable pageable) {
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
            return cb.equal(root.get("userId"), userId);
        };

        return postRepository.findAll(spec, pageable);
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
        getPost(postId);

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content);

        // 设置评论层级
        if (parentId != null) {
            PostComment parentComment = postCommentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "父评论不存在"));

            // 检查父评论是否属于同一个动态
            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "父评论不属于该动态");
            }

            comment.setParentId(parentId);
            comment.setLevel(parentComment.getLevel() + 1);
        }

        PostComment savedComment = postCommentRepository.save(comment);
        postRepository.updateCommentCount(postId, 1);

        return savedComment;
    }

    /**
     * 获取评论树结构
     */
    public Page<PostCommentDTO> getPostCommentTree(Long postId, Pageable pageable) {
        try {
            // 获取一级评论
            Page<PostComment> rootComments = postCommentRepository
                    .findByPostIdAndParentIdIsNullOrderByCreateTimeDesc(postId, pageable);

            // 转换为DTO对象
            List<PostCommentDTO> dtoList = new ArrayList<>();
            for (PostComment comment : rootComments.getContent()) {
                PostCommentDTO dto = convertToCommentDTO(comment);

                // 递归获取子评论
                dto.setChildren(getChildComments(comment.getId()));
                dtoList.add(dto);
            }

            return new PageImpl<>(dtoList, pageable, rootComments.getTotalElements());
        } catch (Exception e) {
            System.err.println("获取评论树结构失败: " + e.getMessage());
            e.printStackTrace();
            // 返回空的评论列表
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    /**
     * 递归获取子评论
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    private List<PostCommentDTO> getChildComments(Long parentId) {
        List<PostComment> children = postCommentRepository.findByParentIdOrderByCreateTimeAsc(parentId);
        List<PostCommentDTO> childDtos = new ArrayList<>();

        for (PostComment child : children) {
            PostCommentDTO childDto = convertToCommentDTO(child);
            // 递归获取子评论
            childDto.setChildren(getChildComments(child.getId()));
            childDtos.add(childDto);
        }

        return childDtos;
    }

    /**
     * 获取评论列表（扁平结构，按层级排序）
     */
    public Page<PostCommentDTO> getPostComments(Long postId, Pageable pageable) {
        try {
            Page<PostComment> comments = postCommentRepository.findByPostIdOrderByLevelAscCreateTimeDesc(postId, pageable);

            // 转换为DTO对象
            List<PostCommentDTO> dtoList = new ArrayList<>();
            for (PostComment comment : comments.getContent()) {
                dtoList.add(convertToCommentDTO(comment));
            }

            return new PageImpl<>(dtoList, pageable, comments.getTotalElements());
        } catch (Exception e) {
            System.err.println("获取评论列表失败: " + e.getMessage());
            e.printStackTrace();
            // 返回空的评论列表
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
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

        // 删除动态前先清除所有关联数据
        // 1. 删除所有评论点赞记录
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByLevelAscCreateTimeDesc(postId, Pageable.unpaged()).getContent();
        for (PostComment comment : comments) {
            postCommentLikeRepository.deleteByCommentId(comment.getId());
        }
        
        // 2. 删除所有评论
        postCommentRepository.deleteAll(comments);
        
        // 3. 删除所有点赞记录
        postLikeRepository.deleteByPostId(postId);
        
        // 4. 删除所有分享记录
        postShareRepository.deleteByPostId(postId);
        
        // 5. 最后删除动态本身
        postRepository.deleteById(postId);
        
        log.info("用户 {} 删除了动态 {}", currentUser.getUsername(), postId);
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
        
        // 更新基本内容
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        
        // 更新图片URL
        if (updatedPost.getImageUrls() != null) {
            post.setImageUrls(updatedPost.getImageUrls());
        }
        
        // 如果提供了标签相关信息，则清除旧标签
        boolean needUpdateTags = (updatedPost.getTagIds() != null && !updatedPost.getTagIds().isEmpty()) || 
                          (updatedPost.getTagNames() != null && !updatedPost.getTagNames().isEmpty());
        
        if (needUpdateTags) {
            // 清除旧标签
            post.getTags().clear();
            
            // 处理标签ID
            if (updatedPost.getTagIds() != null && !updatedPost.getTagIds().isEmpty()) {
                for (Long tagId : updatedPost.getTagIds()) {
                    PostTag tag = postTagRepository.findById(tagId)
                            .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + tagId));
                    post.getTags().add(tag);
                }
            }
            
            // 处理标签名称
            if (updatedPost.getTagNames() != null && !updatedPost.getTagNames().isEmpty()) {
                for (String tagName : updatedPost.getTagNames()) {
                    if (tagName != null && !tagName.trim().isEmpty()) {
                        PostTag tag = postTagService.findOrCreateTag(tagName);
                        // 确保标签已持久化并有ID
                        if (tag.getId() == null) {
                            tag = postTagRepository.save(tag);
                        }
                        post.getTags().add(tag);
                    }
                }
            }
        }
        
        // 记录日志
        log.info("用户 {} 更新了动态 {}", currentUser.getUsername(), postId);
        
        // 保存更新后的帖子
        return postRepository.save(post);
    }

    /**
     * 检查用户是否已点赞动态
     */
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * 获取用户点赞的动态列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的动态列表
     */
    public Page<Post> getUserLikedPosts(Long userId, Pageable pageable) {
        return postLikeRepository.findPostsByUserId(userId, pageable);
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        BeanUtils.copyProperties(post, dto);
        if (post.getUser() != null) {
            dto.setUserName(post.getUser().getUsername());
            dto.setUserRealName(post.getUser().getRealName());
            dto.setUserAvatar(post.getUser().getAvatar());
        }
        
        // 复制标签信息
        dto.setTags(post.getTags());
        
        return dto;
    }

    /**
     * 将PostComment实体转换为PostCommentDTO
     */
    private PostCommentDTO convertToCommentDTO(PostComment comment) {
        PostCommentDTO dto = new PostCommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setPostId(comment.getPostId());
        dto.setParentId(comment.getParentId());
        dto.setLevel(comment.getLevel());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());

        // 设置用户信息
        if (comment.getUser() != null) {
            dto.setUserName(comment.getUser().getUsername());
            dto.setUserRealName(comment.getUser().getRealName());
            dto.setUserAvatar(comment.getUser().getAvatar());
        }

        return dto;
    }

    public Page<PostDTO> getAllPosts(String title, String userName, Pageable pageable) {
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
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
                
        if (followingIds.isEmpty()) {
            return Page.empty(pageable);
        }
        
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
            return root.get("userId").in(followingIds);
        };

        return postRepository.findAll(spec, pageable);
    }

    /**
     * 获取关注用户的动态列表（包含用户信息）
     * @param pageable 分页参数
     * @return 动态DTO列表
     */
    public Page<PostDTO> getFollowingPostsWithUserInfo(Pageable pageable) {
        Page<Post> posts = getFollowingPosts(pageable);
        List<PostDTO> dtoList = posts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, posts.getTotalElements());
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

        // 删除动态前先清除所有关联数据
        // 1. 删除所有评论点赞记录
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByLevelAscCreateTimeDesc(postId, Pageable.unpaged()).getContent();
        for (PostComment comment : comments) {
            postCommentLikeRepository.deleteByCommentId(comment.getId());
        }
        
        // 2. 删除所有评论
        postCommentRepository.deleteAll(comments);
        
        // 3. 删除所有点赞记录
        postLikeRepository.deleteByPostId(postId);
        
        // 4. 删除所有分享记录
        postShareRepository.deleteByPostId(postId);
        
        // 5. 最后删除动态本身
        postRepository.deleteById(postId);
        
        log.info("管理员 {} 删除了动态 {}", username, postId);
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

        // 添加标签到动态并立即保存
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
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
            // 通过关联查找包含指定tagId的Posts
            Join<Post, PostTag> tagJoin = root.join("tags");
            return cb.equal(tagJoin.get("id"), tagId);
        };

        return postRepository.findAll(spec, pageable);
    }

    /**
     * 管理员以指定用户身份评论动态
     * @param postId 动态ID
     * @param content 评论内容
     * @param parentId 父评论ID，如果是一级评论则为null
     * @param userId 用户ID，表示以哪个用户的身份发布评论
     * @return 创建的评论
     */
    @Transactional
    public PostComment commentPostByAdmin(Long postId, String content, Long parentId, Long userId) {
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

        // 确认动态存在
        getPost(postId); // 如果动态不存在会抛出异常

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId); // 设置为目标用户ID
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
        }

        PostComment savedComment = postCommentRepository.save(comment);
        postRepository.updateCommentCount(postId, 1);

        return savedComment;
    }

    /**
     * 管理员删除评论
     * @param commentId 评论ID
     */
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
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

        // 检查评论是否存在
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 获取评论所属的动态ID
        Long postId = comment.getPostId();

        // 检查是否有子评论
        long childCount = postCommentRepository.countByParentId(commentId);
        if (childCount > 0) {
            throw new IllegalStateException("该评论有子评论，无法直接删除");
        }

        // 先删除与评论相关的点赞记录
        postCommentLikeRepository.deleteByCommentId(commentId);

        // 删除评论
        postCommentRepository.deleteById(commentId);

        // 获取当前动态的评论数
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("动态不存在"));
        
        // 确保评论计数不会变为负数
        if (post.getCommentCount() > 0) {
            // 更新动态的评论计数
            postRepository.updateCommentCount(postId, -1);
        } else {
            // 如果当前评论计数已经是0或负数，则强制设置为0
            post.setCommentCount(0);
            postRepository.save(post);
        }
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Transactional
    public int likeComment(Long commentId) {
        User currentUser = userService.getCurrentUser();

        // 确认评论存在
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 检查是否已经点赞，如果已点赞则直接返回当前点赞数
        if (postCommentLikeRepository.existsByUserIdAndCommentId(currentUser.getId(), commentId)) {
            return comment.getLikeCount() != null ? comment.getLikeCount() : 0;
        }

        // 创建点赞记录
        PostCommentLike like = new PostCommentLike();
        like.setUserId(currentUser.getId());
        like.setCommentId(commentId);
        postCommentLikeRepository.save(like);

        // 计算新的点赞数
        int newLikeCount = (comment.getLikeCount() != null ? comment.getLikeCount() : 0) + 1;

        // 更新评论的点赞数
        comment.setLikeCount(newLikeCount);
        postCommentRepository.save(comment);

        // 返回更新后的点赞数
        return newLikeCount;
    }

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Transactional
    public int unlikeComment(Long commentId) {
        User currentUser = userService.getCurrentUser();

        // 确认评论存在
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 检查是否已经点赞，如果未点赞则直接返回当前点赞数
        if (!postCommentLikeRepository.existsByUserIdAndCommentId(currentUser.getId(), commentId)) {
            return comment.getLikeCount() != null ? comment.getLikeCount() : 0;
        }

        // 删除点赞记录
        postCommentLikeRepository.deleteByUserIdAndCommentId(currentUser.getId(), commentId);

        // 获取评论的当前点赞数
        int currentLikeCount = (comment.getLikeCount() != null ? comment.getLikeCount() : 0);

        // 计算新的点赞数，确保不会出现负数
        int newLikeCount = Math.max(0, currentLikeCount - 1);

        // 更新评论的点赞数
        comment.setLikeCount(newLikeCount);
        postCommentRepository.save(comment);

        // 返回更新后的点赞数
        return newLikeCount;
    }

    /**
     * 管理员以指定用户身份点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 更新后的点赞数
     */
    @Transactional
    public int likeCommentByAdmin(Long commentId, Long userId) {
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

        // 确认评论存在
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 检查是否已经点赞，如果已点赞则直接返回当前点赞数
        if (postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            return comment.getLikeCount() != null ? comment.getLikeCount() : 0;
        }

        // 创建点赞记录
        PostCommentLike like = new PostCommentLike();
        like.setUserId(userId);
        like.setCommentId(commentId);
        postCommentLikeRepository.save(like);

        // 计算新的点赞数
        int newLikeCount = (comment.getLikeCount() != null ? comment.getLikeCount() : 0) + 1;

        // 更新评论的点赞数
        comment.setLikeCount(newLikeCount);
        postCommentRepository.save(comment);

        // 返回更新后的点赞数
        return newLikeCount;
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    public boolean isCommentLiked(Long commentId, Long userId) {
        return postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    /**
     * 用户删除自己的评论
     * @param commentId 评论ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        
        // 检查评论是否存在
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
                
        // 检查是否是评论作者
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("您没有权限删除该评论");
        }
        
        // 获取评论所属的动态ID
        Long postId = comment.getPostId();
        
        // 检查是否有子评论
        long childCount = postCommentRepository.countByParentId(commentId);
        if (childCount > 0) {
            throw new IllegalStateException("该评论有子评论，无法直接删除");
        }
        
        // 先删除与评论相关的点赞记录
        postCommentLikeRepository.deleteByCommentId(commentId);
        
        // 删除评论
        postCommentRepository.deleteById(commentId);
        
        // 获取当前动态的评论数
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("动态不存在"));
        
        // 确保评论计数不会变为负数
        if (post.getCommentCount() > 0) {
            // 更新动态的评论计数
            postRepository.updateCommentCount(postId, -1);
        } else {
            // 如果当前评论计数已经是0或负数，则强制设置为0
            post.setCommentCount(0);
            postRepository.save(post);
        }
    }

    // =================== 新增DTO转换方法 ===================
    /**
     * Post转PostDTO
     */
    public PostDTO toPostDTO(Post post) {
        return convertToDTO(post);
    }

    /**
     * 根据ID获取PostDTO
     */
    public PostDTO getPostDTO(Long postId) {
        Post post = getPost(postId);
        return convertToDTO(post);
    }

    /**
     * 获取用户点赞的动态DTO分页
     */
    public Page<PostDTO> getUserLikedPostsDTO(Long userId, Pageable pageable) {
        Page<Post> posts = getUserLikedPosts(userId, pageable);
        return posts.map(this::convertToDTO);
    }

    /**
     * 评论动态并返回DTO
     */
    public PostCommentDTO commentPostDTO(Long postId, String content, Long parentId) {
        PostComment comment = commentPost(postId, content, parentId);
        return convertToCommentDTO(comment);
    }

    /**
     * 管理员以指定用户身份评论动态并返回DTO
     */
    public PostCommentDTO commentPostByAdminDTO(Long postId, String content, Long parentId, Long userId) {
        PostComment comment = commentPostByAdmin(postId, content, parentId, userId);
        return convertToCommentDTO(comment);
    }

    /**
     * 获取用户动态DTO分页
     */
    public Page<PostDTO> getUserPostsDTO(Long userId, Pageable pageable) {
        Page<Post> posts = getUserPosts(userId, pageable);
        return posts.map(this::convertToDTO);
    }

    /**
     * 举报动态
     */
    @Transactional
    public void reportPost(Long postId, PostReportRequest request) {
        User currentUser = userService.getCurrentUser();
        // 校验动态是否存在
        // 防止重复举报（同一用户对同一动态仅能举报一次，或可加时间限制）
        postReportRepository.findByPostIdAndReporterId(postId, currentUser.getId()).ifPresent(r -> {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "您已举报过该动态，请勿重复举报");
        });
        // 保存举报记录
        PostReport report = new PostReport();
        report.setPostId(postId);
        report.setReporterId(currentUser.getId());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setCreateTime(LocalDateTime.now());
        report.setStatus("待处理");
        postReportRepository.save(report);
    }

    /**
     * 修复所有帖子的评论计数，确保不存在负数计数
     * @return 修复的帖子数量
     */
    @Transactional
    public int fixAllPostsCommentCount() {
        int fixedCount = 0;
        
        // 获取所有帖子
        List<Post> posts = postRepository.findAll();
        
        for (Post post : posts) {
            // 检查评论计数是否为负数
            if (post.getCommentCount() != null && post.getCommentCount() < 0) {
                // 计算实际评论数
                long actualCommentCount = postCommentRepository.countByPostId(post.getId());
                
                // 更新为正确的评论数或0
                post.setCommentCount((int) Math.max(0, actualCommentCount));
                postRepository.save(post);
                
                fixedCount++;
                log.info("Fixed comment count for post ID {}: from {} to {}", 
                        post.getId(), post.getCommentCount(), actualCommentCount);
            }
        }
        
        return fixedCount;
    }

    /**
     * 根据标签名称查找动态
     * @param tagName 标签名称
     * @param pageable 分页参数
     * @return 包含指定标签名称的动态列表
     */
    public Page<Post> findPostsByTagName(String tagName, Pageable pageable) {
        // 根据标签名查找标签对象
        Optional<PostTag> tagOpt = postTagRepository.findByName(tagName);
        
        // 如果标签不存在，返回空结果
        if (!tagOpt.isPresent()) {
            return Page.empty(pageable);
        }
        
        // 使用标签ID查询关联的帖子
        return findPostsByTag(tagOpt.get().getId(), pageable);
    }
    
    /**
     * 根据标签名称查找动态并返回DTO
     * @param tagName 标签名称
     * @param pageable 分页参数
     * @return 包含指定标签名称的动态DTO列表
     */
    public Page<PostDTO> findPostsByTagNameDTO(String tagName, Pageable pageable) {
        Page<Post> posts = findPostsByTagName(tagName, pageable);
        return posts.map(this::convertToDTO);
    }

    /**
     * 多条件搜索动态
     * @param title 标题关键字
     * @param content 内容关键字
     * @param userName 用户名关键字
     * @param userId 用户ID
     * @param tagName 标签名称
     * @param tagId 标签ID
     * @param minLikes 最小点赞数
     * @param minComments 最小评论数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 符合条件的动态DTO分页列表
     */
    public Page<PostDTO> searchPosts(String title, String content, String userName, Long userId,
                                   String tagName, Long tagId, Integer minLikes, Integer minComments, 
                                   String startTime, String endTime, Pageable pageable) {
        
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                root.fetch("tags", JoinType.LEFT);
                // 避免出现"query specified join fetching, but the owner of the fetched association was not present in the select list"
                query.distinct(true);
            }
            
            List<Predicate> predicates = new ArrayList<>();
            
            // 标题关键字查询
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + title + "%"));
            }
            
            // 内容关键字查询
            if (content != null && !content.isEmpty()) {
                predicates.add(cb.like(root.get("content"), "%" + content + "%"));
            }
            
            // 用户ID查询
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            
            // 用户名查询
            if (userName != null && !userName.isEmpty()) {
                Join<Post, User> userJoin = root.join("user");
                predicates.add(cb.or(
                    cb.like(userJoin.get("username"), "%" + userName + "%"),
                    cb.like(userJoin.get("realName"), "%" + userName + "%")
                ));
            }
            
            // 标签ID查询
            if (tagId != null) {
                Join<Post, PostTag> tagJoin = root.join("tags");
                predicates.add(cb.equal(tagJoin.get("id"), tagId));
            }
            
            // 标签名称查询
            if (tagName != null && !tagName.isEmpty()) {
                Join<Post, PostTag> tagJoin = root.join("tags");
                predicates.add(cb.like(tagJoin.get("name"), "%" + tagName + "%"));
            }
            
            // 最小点赞数查询
            if (minLikes != null && minLikes > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("likeCount"), minLikes));
            }
            
            // 最小评论数查询
            if (minComments != null && minComments > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("commentCount"), minComments));
            }
            
            // 时间范围查询
            try {
                if (startTime != null && !startTime.isEmpty()) {
                    LocalDateTime startDateTime = LocalDateTime.parse(startTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                }
                
                if (endTime != null && !endTime.isEmpty()) {
                    LocalDateTime endDateTime = LocalDateTime.parse(endTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                }
            } catch (Exception e) {
                log.error("解析时间格式错误", e);
                // 时间格式错误时忽略时间条件
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Post> posts = postRepository.findAll(spec, pageable);
        List<PostDTO> dtoList = posts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(dtoList, pageable, posts.getTotalElements());
    }
}