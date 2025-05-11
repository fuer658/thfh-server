package com.thfh.repository;

import com.thfh.model.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    Optional<Blacklist> findByUserIdAndBlockedId(Long userId, Long blockedId);
    List<Blacklist> findByUserId(Long userId);
    void deleteByUserIdAndBlockedId(Long userId, Long blockedId);
} 