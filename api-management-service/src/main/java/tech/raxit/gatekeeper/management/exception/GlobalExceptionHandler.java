package tech.raxit.gatekeeper.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.raxit.gatekeeper.management.dto.response.ExceptionResponseDto;
import tech.raxit.gatekeeper.management.dto.response.ValidationExceptionResponseDto;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponseDto handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return new ValidationExceptionResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseDto> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return generateExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid or contains an unsupported value.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponseDto> handleApiExceptions(
            ApiException exception,
            HttpServletRequest request
    ) {
        return generateExceptionResponse(
                exception.getStatus(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    private ResponseEntity<ExceptionResponseDto> generateExceptionResponse(
            HttpStatus status,
            String message,
            String requestURI
    ) {
        ExceptionResponseDto response = new ExceptionResponseDto(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                requestURI
        );
        return ResponseEntity.status(status).body(response);
    }
}
