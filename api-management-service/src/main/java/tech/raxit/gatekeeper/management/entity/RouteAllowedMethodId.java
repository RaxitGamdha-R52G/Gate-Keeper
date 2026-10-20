package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RouteAllowedMethodId implements Serializable {

    @Column(name = "route_pk", nullable = false)
    private Long routePk;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;
}