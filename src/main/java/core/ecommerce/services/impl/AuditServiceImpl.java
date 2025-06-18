package core.ecommerce.services.impl;

import core.ecommerce.entity.AuditLog;
import core.ecommerce.repository.AuditLogRepository;
import core.ecommerce.services.AuditService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    
    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(String entityName, String entityId, String operation, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .operation(operation)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
