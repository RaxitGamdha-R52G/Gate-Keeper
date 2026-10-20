package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tech.raxit.gatekeeper.management.entity.enums.HeaderAction;
import tech.raxit.gatekeeper.management.entity.enums.HeaderDirection;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "route_header_rules")
public class RouteHeaderRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "route_pk",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_route_header_rules_route")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApiRoute route;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeaderDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeaderAction action;

    @Column(name = "header_name", nullable = false)
    private String headerName;

    @Column(name = "header_value", length = 2000)
    private String headerValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
