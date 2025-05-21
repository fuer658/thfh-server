package com.thfh.service;

import com.thfh.dto.PostDTO;
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import com.thfh.model.Post;
import com.thfh.model.PostBrowseHistory;
import com.thfh.model.User;
import com.thfh.repository.PostBrowseHistoryRepository;
import com.thfh.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostBrowseHistoryService {

    private static final String POST_NOT_FOUND = "动态不存在";

    private final PostBrowseHistoryRepository postBrowseHistoryRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostService postService;

    public PostBrowseHistoryService(PostBrowseHistoryRepository postBrowseHistoryRepository, PostRepository postRepository, UserService userService, PostService postService) {
        this.postBrowseHistoryRepository = postBrowseHistoryRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.postService = postService;
    }

    /**
     * 记录或更新用户浏览动态的记录
     * 同时增加动态的浏览量
     *
     * @param postId 动态ID
     * @return 浏览记录
     */
    @Transactional
    public PostBrowseHistory recordBrowseHistory(Long postId) {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // 查询动态是否存在
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, POST_NOT_FOUND));

        // 增加动态浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        // 查询是否有浏览记录
        Optional<PostBrowseHistory> existingRecord = postBrowseHistoryRepository.findByUserIdAndPostId(userId, postId);

        if (existingRecord.isPresent()) {
            // 更新现有记录
            PostBrowseHistory history = existingRecord.get();
            history.setLastBrowseTime(LocalDateTime.now());
            history.setBrowseCount(history.getBrowseCount() + 1);
            return postBrowseHistoryRepository.save(history);
        } else {
            // 创建新记录
            PostBrowseHistory newRecord = new PostBrowseHistory();
            newRecord.setUserId(userId);
            newRecord.setPostId(postId);
            newRecord.setLastBrowseTime(LocalDateTime.now());
            newRecord.setBrowseCount(1);
            return postBrowseHistoryRepository.save(newRecord);
        }
    }

    /**
     * 获取用户的浏览历史
     *
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<PostDTO> getUserBrowseHistory(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        // 获取用户浏览记录
        Page<PostBrowseHistory> historyPage = postBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                currentUser.getId(), pageable);
        
        // 转换为动态DTO列表
        return historyPage.map(history -> {
            Post post = postRepository.findById(history.getPostId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, POST_NOT_FOUND));
            return postService.toPostDTO(post);
        });
    }

    /**
     * 获取特定用户的浏览历史（管理员使用）
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<PostDTO> getUserBrowseHistoryByAdmin(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userService.getUserById(userId);
        
        // 获取用户浏览记录
        Page<PostBrowseHistory> historyPage = postBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                userId, pageable);
        
        // 转换为动态DTO列表
        return historyPage.map(history -> {
            Post post = postRepository.findById(history.getPostId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, POST_NOT_FOUND));
            return postService.toPostDTO(post);
        });
    }

    /**
     * 获取用户最近浏览的动态
     *
     * @param limit 限制数量
     * @return 最近浏览动态列表
     */
    public List<PostDTO> getRecentBrowsedPosts(int limit) {
        User currentUser = userService.getCurrentUser();
        
        // 获取最近浏览的动态ID
        List<Long> postIds = postBrowseHistoryRepository.findRecentBrowsedPostIdsByUserId(
                currentUser.getId(), PageRequest.of(0, limit));
        
        // 获取对应的动态，并转换为DTO
        return postIds.stream()
                .map(postId -> {
                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, POST_NOT_FOUND));
                    return postService.toPostDTO(post);
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除单条浏览记录
     *
     * @param historyId 浏览记录ID
     */
    @Transactional
    public void deleteHistory(Long historyId) {
        User currentUser = userService.getCurrentUser();
        
        PostBrowseHistory history = postBrowseHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "浏览记录不存在"));
        
        // 验证是否是用户自己的记录
        if (!history.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该记录");
        }
        
        postBrowseHistoryRepository.delete(history);
    }

    /**
     * 管理员删除浏览记录
     *
     * @param historyId 浏览记录ID
     */
    @Transactional
    public void deleteHistoryByAdmin(Long historyId) {
        PostBrowseHistory history = postBrowseHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "浏览记录不存在"));
        
        postBrowseHistoryRepository.delete(history);
    }

    /**
     * 清空用户的所有浏览记录
     */
    @Transactional
    public void clearUserHistory() {
        User currentUser = userService.getCurrentUser();
        postBrowseHistoryRepository.deleteByUserId(currentUser.getId());
    }

    /**
     * 管理员清空特定用户的浏览记录
     *
     * @param userId 用户ID
     */
    @Transactional
    public void clearUserHistoryByAdmin(Long userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        postBrowseHistoryRepository.deleteByUserId(userId);
    }
} 