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

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followedId) {
        // 检查是否已经关注
        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
        if (existingFollow.isPresent()) {
            throw new RuntimeException("已经关注过该用户");
        }

        // 检查用户是否存在
        if (!userRepository.existsById(followerId) || !userRepository.existsById(followedId)) {
            throw new RuntimeException("用户不存在");
        }

        // 创建关注关系
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowedId(followedId);
        follow.setFollowTime(LocalDateTime.now());
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        followRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);
    }

    public List<FollowDTO> getFollowingList(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return follows.stream().map(FollowDTO::fromEntity).collect(Collectors.toList());
    }

    public List<FollowDTO> getFollowersList(Long userId) {
        List<Follow> follows = followRepository.findByFollowedId(userId);
        return follows.stream().map(FollowDTO::fromEntity).collect(Collectors.toList());
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    public long getFollowersCount(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    public boolean isFollowing(Long followerId, Long followedId) {
        return followRepository.findByFollowerIdAndFollowedId(followerId, followedId).isPresent();
    }
}