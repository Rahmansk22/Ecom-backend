package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.ProductDetailResponse;
import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.dto.ProductVariantDto;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Category;
import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.model.ProductImage;
import com.ecommerce.platform.model.ProductVariant;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.repository.ProductImageRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.repository.ProductVariantRepository;
import com.ecommerce.platform.service.ProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              ProductVariantRepository variantRepository, ProductImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String categorySlug, String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage;

        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            Category category = categoryRepository.findBySlug(categorySlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            productsPage = productRepository.findByCategoryRecursive(category.getId(), pageable);
        } else {
            productsPage = productRepository.findByStatus("ACTIVE", pageable);
        }

        List<ProductResponse> responseList = productsPage.getContent().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#slug")
    public ProductDetailResponse getProductDetail(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ProductVariant> variants = variantRepository.findByProduct(product);
        List<ProductVariantDto> variantDtos = variants.stream()
                .map(this::mapToVariantDto)
                .collect(Collectors.toList());

        return new ProductDetailResponse(
                product.getId(),
                product.getTitle(),
                product.getSlug(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                product.getCountryOfOrigin(),
                product.getHsnCode(),
                variantDtos
        );
    }

    private ProductResponse mapToSummaryResponse(Product product) {
        List<ProductVariant> variants = variantRepository.findByProduct(product);
        
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal compareAtPrice = null;
        String primaryImage = null;

        if (!variants.isEmpty()) {
            // Find lowest price
            ProductVariant cheapest = variants.get(0);
            for (ProductVariant v : variants) {
                if (v.getPrice().compareTo(cheapest.getPrice()) < 0) {
                    cheapest = v;
                }
            }
            minPrice = cheapest.getPrice();
            compareAtPrice = cheapest.getCompareAtPrice();

            // Fetch primary image of cheapest variant (or first variant)
            List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(cheapest);
            if (images.isEmpty() && cheapest != variants.get(0)) {
                images = imageRepository.findByVariantOrderByDisplayOrderAsc(variants.get(0));
            }
            if (!images.isEmpty()) {
                primaryImage = images.get(0).getImageUrl();
            }
        }

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getSlug(),
                product.getBrand(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                minPrice,
                compareAtPrice,
                primaryImage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.searchProductsFilter(query, minPrice, maxPrice, pageable);
        
        List<ProductResponse> responseList = productsPage.getContent().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, productsPage.getTotalElements());
    }

    private ProductVariantDto mapToVariantDto(ProductVariant variant) {
        List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(variant);
        List<String> imageUrls = images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return new ProductVariantDto(
                variant.getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getCompareAtPrice(),
                variant.getStock(),
                variant.getAttributesJson(),
                imageUrls
        );
    }
}
