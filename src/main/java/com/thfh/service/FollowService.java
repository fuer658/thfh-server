package com.thfh.service;

import com.thfh.dto.FollowDTO;
import com.thfh.model.Follow;
import com.thfh.model.User;
import com.thfh.repository.FollowRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 关注服务类
 * 提供用户关注相关的业务逻辑处理，包括关注、取消关注、获取关注列表等功能
 */
@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 关注用户
     * 创建当前登录用户与目标用户的关注关系
     * @param followedId 被关注用户的ID
     * @throws RuntimeException 当用户未登录、已经关注过该用户或被关注用户不存在时抛出
     */
    @Transactional
    public void follow(Long followedId) {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查是否已经关注
        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFollowedId(currentUser.getId(), followedId);
        if (existingFollow.isPresent()) {
            throw new RuntimeException("已经关注过该用户");
        }

        // 检查被关注用户是否存在
        if (!userRepository.existsById(followedId)) {
            throw new RuntimeException("用户不存在");
        }

        // 创建关注关系
        Follow follow = new Follow();
        follow.setFollowerId(currentUser.getId());
        follow.setFollowedId(followedId);
        follow.setFollowTime(LocalDateTime.now());
        followRepository.save(follow);
    }

    /**
     * 取消关注用户
     * 删除当前登录用户与目标用户的关注关系
     * @param followedId 被关注用户的ID
     * @throws RuntimeException 当用户未登录时抛出
     */
    @Transactional
    public void unfollow(Long followedId) {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        followRepository.deleteByFollowerIdAndFollowedId(currentUser.getId(), followedId);
    }

    /**
     * 获取用户关注的用户列表
     * @param userId 用户ID
     * @return 用户关注的用户DTO列表
     */
    public List<FollowDTO> getFollowingList(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return follows.stream().map(FollowDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * 获取关注用户的粉丝列表
     * @param userId 用户ID
     * @return 关注该用户的粉丝DTO列表
     */
    public List<FollowDTO> getFollowersList(Long userId) {
        List<Follow> follows = followRepository.findByFollowedId(userId);
        return follows.stream().map(FollowDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * 获取用户关注的用户数量
     * @param userId 用户ID
     * @return 用户关注的用户数量
     */
    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    /**
     * 获取用户的粉丝数量
     * @param userId 用户ID
     * @return 关注该用户的粉丝数量
     */
    public long getFollowersCount(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    /**
     * 检查用户是否关注了另一个用户
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return 如果关注关系存在则返回true，否则返回false
     */
    public boolean isFollowing(Long followerId, Long followedId) {
        return followRepository.findByFollowerIdAndFollowedId(followerId, followedId).isPresent();
    }
}