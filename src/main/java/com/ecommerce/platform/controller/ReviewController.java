package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.ReviewRequest;
import com.ecommerce.platform.dto.ReviewResponse;
import com.ecommerce.platform.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.addReview(userDetails.getUsername(), request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, page, size));
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<Map<String, Object>> getProductSummary(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getProductRatingSummary(productId));
    }
}
