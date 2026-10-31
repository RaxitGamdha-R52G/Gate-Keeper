package tech.raxit.gatekeeper.management.dto.response;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ValidationExceptionResponseDto extends ExceptionResponseDto {
    private final Map<String, String> fieldErrors;

    public ValidationExceptionResponseDto(Instant timestamp, int status, String error, String message, String path, Map<String, String> fieldErrors) {
        super(timestamp, status, error, message, path);
        this.fieldErrors = fieldErrors;
    }
}