package com.thfh.service;

import com.thfh.dto.BlacklistDTO;
import com.thfh.model.Blacklist;
import com.thfh.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlacklistService {
    @Autowired
    private BlacklistRepository blacklistRepository;

    @Transactional
    public void addToBlacklist(Long userId, Long blockedId) {
        if (userId.equals(blockedId)) {
            throw new IllegalArgumentException("不能拉黑自己");
        }
        blacklistRepository.findByUserIdAndBlockedId(userId, blockedId)
                .ifPresent(b -> { throw new IllegalArgumentException("已在黑名单中"); });
        Blacklist blacklist = new Blacklist();
        blacklist.setUserId(userId);
        blacklist.setBlockedId(blockedId);
        blacklist.setCreateTime(LocalDateTime.now());
        blacklistRepository.save(blacklist);
    }

    @Transactional
    public void removeFromBlacklist(Long userId, Long blockedId) {
        blacklistRepository.deleteByUserIdAndBlockedId(userId, blockedId);
    }

    public List<BlacklistDTO> getBlacklist(Long userId) {
        List<Blacklist> list = blacklistRepository.findByUserId(userId);
        return list.stream().map(b -> {
            BlacklistDTO dto = new BlacklistDTO();
            dto.setUserId(b.getUserId());
            dto.setBlockedId(b.getBlockedId());
            dto.setCreateTime(b.getCreateTime());
            return dto;
        }).collect(Collectors.toList());
    }
} 