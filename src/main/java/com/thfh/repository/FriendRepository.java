package com.thfh.repository;

import com.thfh.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserId(Long userId);
    Friend findByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendId(Long userId, Long friendId);
} 