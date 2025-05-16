package com.thfh.repository;

import com.thfh.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByToUserIdAndStatus(Long toUserId, Integer status);
    List<FriendRequest> findByFromUserIdAndStatus(Long fromUserId, Integer status);
    FriendRequest findByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Integer status);
    List<FriendRequest> findByToUserId(Long toUserId);
    List<FriendRequest> findByFromUserId(Long fromUserId);
} 