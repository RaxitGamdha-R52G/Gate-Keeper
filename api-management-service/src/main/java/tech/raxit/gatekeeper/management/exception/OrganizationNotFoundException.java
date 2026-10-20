package tech.raxit.gatekeeper.management.exception;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(Long organizationId) {
        super("Organization not found: " + organizationId);
    }
}
