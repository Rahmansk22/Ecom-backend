package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.model.ProductImage;
import com.ecommerce.platform.model.ProductVariant;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.model.Wishlist;
import com.ecommerce.platform.repository.ProductImageRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.repository.ProductVariantRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.repository.WishlistRepository;
import com.ecommerce.platform.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, UserRepository userRepository,
                               ProductRepository productRepository, ProductVariantRepository variantRepository,
                               ProductImageRepository imageRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
    }

    private Wishlist getOrCreateWishlist(String email) {
        return wishlistRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Wishlist wishlist = new Wishlist(user);
                    return wishlistRepository.save(wishlist);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getWishlist(String email) {
        Wishlist wishlist = getOrCreateWishlist(email);
        return wishlist.getProducts().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProductResponse> addProductToWishlist(String email, UUID productId) {
        Wishlist wishlist = getOrCreateWishlist(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        wishlist.getProducts().add(product);
        wishlistRepository.save(wishlist);
        return wishlist.getProducts().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProductResponse> removeProductFromWishlist(String email, UUID productId) {
        Wishlist wishlist = getOrCreateWishlist(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        wishlist.getProducts().remove(product);
        wishlistRepository.save(wishlist);
        return wishlist.getProducts().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(String email, UUID productId) {
        return wishlistRepository.findByUserEmail(email)
                .map(wishlist -> wishlist.getProducts().stream().anyMatch(p -> p.getId().equals(productId)))
                .orElse(false);
    }

    private ProductResponse mapToSummaryResponse(Product product) {
        List<ProductVariant> variants = variantRepository.findByProduct(product);
        
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal compareAtPrice = null;
        String primaryImage = null;

        if (!variants.isEmpty()) {
            ProductVariant cheapest = variants.get(0);
            for (ProductVariant v : variants) {
                if (v.getPrice().compareTo(cheapest.getPrice()) < 0) {
                    cheapest = v;
                }
            }
            minPrice = cheapest.getPrice();
            compareAtPrice = cheapest.getCompareAtPrice();

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
}
