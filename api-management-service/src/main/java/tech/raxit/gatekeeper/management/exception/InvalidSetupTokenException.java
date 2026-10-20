package tech.raxit.gatekeeper.management.exception;

import java.io.Serial;

public final class InvalidSetupTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidSetupTokenException(String message) {
        super(message);
    }
}
