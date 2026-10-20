package tech.raxit.gatekeeper.management.dto.request.organization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record OrganizationRegistrationRequest(

        @Valid
        @NotNull
        OrganizationDetails organization,

        @Valid
        @NotNull
        InitialUserDetails initialUser

) {}
