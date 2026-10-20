package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.raxit.gatekeeper.management.dto.request.setup.CompletePasswordSetupRequest;
import tech.raxit.gatekeeper.management.service.ManagementUserPasswordSetupService;

@RestController
@RequestMapping("/api/v1/management-users")
@RequiredArgsConstructor
public class ManagementUserController {

    private final ManagementUserPasswordSetupService passwordSetupService;

    @PostMapping("/complete-setup")
    public ResponseEntity<Void> completeSetup(
            @Valid @RequestBody CompletePasswordSetupRequest request
    ) {
        passwordSetupService.completeSetup(
                request.token(),
                request.newPassword()
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
