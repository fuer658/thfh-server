package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户查询DTO
 */
@Data
@Getter
@Setter
@Schema(description = "用户查询DTO - 用于查询用户列表的参数")
public class UserQueryDTO {
    
    @Schema(description = "关键词 - 用于搜索用户名或真实姓名", example = "张三")
    private String keyword;
    
    @Schema(description = "用户类型 - 筛选特定类型的用户", example = "STUDENT")
    private String userType;
    
    @Schema(description = "认证状态 - 筛选已认证或未认证的用户", example = "true")
    private Boolean verified;
    
    @Schema(description = "状态 - 筛选特定状态的用户", example = "ACTIVE")
    private String status;
    
    @Schema(description = "页码 - 分页参数", example = "1")
    private Integer page;
    
    @Schema(description = "每页数量 - 分页参数", example = "10")
    private Integer size;
    
    @Schema(description = "排序字段", example = "createTime")
    private String sortBy;
    
    @Schema(description = "排序方向", example = "DESC")
    private String sortDirection;
}
