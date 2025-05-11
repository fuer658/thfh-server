package com.thfh.repository;

import com.thfh.model.ArtworkReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArtworkReportRepository extends JpaRepository<ArtworkReport, Long> {
    Optional<ArtworkReport> findByArtworkIdAndReporterId(Long artworkId, Long reporterId);
} 