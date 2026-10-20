package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.RouteHeaderRules;

public interface RouteHeaderRulesRepository extends JpaRepository<RouteHeaderRules, Long> {
}
