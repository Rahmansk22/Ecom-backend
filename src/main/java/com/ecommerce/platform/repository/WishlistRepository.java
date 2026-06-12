package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByUserEmail(String email);
    Optional<Wishlist> findByUserId(UUID userId);
}
