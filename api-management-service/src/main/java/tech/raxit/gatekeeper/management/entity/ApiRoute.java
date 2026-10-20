package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(
        name = "api_routes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_api_routes_tenant_route",
                        columnNames = {"organization_id", "route_id"}
                )
        }
)
public class ApiRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_api_routes_organization"),
            referencedColumnName = "id"
    )
    private Organization organization;

    @Column(name = "route_id", nullable = false, length = 100)
    private String routeId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "path_pattern", nullable = false, length = 500)
    private String pathPattern;

    @Column(name = "target_base_url", nullable = false, length = 1000)
    private String targetBaseUrl;

    @Column(name = "path_prefix", nullable = false, length = 500)
    private String pathPrefix;

    @Column(name = "request_per_minute")
    private Integer requestPerMinute;

    @Column(name = "response_timeout_ms", nullable = false)
    private Integer responseTimeoutMs;

    @Column(name = "idempotency_enabled", nullable = false)
    private boolean idempotencyEnabled = false;

    @Column(nullable = false)
    private boolean active = false;

    @Column(name = "configuration_version", nullable = false)
    private Long configurationVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
