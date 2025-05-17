package com.thfh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thfh.model.Follow;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 关注关系数据传输对象
 */
@Data
@ApiModel(value = "关注关系信息", description = "包含用户关注关系的详细信息")
public class FollowDTO {
    @ApiModelProperty(value = "关注ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "关注者ID", notes = "当前用户ID", example = "100")
    private Long followerId = 0L;  // 默认值 0，防止 null 访问
    
    @ApiModelProperty(value = "被关注者ID", notes = "被关注的用户ID", example = "200")
    private Long followedId = 0L;  // 默认值 0
    
    @ApiModelProperty(value = "关注时间", example = "2023-01-01 10:00:00")
    private LocalDateTime followTime = LocalDateTime.now();  // 避免 null 时间戳

    @ApiModelProperty(value = "关注者信息", notes = "包含关注者的基本信息")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler", "password", "email", "phone",
            "createTime", "updateTime", "lastLoginTime", "status", "roles",
            "disability", "points", "birthday"
    })
    private SimpleUserDTO follower;

    @ApiModelProperty(value = "被关注者信息", notes = "包含被关注者的基本信息")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler", "password", "email", "phone",
            "createTime", "updateTime", "lastLoginTime", "status", "roles",
            "disability", "points", "birthday"
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