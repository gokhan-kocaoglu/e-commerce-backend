package com.commerce.e_commerce.repository.customer;

import com.commerce.e_commerce.domain.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserIdAndDeletedFalse(UUID userId);
}
