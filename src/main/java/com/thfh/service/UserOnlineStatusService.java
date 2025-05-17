package com.thfh.service;

import com.thfh.dto.UserOnlineStatusDTO;
import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.Friend;
import com.thfh.model.User;
import com.thfh.model.UserOnlineRecord;
import com.thfh.model.UserOnlineStatus;
import com.thfh.repository.FriendRepository;
import com.thfh.repository.UserOnlineRecordRepository;
import com.thfh.repository.UserRepository;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户在线状态服务
 * 管理用户的在线状态信息，包括更新状态、查询状态和通知好友
 */
@Api(tags = "用户在线状态服务")
@Service
@Slf4j
public class UserOnlineStatusService {

    private static final Duration INACTIVE_TIMEOUT = Duration.ofMinutes(5);
    
    private final UserOnlineRecordRepository userOnlineRecordRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public UserOnlineStatusService(
            UserOnlineRecordRepository userOnlineRecordRepository,
            UserRepository userRepository,
            FriendRepository friendRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.userOnlineRecordRepository = userOnlineRecordRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 更新用户在线状态
     * 
     * @param userId 用户ID
     * @param status 在线状态
     * @return 更新后的状态DTO
     */
    public UserOnlineStatusDTO updateUserStatus(Long userId, UserOnlineStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        UserOnlineRecord record = userOnlineRecordRepository.findTopByUserOrderByUpdateTimeDesc(user)
                .orElse(new UserOnlineRecord());
        
        // 如果是新记录或状态发生变化，则创建新记录
        if (record.getId() == null || record.getStatus() != status) {
            record = new UserOnlineRecord();
            record.setUser(user);
            record.setStatus(status);
            record.setLastActive(LocalDateTime.now());
            record = userOnlineRecordRepository.save(record);
            
            // 通知该用户的好友状态变化
            notifyFriendsOfStatusChange(userId, status);
        } else {
            // 只更新最后活跃时间
            record.setLastActive(LocalDateTime.now());
            record = userOnlineRecordRepository.save(record);
        }
        
        return convertToDTO(record, null);
    }
    
    /**
     * 用户心跳接口，更新用户最后活跃时间
     * 
     * @param userId 用户ID
     * @return 更新后的状态DTO
     */
    public UserOnlineStatusDTO heartbeat(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Optional<UserOnlineRecord> recordOpt = userOnlineRecordRepository.findTopByUserOrderByUpdateTimeDesc(user);
        
        UserOnlineRecord record;
        if (recordOpt.isPresent()) {
            record = recordOpt.get();
            // 更新最后活跃时间
            record.setLastActive(LocalDateTime.now());
            
            // 如果之前状态是离线，则更新为在线
            if (record.getStatus() == UserOnlineStatus.OFFLINE) {
                record.setStatus(UserOnlineStatus.ONLINE);
                // 通知好友用户上线
                notifyFriendsOfStatusChange(userId, UserOnlineStatus.ONLINE);
            }
        } else {
            // 创建新的在线记录
            record = new UserOnlineRecord();
            record.setUser(user);
            record.setStatus(UserOnlineStatus.ONLINE);
            record.setLastActive(LocalDateTime.now());
            // 通知好友用户上线
            notifyFriendsOfStatusChange(userId, UserOnlineStatus.ONLINE);
        }
        
        record = userOnlineRecordRepository.save(record);
        return convertToDTO(record, null);
    }
    
    /**
     * 获取用户在线状态
     * 
     * @param userId 用户ID
     * @return 用户在线状态DTO
     */
    public UserOnlineStatusDTO getUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Optional<UserOnlineRecord> recordOpt = userOnlineRecordRepository.findTopByUserOrderByUpdateTimeDesc(user);
        
        if (recordOpt.isPresent()) {
            UserOnlineRecord record = recordOpt.get();
            
            // 检查是否超过不活跃超时时间
            if (record.getStatus() != UserOnlineStatus.OFFLINE && 
                Duration.between(record.getLastActive(), LocalDateTime.now()).compareTo(INACTIVE_TIMEOUT) > 0) {
                // 自动更新为离线状态
                record.setStatus(UserOnlineStatus.OFFLINE);
                record = userOnlineRecordRepository.save(record);
            }
            
            return convertToDTO(record, null);
        } else {
            // 没有记录，默认为离线
            UserOnlineStatusDTO dto = new UserOnlineStatusDTO();
            dto.setUserId(userId);
            dto.setUsername(user.getUsername());
            dto.setStatus(UserOnlineStatus.OFFLINE);
            dto.setLastActive(null);
            dto.setAvatar(user.getAvatar());
            return dto;
        }
    }
    
