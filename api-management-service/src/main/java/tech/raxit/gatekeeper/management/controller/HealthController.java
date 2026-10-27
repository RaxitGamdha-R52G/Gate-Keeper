package tech.raxit.gatekeeper.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.raxit.gatekeeper.management.dto.response.HealthResponse;
import tech.raxit.gatekeeper.management.service.HealthService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
public class HealthController {

    public final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        String dbStatus = healthService.getHealth();
        return ResponseEntity.ok(
                new HealthResponse("UP", dbStatus, LocalDateTime.now()));
    }
}