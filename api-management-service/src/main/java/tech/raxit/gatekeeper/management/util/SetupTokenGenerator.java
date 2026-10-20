
package tech.raxit.gatekeeper.management.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

@Component
public class SetupTokenGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    @Getter
    private final int tokenByteLength;
    private final int tokenCharacterLength;
    private final Pattern tokenFormatPattern;

    public SetupTokenGenerator(
            @Value("${gatekeeper.setup-token.length:32}")
            int tokenByteLength
    ) {

        this.tokenByteLength = tokenByteLength;
        this.tokenCharacterLength = (tokenByteLength * 8 + 5) / 6;
        this.tokenFormatPattern = Pattern.compile(
                "^[A-Za-z0-9_-]{" + tokenCharacterLength + "}$"
        );
    }

    public String generateToken() {
        byte[] tokenBytes = new byte[tokenByteLength];

        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    public String hashToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Setup token must not be blank");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable in this Java runtime",
                    exception);
        }
    }

    public boolean hasValidFormat(String rawToken) {
        return rawToken != null && tokenFormatPattern.matcher(rawToken).matches();
    }

    public int getTokenLength() {
        return tokenCharacterLength;
    }
}
