package core.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import core.ecommerce.entity.AuditLog;

public interface AuditLogRepository  extends JpaRepository<AuditLog, Long> {
}
