package com.thfh.repository;

import com.thfh.model.PointsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 积分记录数据访问接口
 * 提供对积分记录(PointsRecord)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface PointsRecordRepository extends JpaRepository<PointsRecord, Long>, JpaSpecificationExecutor<PointsRecord> {
} 