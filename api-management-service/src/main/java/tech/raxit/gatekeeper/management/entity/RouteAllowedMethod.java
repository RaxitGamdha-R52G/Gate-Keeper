package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "route_allowed_methods")
public class RouteAllowedMethod {

    @EmbeddedId
    private RouteAllowedMethodId id;

    @MapsId("routePk")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "route_pk",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_route_allowed_methods_route"
            )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApiRoute route;
}