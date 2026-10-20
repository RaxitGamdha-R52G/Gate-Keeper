package tech.raxit.gatekeeper.analytics.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HealthResponse {
    private String serviceName;
    private String serviceDescription;
    private String appStatus;
}
