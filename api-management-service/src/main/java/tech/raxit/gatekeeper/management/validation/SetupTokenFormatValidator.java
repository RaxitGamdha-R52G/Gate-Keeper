package tech.raxit.gatekeeper.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.raxit.gatekeeper.management.util.SetupTokenGenerator;

@Component
@RequiredArgsConstructor
public class SetupTokenFormatValidator
        implements ConstraintValidator<ValidSetupTokenFormat, String> {

    private final SetupTokenGenerator setupTokenGenerator;

    @Override
    public boolean isValid(String token, ConstraintValidatorContext context) {
        return setupTokenGenerator.hasValidFormat(token);
    }
}