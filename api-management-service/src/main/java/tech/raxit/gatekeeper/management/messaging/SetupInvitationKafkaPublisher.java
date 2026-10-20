package tech.raxit.gatekeeper.management.messaging;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tech.raxit.gatekeeper.management.event.setup.SetupInvitationRequested;

@Component
public class SetupInvitationKafkaPublisher {

    public static final Logger log = LoggerFactory.getLogger(SetupInvitationKafkaPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public SetupInvitationKafkaPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${gatekeeper.kafka.topics.setup-invitations}")
            String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(SetupInvitationRequested event) {
        kafkaTemplate.send(
                topic,
                event.managementUserId().toString(),
                event
        ).whenComplete((result, failure) -> {
            if (failure != null) {
                log.error(
                        "Failed to publish setup invitation event for managementUserId={}",
                        event.managementUserId(),
                        failure
                );
                return;
            }

            RecordMetadata metadata = result.getRecordMetadata();

            log.debug(
                    "Published setup invitation event: userId={}, topic={}, partition={}, offset={}",
                    event.managementUserId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        });
    }
}
