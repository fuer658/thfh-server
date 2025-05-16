package com.thfh.service;

import com.thfh.model.Friend;
import com.thfh.model.FriendRequest;
import com.thfh.repository.FriendRepository;
import com.thfh.repository.FriendRequestRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Collections;

@Api(tags = "好友业务逻辑")
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final BlacklistService blacklistService;

    public FriendService(FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, BlacklistService blacklistService) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.blacklistService = blacklistService;
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
    public List<Friend> listFriends(Long userId) {
        return friendRepository.findByUserId(userId);
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

    public List<FriendRequest> getReceivedFriendRequests(Long userId, String status) {
        if (status == null || status.isEmpty()) {
            List<FriendRequest> list = friendRequestRepository.findByToUserId(userId);
            return list != null ? list : Collections.emptyList();
        } else {
            Integer statusInt = parseStatus(status);
            List<FriendRequest> list = friendRequestRepository.findByToUserIdAndStatus(userId, statusInt);
            return list != null ? list : Collections.emptyList();
        }
    }
    public List<FriendRequest> getSentFriendRequests(Long userId, String status) {
        if (status == null || status.isEmpty()) {
            List<FriendRequest> list = friendRequestRepository.findByFromUserId(userId);
            return list != null ? list : Collections.emptyList();
        } else {
            Integer statusInt = parseStatus(status);
            List<FriendRequest> list = friendRequestRepository.findByFromUserIdAndStatus(userId, statusInt);
            return list != null ? list : Collections.emptyList();
        }
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
    public Friend getFriendDetail(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId);
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
} 