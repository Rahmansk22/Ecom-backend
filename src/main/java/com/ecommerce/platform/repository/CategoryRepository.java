package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();
}
