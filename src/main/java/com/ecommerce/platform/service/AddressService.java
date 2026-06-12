package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.AddressRequest;
import com.ecommerce.platform.dto.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse addAddress(String email, AddressRequest request);
    List<AddressResponse> getAddresses(String email);
    AddressResponse updateAddress(String email, UUID addressId, AddressRequest request);
    void deleteAddress(String email, UUID addressId);
    AddressResponse setDefaultAddress(String email, UUID addressId);
}
