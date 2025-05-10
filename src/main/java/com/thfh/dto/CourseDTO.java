package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 课程数据传输对象
 * 用于在不同层之间传输课程信息
 */
@Data
@ApiModel(value = "课程信息", description = "包含课程的详细信息")
public class CourseDTO {
    /**
     * 课程ID，唯一标识
     */
    @ApiModelProperty(value = "课程ID", notes = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 课程标题
     */
    @ApiModelProperty(value = "课程标题", required = true, example = "Java编程入门")
    private String title;
    
    /**
     * 课程详细描述
     */
    @ApiModelProperty(value = "课程描述", example = "本课程介绍Java编程基础知识")
    private String description;
    
    /**
     * 课程封面图片URL
     */
    @ApiModelProperty(value = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;
    
    /**
     * 课程封面视频URL
     */
    @ApiModelProperty(value = "封面视频URL", example = "https://example.com/cover.mp4")
    private String coverVideo;
    
    /**
     * 讲师ID
     */
    @ApiModelProperty(value = "讲师ID", example = "100")
    private Long teacherId;
    
    /**
     * 讲师姓名
     */
    @ApiModelProperty(value = "讲师姓名", example = "张教授")
    private String teacherName;
    
    /**
     * 课程价格（人民币）
     */
    @ApiModelProperty(value = "课程价格", notes = "人民币", example = "99.9")
    private BigDecimal price;
    
    /**
     * 课程积分价格
     */
    @ApiModelProperty(value = "积分价格", example = "1000")
    private Integer pointsPrice;
    
    /**
     * 课程总学时
     */
    @ApiModelProperty(value = "总学时", example = "24")
    private Integer totalHours;
    
    /**
     * 课程状态（如：草稿、已发布、已下架等）
     */
    @ApiModelProperty(value = "课程状态", notes = "如草稿、已发布、已下架等", example = "PUBLISHED")
    private CourseStatus status;
    
    /**
     * 课程视频URL
     */
    @ApiModelProperty(value = "视频URL", example = "https://example.com/course.mp4")
    private String videoUrl;
    
    /**
     * 课程学习资料
     */
    @ApiModelProperty(value = "学习资料", example = "PDF讲义、示例代码")
    private String materials;
    
    /**
     * 课程点赞数
     */
    @ApiModelProperty(value = "点赞数", example = "256")
    private Integer likeCount;
    
    /**
     * 课程收藏数
     */
    @ApiModelProperty(value = "收藏数", example = "128")
    private Integer favoriteCount;
    
    /**
     * 课程学生数量
     */
    @ApiModelProperty(value = "学生数量", example = "512")
    private Integer studentCount;
    
    /**
     * 课程是否启用
     */
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 课程标签
     */
    @ApiModelProperty(value = "课程标签")
    private Set<CourseTagDTO> tags;
    
    /**
     * 课程创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private String createTime;
    
    /**
     * 课程浏览量
     */
    @ApiModelProperty(value = "浏览量", example = "100")
    private Integer viewCount;
} 