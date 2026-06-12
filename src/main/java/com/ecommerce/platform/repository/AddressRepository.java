package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Address;
import com.ecommerce.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
}
