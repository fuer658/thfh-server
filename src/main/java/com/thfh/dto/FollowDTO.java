package com.thfh.dto;

import com.thfh.model.Follow;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowDTO {
    private Long id;
    private Long followerId;
    private Long followedId;
    private LocalDateTime followTime;
    private UserDTO follower;
    private UserDTO followed;

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