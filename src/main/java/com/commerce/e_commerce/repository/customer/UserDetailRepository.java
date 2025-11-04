package com.commerce.e_commerce.repository.customer;

import com.commerce.e_commerce.domain.customer.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDetailRepository extends JpaRepository<UserDetail, UUID> {
    Optional<UserDetail> findByUserId(UUID userId);
}
