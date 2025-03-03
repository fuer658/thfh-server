package com.thfh.dto;

import lombok.Data;

@Data
public class AdminDTO {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Boolean enabled;
}