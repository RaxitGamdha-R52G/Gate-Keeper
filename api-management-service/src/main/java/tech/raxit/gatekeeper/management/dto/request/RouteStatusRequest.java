package tech.raxit.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotNull;

public record RouteStatusRequest(
        @NotNull(message = "Route status is required")
        Boolean active
) {
}
