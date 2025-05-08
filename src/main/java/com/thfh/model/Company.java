package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 公司名称

    private String logo; // 公司logo

    @Column(length = 1000)
    private String description; // 公司简介

    private String industry; // 所属行业

    private String scale; // 公司规模

    private String website; // 公司网站

    private String address; // 公司地址

    private String workStartTime; // 上班时间

    private String workEndTime; // 下班时间

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    private String companyTreatment; // 公司待遇

    private String employeeBenefits; // 员工福利

    private String promotionProspects; // 晋升待遇

    private String jobRequirements; // 岗位要求
    
    private String salary; // 薪资范围
    
    @Column(length = 500)
    private String tags; // 公司标签，多个标签以逗号分隔
    
    @Column(length = 500)
    private String positions; // 招聘岗位，多个岗位以逗号分隔

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("company")
    private List<CompanyAlbumCategory> albumCategories;
}