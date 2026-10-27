package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.ManagementUser;

public record TenantAdminResponse(
        Long id,
        Long organizationId,
        String username,
        boolean enabled
) {
    public static TenantAdminResponse from(ManagementUser user) {
        return new TenantAdminResponse(
                user.getId(),
                user.getOrganization().getId(),
                user.getUsername(),
                user.isEnabled()
        );
    }
}
