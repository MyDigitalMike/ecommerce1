package core.ecommerce.services;

public interface AuditService {

    void log(String entityName, String entityId, String operation, String performedBy, String details);
}
