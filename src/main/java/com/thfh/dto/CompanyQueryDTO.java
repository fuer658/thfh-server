package com.thfh.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class CompanyQueryDTO {
    private String name;
    private String industry;
    private Boolean enabled;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createTime";
    private String sortDirection = "DESC";

    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }
}
