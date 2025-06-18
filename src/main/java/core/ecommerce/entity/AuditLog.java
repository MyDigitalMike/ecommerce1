package core.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;

    private String operation;

    private String entityId;

    private String performedBy; // Correo u otro identificador del usuario

    private LocalDateTime timestamp;

    @Column(length = 2000)
    private String details;
}
