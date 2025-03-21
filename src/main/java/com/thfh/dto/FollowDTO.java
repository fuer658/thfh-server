package com.thfh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thfh.model.Follow;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class FollowDTO {
    private Long id;
    private Long followerId = 0L;  // 默认值 0，防止 null 访问
    private Long followedId = 0L;  // 默认值 0
    private LocalDateTime followTime = LocalDateTime.now();  // 避免 null 时间戳

    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler", "password", "email", "phone",
            "createTime", "updateTime", "lastLoginTime", "status", "roles",
            "qualification", "speciality", "disability", "points", "birthday"
    })
    private SimpleUserDTO follower;

    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler", "password", "email", "phone",
            "createTime", "updateTime", "lastLoginTime", "status", "roles",
            "qualification", "speciality", "disability", "points", "birthday"
    })
    private SimpleUserDTO followed;

    /**
     * 转换 Follow 实体，完整返回关注和粉丝
     */
    public static FollowDTO fromEntity(Follow follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setFollowerId(Optional.ofNullable(follow.getFollowerId()).orElse(0L));
        dto.setFollowedId(Optional.ofNullable(follow.getFollowedId()).orElse(0L));
        dto.setFollowTime(Optional.ofNullable(follow.getFollowTime()).orElse(LocalDateTime.now()));

        dto.setFollower(Optional.ofNullable(follow.getFollower()).map(SimpleUserDTO::fromEntity).orElse(null));
        dto.setFollowed(Optional.ofNullable(follow.getFollowed()).map(SimpleUserDTO::fromEntity).orElse(null));

        return dto;
    }

    /**
     * 仅返回关注的用户（Following），不包含粉丝信息
     */
    public static FollowDTO fromEntityForFollowing(Follow follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setFollowerId(Optional.ofNullable(follow.getFollowerId()).orElse(0L));
        dto.setFollowedId(Optional.ofNullable(follow.getFollowedId()).orElse(0L));
        dto.setFollowTime(Optional.ofNullable(follow.getFollowTime()).orElse(LocalDateTime.now()));

        dto.setFollowed(Optional.ofNullable(follow.getFollowed()).map(SimpleUserDTO::fromEntity).orElse(null));

        return dto;
    }

    /**
     * 仅返回粉丝（Followers），不包含关注的用户信息
     */
    public static FollowDTO fromEntityForFollowers(Follow follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setFollowerId(Optional.ofNullable(follow.getFollowerId()).orElse(0L));
        dto.setFollowedId(Optional.ofNullable(follow.getFollowedId()).orElse(0L));
        dto.setFollowTime(Optional.ofNullable(follow.getFollowTime()).orElse(LocalDateTime.now()));

        dto.setFollower(Optional.ofNullable(follow.getFollower()).map(SimpleUserDTO::fromEntity).orElse(null));

        return dto;
    }
}