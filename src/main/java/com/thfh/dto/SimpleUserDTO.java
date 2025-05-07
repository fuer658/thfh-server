package com.thfh.dto;

import com.thfh.model.User;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

/**
 * 简化版用户数据传输对象
 * 只包含用户基本信息，用于列表展示等场景
 */
@Data
@ApiModel(value = "简化用户信息", description = "包含用户的基本信息，用于列表展示等场景")
public class SimpleUserDTO {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", notes = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 用户真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 用户头像URL
     */
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 将User实体对象转换为SimpleUserDTO对象
     * 
     * @param user 用户实体对象
     * @return 转换后的SimpleUserDTO对象
     */
    public static SimpleUserDTO fromEntity(User user) {
        SimpleUserDTO dto = new SimpleUserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}