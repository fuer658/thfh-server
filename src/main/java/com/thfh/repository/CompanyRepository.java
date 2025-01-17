package com.thfh.repository;

import com.thfh.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    @Query("SELECT c FROM Company c WHERE (:name is null OR c.name LIKE CONCAT('%', :name, '%'))")
    Page<Company> findByCondition(@Param("name") String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Company c SET c.enabled = :enabled WHERE c.id = :id")
    void updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    @Query("SELECT c FROM Company c WHERE c.enabled = true")
    List<Company> findAllEnabled();
}