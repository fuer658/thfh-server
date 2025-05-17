package com.thfh.service;

import com.thfh.model.Friend;
import com.thfh.model.FriendRequest;
import com.thfh.repository.FriendRepository;
import com.thfh.repository.FriendRequestRepository;
import com.thfh.repository.UserRepository;
import com.thfh.model.User;
import com.thfh.dto.FriendRequestDTO;
import com.thfh.dto.FriendDTO;
import com.thfh.model.UserOnlineRecord;
import com.thfh.model.UserOnlineStatus;
import com.thfh.repository.UserOnlineRecordRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalDateTime;

@Api(tags = "好友业务逻辑")
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final BlacklistService blacklistService;
    private final UserRepository userRepository;
    private final UserOnlineRecordRepository userOnlineRecordRepository;

    public FriendService(FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, BlacklistService blacklistService, UserRepository userRepository, UserOnlineRecordRepository userOnlineRecordRepository) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.blacklistService = blacklistService;
        this.userRepository = userRepository;
        this.userOnlineRecordRepository = userOnlineRecordRepository;
    }

    /**
     * 发送好友请求
     */
    @ApiOperation("发送好友请求")
    public String sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            return "不能添加自己为好友";
        }
        // 已是好友
        if (friendRepository.findByUserIdAndFriendId(fromUserId, toUserId) != null) {
            return "已是好友";
        }
        // 已有未处理请求
        FriendRequest exist = friendRequestRepository.findByFromUserIdAndToUserIdAndStatus(fromUserId, toUserId, 0);
        if (exist != null) {
            return "已发送请求，待对方处理";
        }
        FriendRequest request = new FriendRequest();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setStatus(0);
        request.setCreatedAt(new Date());
        request.setUpdatedAt(new Date());
        friendRequestRepository.save(request);
        return "请求已发送";
    }

    /**
     * 处理好友请求
     */
    @ApiOperation("处理好友请求（同意/拒绝）")
    @Transactional
    public String handleFriendRequest(Long requestId, boolean accept) {
        FriendRequest request = friendRequestRepository.findById(requestId).orElse(null);
        if (request == null || request.getStatus() != 0) {
            return "请求不存在或已处理";
        }
        if (accept) {
            // 建立双向好友关系
            Friend f1 = new Friend();
            f1.setUserId(request.getFromUserId());
            f1.setFriendId(request.getToUserId());
            f1.setCreatedAt(new Date());
            Friend f2 = new Friend();
            f2.setUserId(request.getToUserId());
            f2.setFriendId(request.getFromUserId());
            f2.setCreatedAt(new Date());
            friendRepository.save(f1);
            friendRepository.save(f2);
            request.setStatus(1);
        } else {
            request.setStatus(2);
        }
        request.setUpdatedAt(new Date());
        friendRequestRepository.save(request);
        return accept ? "已同意" : "已拒绝";
    }

    /**
     * 查询好友列表
     */
    @ApiOperation("查询好友列表")
    public List<FriendDTO> listFriends(Long userId) {
        List<Friend> friends = friendRepository.findByUserId(userId);
        return friends.stream().map(this::toFriendDTO).collect(Collectors.toList());
    }

    /**
     * 删除好友
     */
    @ApiOperation("删除好友")
    @Transactional
    public String deleteFriend(Long userId, Long friendId) {
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);
        return "已删除好友";
    }

    private FriendRequestDTO toDTO(FriendRequest req) {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(req.getId());
        dto.setFromUserId(req.getFromUserId());
        dto.setToUserId(req.getToUserId());
        dto.setStatus(req.getStatus());
        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());
        User fromUser = userRepository.findById(req.getFromUserId()).orElse(null);
        User toUser = userRepository.findById(req.getToUserId()).orElse(null);
        if (fromUser != null) {
            dto.setFromUserName(fromUser.getUsername());
            dto.setFromUserAvatar(fromUser.getAvatar());
            dto.setFromUserIntroduction(fromUser.getIntroduction());
            dto.setFromUserLevel(fromUser.getLevel());
        }
        if (toUser != null) {
            dto.setToUserName(toUser.getUsername());
            dto.setToUserAvatar(toUser.getAvatar());
            dto.setToUserIntroduction(toUser.getIntroduction());
            dto.setToUserLevel(toUser.getLevel());
        }
        return dto;
    }
    
    public List<FriendRequestDTO> getReceivedFriendRequests(Long userId, String status) {
        List<FriendRequest> list;
        if (status == null || status.isEmpty()) {
            list = friendRequestRepository.findByToUserId(userId);
        } else {
            Integer statusInt = parseStatus(status);
            list = friendRequestRepository.findByToUserIdAndStatus(userId, statusInt);
        }
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public List<FriendRequestDTO> getSentFriendRequests(Long userId, String status) {
        List<FriendRequest> list;
        if (status == null || status.isEmpty()) {
            list = friendRequestRepository.findByFromUserId(userId);
        } else {
            Integer statusInt = parseStatus(status);
            list = friendRequestRepository.findByFromUserIdAndStatus(userId, statusInt);
        }
        if (list == null) return Collections.emptyList();
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    private Integer parseStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending": return 0;
            case "accepted": return 1;
            case "rejected": return 2;
            default: return null;
        }
    }
    
    public String blockFriend(Long userId, Long targetUserId) {
        blacklistService.addToBlacklist(userId, targetUserId);
        return "拉黑成功";
    }
    
    public String unblockFriend(Long userId, Long targetUserId) {
        blacklistService.removeFromBlacklist(userId, targetUserId);
        return "解除拉黑成功";
    }
    
    public String setFriendRemark(Long userId, Long friendId, String remark) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null) {
            return "好友不存在";
        }
        friend.setRemark(remark); // 假设Friend表有remark字段
        friendRepository.save(friend);
        return "备注设置成功";
    }
    
    public FriendDTO getFriendDetail(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        return friend == null ? null : toFriendDTO(friend);
    }
    
    public String cancelFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            return "请求不存在";
        }
        if (!request.getFromUserId().equals(userId)) {
            return "只能撤回自己发起的请求";
        }
        if (request.getStatus() != 0) {
            return "请求已处理，无法撤回";
        }
        request.setStatus(3); // 3=撤回
        request.setUpdatedAt(new java.util.Date());
        friendRequestRepository.save(request);
        return "撤回成功";
    }

    /**
     * 判断两用户是否为好友
     */
    @ApiOperation("判断两用户是否为好友")
    public boolean isFriend(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId) != null;
    }

    private FriendDTO toFriendDTO(Friend friend) {
        FriendDTO dto = new FriendDTO();
        dto.setId(friend.getId());
        dto.setUserId(friend.getUserId());
        dto.setFriendId(friend.getFriendId());
        dto.setRemark(friend.getRemark());
        dto.setCreatedAt(friend.getCreatedAt());
        // 查找好友用户名、头像、简介和等级
        User user = userRepository.findById(friend.getFriendId()).orElse(null);
        if (user != null) {
            dto.setFriendName(user.getUsername());
            dto.setAvatar(user.getAvatar());
            dto.setIntroduction(user.getIntroduction());
            dto.setLevel(user.getLevel());
            
            // 获取好友在线状态
            UserOnlineRecord onlineRecord = userOnlineRecordRepository.findTopByUserOrderByUpdateTimeDesc(user).orElse(null);
            if (onlineRecord != null) {
                dto.setOnlineStatus(onlineRecord.getStatus());
                // 将LocalDateTime转为Date
                Date lastActiveTime = java.util.Date.from(onlineRecord.getLastActive().atZone(java.time.ZoneId.systemDefault()).toInstant());
                dto.setLastActiveTime(lastActiveTime);
                
                // 检查是否超过不活跃超时时间
                Duration inactiveTimeout = Duration.ofMinutes(5);
                if (onlineRecord.getStatus() != UserOnlineStatus.OFFLINE && 
                    Duration.between(onlineRecord.getLastActive(), LocalDateTime.now()).compareTo(inactiveTimeout) > 0) {
                    dto.setOnlineStatus(UserOnlineStatus.OFFLINE);
                }
            } else {
                dto.setOnlineStatus(UserOnlineStatus.OFFLINE);
            }
        }
        return dto;
    }
} 