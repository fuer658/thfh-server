package com.thfh.dto;

import lombok.Data;

@Data
public class CourseTagDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
}
