package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Order> findByIdAndUserEmail(UUID id, String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.variant v JOIN v.product p WHERE p.seller.email = :email ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerEmail(@org.springframework.data.repository.query.Param("email") String email);
}
