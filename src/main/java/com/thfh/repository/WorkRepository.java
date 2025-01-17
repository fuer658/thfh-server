package com.thfh.repository;

import com.thfh.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkRepository extends JpaRepository<Work, Long>, JpaSpecificationExecutor<Work> {
    boolean existsByTitleAndStudentId(String title, Long studentId);
} 