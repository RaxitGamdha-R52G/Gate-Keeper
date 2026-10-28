package tech.raxit.gatekeeper.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.raxit.gatekeeper.management.dto.response.RouteResponse;
import tech.raxit.gatekeeper.management.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/gateway/routes")
@RequiredArgsConstructor
public class GatewayConfigurationController {

    private final RouteService routeService;


    @GetMapping
    public List<RouteResponse> getActiveRoutes() {
        return routeService.findAllActiveForGateway();
    }
}