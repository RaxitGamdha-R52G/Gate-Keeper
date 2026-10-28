package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.ControlPlaneAuditRecord;

import java.util.List;

public interface ControlPlaneAuditRecordRepository extends JpaRepository<ControlPlaneAuditRecord, Long> {

    List<ControlPlaneAuditRecord> findAllByOrganizationIdOrderByCreatedAtDesc(Long id);
}
