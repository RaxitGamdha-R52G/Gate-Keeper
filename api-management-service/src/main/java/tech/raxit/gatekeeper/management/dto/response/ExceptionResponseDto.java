package tech.raxit.gatekeeper.management.dto.response;

import java.time.Instant;

public record ExceptionResponseDto(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}

