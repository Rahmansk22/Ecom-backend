package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.ReviewRequest;
import com.ecommerce.platform.dto.ReviewResponse;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse addReview(String email, ReviewRequest request);
    Page<ReviewResponse> getProductReviews(UUID productId, int page, int size);
    Map<String, Object> getProductRatingSummary(UUID productId);
}
