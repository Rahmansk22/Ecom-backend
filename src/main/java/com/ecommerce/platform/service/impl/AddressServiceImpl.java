package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.AddressRequest;
import com.ecommerce.platform.dto.AddressResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Address;
import com.ecommerce.platform.model.AuditLog;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.AddressRepository;
import com.ecommerce.platform.repository.AuditLogRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository,
                              AuditLogRepository auditLogRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String email, AddressRequest request) {
        log.info("Adding address for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.isDefault()) {
            resetDefaultAddress(user);
        }

        Address address = Address.builder()
                .user(user)
                .label(request.label())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .pincode(request.pincode())
                .isDefault(request.isDefault())
                .build();

        Address savedAddress = addressRepository.save(address);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("ADDRESS_ADD")
                .details("Added new address: " + savedAddress.getLabel())
                .build());

        return mapToResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddresses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, UUID addressId, AddressRequest request) {
        log.info("Updating address {} for user: {}", addressId, email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access to address");
        }

        if (request.isDefault() && !address.isDefault()) {
            resetDefaultAddress(user);
        }

        address.setLabel(request.label());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setPincode(request.pincode());
        address.setDefault(request.isDefault());

        Address updatedAddress = addressRepository.save(address);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("ADDRESS_UPDATE")
                .details("Updated address details for: " + updatedAddress.getLabel())
                .build());

        return mapToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, UUID addressId) {
        log.info("Deleting address {} for user: {}", addressId, email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access to address");
        }

        addressRepository.delete(address);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("ADDRESS_DELETE")
                .details("Deleted address label: " + address.getLabel())
                .build());
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(String email, UUID addressId) {
        log.info("Setting default address {} for user: {}", addressId, email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access to address");
        }

        resetDefaultAddress(user);
        address.setDefault(true);
        Address savedAddress = addressRepository.save(address);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("ADDRESS_SET_DEFAULT")
                .details("Set address as default: " + savedAddress.getLabel())
                .build());

        return mapToResponse(savedAddress);
    }

    private void resetDefaultAddress(User user) {
        Optional<Address> currentDefault = addressRepository.findByUserAndIsDefaultTrue(user);
        currentDefault.ifPresent(address -> {
            address.setDefault(false);
            addressRepository.save(address);
        });
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPincode(),
                address.isDefault()
        );
    }
}
