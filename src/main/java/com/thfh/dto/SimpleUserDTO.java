package com.thfh.dto;

import com.thfh.model.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 简化版用户数据传输对象
 * 只包含用户基本信息，用于列表展示等场景
 */
@Data
public class SimpleUserDTO {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户真实姓名
     */
    private String realName;
    
    /**
     * 用户头像URL
     */
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