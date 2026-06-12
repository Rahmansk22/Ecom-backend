package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.UserResponse;
import com.ecommerce.platform.model.AuditLog;
import com.ecommerce.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String mobile = request.get("mobile");
        String profilePictureUrl = request.get("profilePictureUrl");

        return ResponseEntity.ok(userService.updateProfile(
                userDetails.getUsername(),
                firstName,
                lastName,
                mobile,
                profilePictureUrl
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        userService.changePassword(userDetails.getUsername(), oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateAccount(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateAccount(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activities")
    public ResponseEntity<List<AuditLog>> getActivityLog(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getActivityLog(userDetails.getUsername(), page, size));
    }

    @PostMapping("/become-seller")
    public ResponseEntity<UserResponse> becomeSeller(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.registerAsSeller(userDetails.getUsername()));
    }
}
