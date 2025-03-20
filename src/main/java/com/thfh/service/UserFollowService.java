package com.thfh.service;

import com.thfh.model.User;
import com.thfh.model.UserFollow;
import com.thfh.repository.UserFollowRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFollowService {
    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            UserFollow userFollow = new UserFollow();
            User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("关注者不存在"));
            User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("被关注者不存在"));
            userFollow.setFollower(follower);
            userFollow.setFollowing(following);
            userFollowRepository.save(userFollow);
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Page<User> getFollowingList(Long userId, Pageable pageable) {
        return userFollowRepository.findByFollowerId(userId, pageable)
                .map(UserFollow::getFollowing);
    }

    public Page<User> getFollowerList(Long userId, Pageable pageable) {
        return userFollowRepository.findByFollowingId(userId, pageable)
                .map(UserFollow::getFollower);
    }

    public long getFollowingCount(Long userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    public long getFollowerCount(Long userId) {
        return userFollowRepository.countByFollowingId(userId);
    }
} 