package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(
        name = "route_configuration_versions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_route_versions_tenant_version",
                columnNames = {
                        "organization_id",
                        "route_id",
                        "version_number"
                }
        )
)
public class RouteConfigurationVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_route_versions_organization")
    )
    private Organization organization;

    @Column(name = "route_id", nullable = false, length = 100)
    private String routeId;

    @Column(name = "version_number", nullable = false)
    private Long versionNumber;

    @Column(name = "change_type", nullable = false, length = 30)
    private String changeType;

    @Column(
            name = "configuration_snapshot",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String configurationSnapshot;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}