package tech.raxit.gatekeeper.gateway.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tech.raxit.gatekeeper.gateway.service.GatewayConfigurationLoader;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationEventConsumer {

    private final GatewayConfigurationLoader gatewayConfigurationLoader;

    @KafkaListener(topics = "${gateway.kafka.configuration-topic}", groupId = "${gateway.kafka.configuration-group}")
    public void consumeConfigurationChange(String event) {
        log.info("Gateway configuration notification received: {}", event);

        gatewayConfigurationLoader.refresh();
    }
}
