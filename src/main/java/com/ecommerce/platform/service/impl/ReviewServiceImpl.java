package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.ReviewRequest;
import com.ecommerce.platform.dto.ReviewResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.model.Review;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.repository.ReviewRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewResponse addReview(String email, ReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Enforce single review constraint
        if (reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())) {
            throw new BadRequestException("You have already submitted a review for this product");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(UUID productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewsPage = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);

        List<ReviewResponse> responseList = reviewsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, reviewsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductRatingSummary(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);

        int totalReviews = reviews.size();
        double averageRating = 0.0;
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        if (totalReviews > 0) {
            double sum = 0;
            for (Review r : reviews) {
                sum += r.getRating();
                distribution.put(r.getRating(), distribution.get(r.getRating()) + 1);
            }
            averageRating = sum / totalReviews;
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
        summary.put("totalReviews", totalReviews);
        summary.put("distribution", distribution);
        return summary;
    }

    private ReviewResponse mapToResponse(Review review) {
        String reviewerName = review.getUser().getFirstName() + " " + 
                (review.getUser().getLastName() != null ? review.getUser().getLastName() : "");
        return new ReviewResponse(
                review.getId(),
                reviewerName.trim(),
                review.getRating(),
                review.getTitle(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
