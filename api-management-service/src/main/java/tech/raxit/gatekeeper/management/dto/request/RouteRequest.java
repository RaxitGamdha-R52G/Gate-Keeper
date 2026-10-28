package tech.raxit.gatekeeper.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public record RouteRequest(

        @NotBlank(message = "Route ID is required")
        @Size(max = 100, message = "Route ID must not exceed 100 characters")
        String routeId,

        @NotBlank(message = "Route name is required")
        @Size(max = 200, message = "Route name must not exceed 200 characters")
        String name,

        @NotBlank(message = "Path pattern is required")
        @Size(max = 500, message = "Path pattern must not exceed 500 characters")
        String pathPattern,

        @NotBlank(message = "Target base URL is required")
        @Size(max = 1000, message = "Target base URL must not exceed 1000 characters")
        String targetBaseUrl,

        @NotBlank(message = "Target path prefix is required")
        @Size(max = 500, message = "Target path prefix must not exceed 500 characters")
        String targetPathPrefix,

        @Min(value = 1, message = "Requests per minute must be at least 1")
        Integer requestsPerMinute,

        @NotNull(message = "Response timeout is required")
        @Min(value = 1, message = "Response timeout must be at least 1 millisecond")
        Integer responseTimeoutMs,

        boolean idempotencyEnabled,

        boolean active,

        @NotEmpty(message = "At least one allowed HTTP method must be specified")
        Set<@NotBlank(message = "HTTP method must not be blank") String> allowedMethods,

        @NotNull(message = "Header rules must not be null")
        List<@NotNull(message = "Header rule must not be null") @Valid HeaderRuleRequest> headerRules
) {
}