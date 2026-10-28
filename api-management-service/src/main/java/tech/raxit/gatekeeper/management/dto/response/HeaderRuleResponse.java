package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.RouteHeaderRule;

public record HeaderRuleResponse(
        Long id,
        String direction,
        String action,
        String headerName,
        String headerValue
) {

    public static HeaderRuleResponse from(RouteHeaderRule rule) {
        return new HeaderRuleResponse(
                rule.getId(),
                rule.getDirection(),
                rule.getAction(),
                rule.getHeaderName(),
                rule.getHeaderValue()
        );
    }
}
