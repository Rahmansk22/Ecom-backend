package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Category;
import com.ecommerce.platform.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySlug(String slug);
    Page<Product> findByStatus(String status, Pageable pageable);
    java.util.List<Product> findBySellerEmail(String email);
    java.util.List<Product> findBySellerId(UUID sellerId);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.status = 'ACTIVE'")
    Page<Product> findByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id IN (SELECT c.id FROM Category c WHERE c.id = :catId OR c.parent.id = :catId) AND p.status = 'ACTIVE'")
    Page<Product> findByCategoryRecursive(@Param("catId") UUID catId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p JOIN ProductVariant v ON v.product = p WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:minPrice IS NULL OR v.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.price <= :maxPrice) AND " +
           "p.status = 'ACTIVE'")
    Page<Product> searchProductsFilter(
            @Param("query") String query, 
            @Param("minPrice") java.math.BigDecimal minPrice, 
            @Param("maxPrice") java.math.BigDecimal maxPrice, 
            Pageable pageable);
}
