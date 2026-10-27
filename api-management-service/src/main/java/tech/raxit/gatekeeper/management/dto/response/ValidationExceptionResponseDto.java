package tech.raxit.gatekeeper.management.dto.response;

import java.time.Instant;
import java.util.Map;

public record ValidationExceptionResponseDto(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}