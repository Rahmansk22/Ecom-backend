package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.AddressRequest;
import com.ecommerce.platform.dto.AddressResponse;
import com.ecommerce.platform.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.addAddress(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getAddresses(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.updateAddress(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id
    ) {
        addressService.deleteAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(addressService.setDefaultAddress(userDetails.getUsername(), id));
    }
}
