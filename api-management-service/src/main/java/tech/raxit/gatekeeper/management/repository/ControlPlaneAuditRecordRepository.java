package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.ControlPlaneAuditRecord;

public interface ControlPlaneAuditRecordRepository extends JpaRepository<ControlPlaneAuditRecord, Long> {
}
