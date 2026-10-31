package tech.raxit.gatekeeper.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ExceptionResponseDto {
    protected final Instant timestamp;
    protected final int status;
    protected final String error;
    protected final String message;
    protected final String path;
}

