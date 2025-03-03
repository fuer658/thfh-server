package com.thfh.repository;

import com.thfh.model.User;
import com.thfh.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);
    Page<User> findByUserType(UserType userType, Pageable pageable);
    Optional<User> findByUsername(String username);
}