package com.thfh.repository;

import com.thfh.model.PointsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointsRecordRepository extends JpaRepository<PointsRecord, Long>, JpaSpecificationExecutor<PointsRecord> {
} 