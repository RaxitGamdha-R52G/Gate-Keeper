package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.RouteAllowedMethod;
import tech.raxit.gatekeeper.management.entity.RouteAllowedMethodId;

public interface RouteAllowedMethodRepository extends JpaRepository<RouteAllowedMethod, RouteAllowedMethodId> {
}
