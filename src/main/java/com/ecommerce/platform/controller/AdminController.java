package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.UserResponse;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Order;
import com.ecommerce.platform.model.PaymentStatus;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.OrderRepository;
import com.ecommerce.platform.repository.ReviewRepository;
import com.ecommerce.platform.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final JobLauncher jobLauncher;
    private final Job salesExportJob;

    public AdminController(UserRepository userRepository,
                           OrderRepository orderRepository,
                           ReviewRepository reviewRepository,
                           JobLauncher jobLauncher,
                           Job salesExportJob) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.jobLauncher = jobLauncher;
        this.salesExportJob = salesExportJob;
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        List<Order> orders = orderRepository.findAll();
        List<User> users = userRepository.findAll();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(Order::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeUsers = users.stream().filter(User::isActive).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("ordersCount", orders.size());
        stats.put("usersCount", users.size());
        stats.put("activeUsersCount", activeUsers);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> list = userRepository.findAll().stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getEmail(),
                        u.getMobile(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getProfilePictureUrl(),
                        u.getRole(),
                        u.isEmailVerified(),
                        u.isPhoneVerified(),
                        u.isActive()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/users/{userId}/status")
    @Transactional
    public ResponseEntity<Void> toggleUserStatus(@PathVariable UUID userId, @RequestParam boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(active);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Transactional
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reports/sales")
    public ResponseEntity<Map<String, String>> triggerSalesReport() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(salesExportJob, jobParameters);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Sales export job executed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Batch job execution failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/reports/sales/download")
    public ResponseEntity<Resource> downloadSalesReport() {
        File file = new File("exports/latest_sales_report.csv");
        if (!file.exists()) {
            throw new ResourceNotFoundException("Report file has not been generated yet. Please trigger generation first.");
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"latest_sales_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
