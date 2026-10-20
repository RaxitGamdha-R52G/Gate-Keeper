package tech.raxit.gatekeeper.management.controller;

import org.springframework.beans.factory.annotation.Value;
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

    private final HealthService healthService;

    @Value("${info.app.name}")
    private String appName;

    @Value("${info.app.description}")
    private String appDescription;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
                new HealthResponse(
                        appName,
                        appDescription,
                        "UP",
                        healthService.getHealth(),
                        LocalDateTime.now()
                )
        );
    }
}
