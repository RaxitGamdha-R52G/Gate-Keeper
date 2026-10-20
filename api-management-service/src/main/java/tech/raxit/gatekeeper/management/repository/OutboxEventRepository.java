package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.raxit.gatekeeper.management.entity.OutboxEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    @Query("""
            SELECT event
            FROM OutboxEvent event
            WHERE event.publishedAt IS NULL
            ORDER BY event.id ASC
            """)
    List<OutboxEvent> findPendingEvents(Pageable pageable);

    @Modifying
    @Query("""
            UPDATE OutboxEvent event
            SET event.publishedAt = :publishedAt,
                event.lastError = NULL
            WHERE event.id = :eventId
            """)
    int markPublished(
            @Param("eventId") Long eventId,
            @Param("publishedAt") LocalDateTime publishedAt
    );

    @Modifying
    @Query("""
            UPDATE OutboxEvent event
            SET event.attempts = event.attempts + 1,
                event.lastError = :lastError
            WHERE event.id = :eventId
            """)
    int recordPublishFailure(
            @Param("eventId") Long eventId,
            @Param("lastError") String lastError
    );
}