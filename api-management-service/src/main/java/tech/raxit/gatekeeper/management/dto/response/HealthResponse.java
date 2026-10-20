package tech.raxit.gatekeeper.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HealthResponse {
    private String appName;
    private String appDescription;
    private String serviceStatus;
    private String dbStatus;
    private LocalDateTime timestamp;
}