    /**
     * 获取好友在线状态列表
     * 
     * @param userId 用户ID
     * @return 好友在线状态列表
     */
    public List<UserOnlineStatusDTO> getFriendsStatus(Long userId) {
        // 获取用户的好友列表
        List<Friend> friends = friendRepository.findByUserId(userId);
        if (friends.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取好友ID列表
        List<Long> friendIds = friends.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());
        
        // 好友备注Map
        Map<Long, String> friendRemarkMap = friends.stream()
                .collect(Collectors.toMap(Friend::getFriendId, Friend::getRemark, (a, b) -> a));
        
        // 查询好友的在线状态
        List<UserOnlineRecord> onlineRecords = userOnlineRecordRepository.findLatestByUserIds(friendIds);
        
        // 组织好友ID到最新记录的映射
        Map<Long, UserOnlineRecord> userIdToRecord = onlineRecords.stream()
                .collect(Collectors.toMap(
                        r -> r.getUser().getId(),
                        r -> r,
                        (a, b) -> a.getUpdateTime().isAfter(b.getUpdateTime()) ? a : b
                ));
        
        // 获取所有好友用户实体
        List<User> friendUsers = userRepository.findAllById(friendIds);
        Map<Long, User> userIdToUser = friendUsers.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        // 构建返回结果
        List<UserOnlineStatusDTO> result = new ArrayList<>();
        for (Long friendId : friendIds) {
            User user = userIdToUser.get(friendId);
            if (user == null) continue;
            
            UserOnlineRecord record = userIdToRecord.get(friendId);
            UserOnlineStatusDTO dto = new UserOnlineStatusDTO();
            dto.setUserId(friendId);
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());
            dto.setRemark(friendRemarkMap.get(friendId));
            
            if (record != null) {
                dto.setStatus(record.getStatus());
                dto.setLastActive(record.getLastActive());
                
                // 检查是否超过不活跃超时时间
                if (record.getStatus() != UserOnlineStatus.OFFLINE && 
                    Duration.between(record.getLastActive(), LocalDateTime.now()).compareTo(INACTIVE_TIMEOUT) > 0) {
                    dto.setStatus(UserOnlineStatus.OFFLINE);
                }
            } else {
                dto.setStatus(UserOnlineStatus.OFFLINE);
                dto.setLastActive(null);
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * 通知用户的好友其状态变化
     * 
     * @param userId 状态变化的用户ID
     * @param status 新状态
     */
    private void notifyFriendsOfStatusChange(Long userId, UserOnlineStatus status) {
        // 获取用户信息
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        
        // 查询谁将该用户添加为好友
        List<Friend> friendships = friendRepository.findByFriendId(userId);
        if (friendships.isEmpty()) return;
        
        // 通知每个好友
        for (Friend friendship : friendships) {
            Long friendId = friendship.getUserId();
            
            // 准备通知数据
            UserOnlineStatusDTO statusDTO = new UserOnlineStatusDTO();
            statusDTO.setUserId(userId);
            statusDTO.setUsername(user.getUsername());
            statusDTO.setStatus(status);
            statusDTO.setLastActive(LocalDateTime.now());
            statusDTO.setAvatar(user.getAvatar());
            statusDTO.setRemark(friendship.getRemark());
            
            // 发送WebSocket消息
            messagingTemplate.convertAndSendToUser(
                    friendId.toString(),
                    "/queue/friend-status",
                    statusDTO
            );
        }
    }

    /**
     * 定时任务：检查不活跃用户，将状态更新为离线
     * 每1分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void checkInactiveUsers() {
        log.debug("执行检查不活跃用户任务");
        
        LocalDateTime inactiveTime = LocalDateTime.now().minus(INACTIVE_TIMEOUT);
        
        // 查找活动状态但最后活跃时间超过阈值的用户
        List<UserOnlineRecord> records = userOnlineRecordRepository.findByStatus(UserOnlineStatus.ONLINE);
        records.addAll(userOnlineRecordRepository.findByStatus(UserOnlineStatus.BUSY));
        records.addAll(userOnlineRecordRepository.findByStatus(UserOnlineStatus.AWAY));
        
        List<UserOnlineRecord> inactiveRecords = records.stream()
                .filter(r -> r.getLastActive().isBefore(inactiveTime))
                .collect(Collectors.toList());
        
        for (UserOnlineRecord record : inactiveRecords) {
            record.setStatus(UserOnlineStatus.OFFLINE);
            userOnlineRecordRepository.save(record);
            
            // 通知好友该用户已离线
            notifyFriendsOfStatusChange(record.getUser().getId(), UserOnlineStatus.OFFLINE);
        }
    }
    
    /**
     * 将记录实体转换为DTO
     * 
     * @param record 记录实体
     * @param remark 好友备注（可为null）
     * @return DTO对象
     */
    private UserOnlineStatusDTO convertToDTO(UserOnlineRecord record, String remark) {
        UserOnlineStatusDTO dto = new UserOnlineStatusDTO();
        dto.setUserId(record.getUser().getId());
        dto.setUsername(record.getUser().getUsername());
        dto.setStatus(record.getStatus());
        dto.setLastActive(record.getLastActive());
        dto.setAvatar(record.getUser().getAvatar());
        dto.setRemark(remark);
        return dto;
    }
} 