package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.ControlPlaneAuditRecord;

import java.time.LocalDateTime;

public record AuditRecordResponse(
        Long id,
        String actor,
        String action,
        String resourceType,
        String resourceId,
        String correlationId,
        long durationMs,
        String result,
        String details,
        LocalDateTime createdAt
) {

    public static AuditRecordResponse from(ControlPlaneAuditRecord record) {
        return new AuditRecordResponse(
                record.getId(),
                record.getActor(),
                record.getAction(),
                record.getResourceType(),
                record.getResourceId(),
                record.getCorrelationId(),
                record.getDurationMs(),
                record.getResult(),
                record.getDetails(),
                record.getCreatedAt()
        );
    }
}
