package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.ProductImage;
import com.ecommerce.platform.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByVariantOrderByDisplayOrderAsc(ProductVariant variant);
    void deleteByVariant(ProductVariant variant);
}
