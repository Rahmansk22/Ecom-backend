package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.AuditLog;
import com.ecommerce.platform.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
