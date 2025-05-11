package com.thfh.repository;

import com.thfh.model.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    Optional<PostReport> findByPostIdAndReporterId(Long postId, Long reporterId);
} 