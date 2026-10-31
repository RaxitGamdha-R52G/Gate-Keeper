package tech.raxit.gatekeeper.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tech.raxit.gatekeeper.gateway.client.ManagementApiClient;
import tech.raxit.gatekeeper.gateway.dto.GatewayRoute;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayConfigurationLoader implements ApplicationRunner {
    private final ManagementApiClient managementApiClient;

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        refresh();
    }

    public void refresh() {
        List<GatewayRoute> routes = managementApiClient.fetchActiveRoutes();

        routes.forEach(r -> log.info("Route: {}", r.getTargetPathPrefix()));
    }
}
