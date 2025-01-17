package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private Long teacherId;
    private String teacherName;
    private BigDecimal price;
    private Integer pointsPrice;
    private Integer totalHours;
    private CourseStatus status;
    private String videoUrl;
    private String materials;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer studentCount;
    private Boolean enabled;
    private String createTime;
} 