package com.thfh.dto;

import com.thfh.model.Follow;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注关系数据传输对象
 * 用于在不同层之间传输用户关注关系信息
 */
@Data
public class FollowDTO {
    /**
     * 关注关系ID，唯一标识
     */
    private Long id;
    
    /**
     * 关注者用户ID
     */
    private Long followerId;
    
    /**
     * 被关注者用户ID
     */
    private Long followedId;
    
    /**
     * 关注时间
     */
    private LocalDateTime followTime;
    
    /**
     * 关注者用户信息
     */
    private UserDTO follower;
    
    /**
     * 被关注者用户信息
     */
    private UserDTO followed;

    /**
     * 将Follow实体对象转换为FollowDTO对象
     * 
     * @param follow 关注关系实体对象
     * @return 转换后的FollowDTO对象
     */
    public static FollowDTO fromEntity(Follow follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setFollowerId(follow.getFollowerId());
        dto.setFollowedId(follow.getFollowedId());
        dto.setFollowTime(follow.getFollowTime());
        
        if (follow.getFollower() != null) {
            dto.setFollower(UserDTO.fromEntity(follow.getFollower()));
        }
        if (follow.getFollowed() != null) {
            dto.setFollowed(UserDTO.fromEntity(follow.getFollowed()));
        }
        
        return dto;
    }
}