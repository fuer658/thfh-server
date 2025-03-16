package com.thfh.repository;

import com.thfh.model.DisabilityCertification;
import com.thfh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisabilityCertificationRepository extends JpaRepository<DisabilityCertification, Long>, JpaSpecificationExecutor<DisabilityCertification> {
    List<DisabilityCertification> findByUser(User user);
    List<DisabilityCertification> findByStatus(DisabilityCertification.Status status);
    Optional<DisabilityCertification> findTopByUserOrderByCreateTimeDesc(User user);
    boolean existsByUserAndStatus(User user, DisabilityCertification.Status status);
} 