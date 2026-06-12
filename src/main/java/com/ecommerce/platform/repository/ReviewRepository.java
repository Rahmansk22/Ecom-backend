package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);
    List<Review> findByProductId(UUID productId);
    boolean existsByProductIdAndUserId(UUID productId, UUID userId);
}
