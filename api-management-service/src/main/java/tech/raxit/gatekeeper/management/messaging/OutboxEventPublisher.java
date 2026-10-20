package tech.raxit.gatekeeper.management.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tech.raxit.gatekeeper.management.entity.OutboxEvent;
import tech.raxit.gatekeeper.management.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;

    @Value("${gatekeeper.kafka.topics.organization-events}")
    private String organizationEventsTopic;

    @Scheduled(fixedDelayString = "${gatekeeper.outbox.poll-delay-ms:1000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findPendingEvents(PageRequest.of(0, 50));

        for (OutboxEvent event : events) {
            publishOne(event);
        }
    }

    private void publishOne(OutboxEvent event) {
        String topic = switch (event.getEventType()) {
            case "ORGANIZATION_STATUS_CHANGED" -> organizationEventsTopic;
            default -> null;
        };

        if (topic == null) {
            log.error(
                    "Unsupported outbox event type {} for event {}",
                    event.getEventType(),
                    event.getId()
            );
            recordFailure(event.getId(), "Unsupported event type");
            return;
        }

        try {
            kafkaTemplate.send(
                    topic,
                    event.getAggregateId(),
                    event.getPayload()
            ).get(10, TimeUnit.SECONDS);

            transactionTemplate.executeWithoutResult(status ->
                    outboxEventRepository.markPublished(
                            event.getId(),
                            LocalDateTime.now()
                    )
            );
        } catch (Exception exception) {
            log.error(
                    "Failed to publish outbox event {}",
                    event.getId(),
                    exception
            );
            recordFailure(event.getId(), exception.getClass().getSimpleName());
        }
    }

    private void recordFailure(Long eventId, String error) {
        transactionTemplate.executeWithoutResult(status ->
                outboxEventRepository.recordPublishFailure(
                        eventId,
                        error
                )
        );
    }
}
