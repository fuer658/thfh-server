package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 课程数据传输对象
 * 用于在不同层之间传输课程信息
 */
@Data
public class CourseDTO {
    /**
     * 课程ID，唯一标识
     */
    private Long id;
    
    /**
     * 课程标题
     */
    private String title;
    
    /**
     * 课程详细描述
     */
    private String description;
    
    /**
     * 课程封面图片URL
     */
    private String coverImage;
    
    /**
     * 课程封面视频URL
     */
    private String coverVideo;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 讲师姓名
     */
    private String teacherName;
    
    /**
     * 课程价格（人民币）
     */
    private BigDecimal price;
    
    /**
     * 课程积分价格
     */
    private Integer pointsPrice;
    
    /**
     * 课程总学时
     */
    private Integer totalHours;
    
    /**
     * 课程状态（如：草稿、已发布、已下架等）
     */
    private CourseStatus status;
    
    /**
     * 课程视频URL
     */
    private String videoUrl;
    
    /**
     * 课程学习资料
     */
    private String materials;
    
    /**
     * 课程点赞数
     */
    private Integer likeCount;
    
    /**
     * 课程收藏数
     */
    private Integer favoriteCount;
    
    /**
     * 课程学生数量
     */
    private Integer studentCount;
    
    /**
     * 课程是否启用
     */
    private Boolean enabled;
    
    /**
     * 课程标签
     */
    private Set<CourseTagDTO> tags;
    
    /**
     * 课程创建时间
     */
    private String createTime;
} 