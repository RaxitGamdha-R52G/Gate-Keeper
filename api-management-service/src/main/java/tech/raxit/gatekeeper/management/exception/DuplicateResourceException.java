package tech.raxit.gatekeeper.management.exception;

import java.io.Serial;

public final class DuplicateResourceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }
}
