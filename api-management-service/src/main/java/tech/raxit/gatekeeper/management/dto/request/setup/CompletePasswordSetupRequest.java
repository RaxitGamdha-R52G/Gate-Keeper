package tech.raxit.gatekeeper.management.dto.request.setup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tech.raxit.gatekeeper.management.validation.ValidSetupTokenFormat;

public record CompletePasswordSetupRequest(

        @NotBlank(message = "Setup token is required")
        @ValidSetupTokenFormat
        String token,

        @NotBlank(message = "Password is required")
        @Size(
                min = 12,
                max = 72,
                message = "Password must contain between 12 and 72 characters"
        )
        String newPassword

) {
}
