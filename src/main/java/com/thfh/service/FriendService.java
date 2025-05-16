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

    public FriendService(FriendRepository friendRepository, FriendRequestRepository friendRequestRepository) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
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
        return "拉黑成功";
    }
    public String unblockFriend(Long userId, Long targetUserId) {
        return "解除拉黑成功";
    }
    public String setFriendRemark(Long userId, Long friendId, String remark) {
        return "备注设置成功";
    }
    public Friend getFriendDetail(Long userId, Long friendId) {
        return null;
    }
    public String cancelFriendRequest(Long requestId, Long userId) {
        return "撤回成功";
    }
} 