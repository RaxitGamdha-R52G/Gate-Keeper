package tech.raxit.gatekeeper.management.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.raxit.gatekeeper.management.entity.ControlPlaneAuditRecord;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.repository.ControlPlaneAuditRecordRepository;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ControlPlaneAuditRecordRepository controlPlaneAuditRecordRepository;

    @Transactional
    public void record(Organization organization, String action,
                       String resourceType, String resourceId,
                       long startedAt, String details) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String actor = authentication == null ? "system" : authentication.getName();

        ControlPlaneAuditRecord record = new ControlPlaneAuditRecord();

        record.setOrganization(organization);
        record.setActor(actor);
        record.setAction(action);
        record.setResourceType(resourceType);
        record.setResourceId(resourceId);
        record.setCorrelationId(getCorrelationId());
        record.setDurationMs((System.nanoTime() - startedAt) / 1000000);
        record.setResult("SUCCESS");
        record.setDetails(details);

        controlPlaneAuditRecordRepository.save(record);
    }

    private String getCorrelationId() {
        if (!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getHeader("X-Correlation-ID");
    }
}
