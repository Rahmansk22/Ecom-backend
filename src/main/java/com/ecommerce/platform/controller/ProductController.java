package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.ProductDetailResponse;
import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(productService.getProducts(category, brand, page, size));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductDetail(slug));
    }
}
