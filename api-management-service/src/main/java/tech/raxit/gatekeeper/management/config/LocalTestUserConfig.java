
package tech.raxit.gatekeeper.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local-test")
public class LocalTestUserConfig {

    @Bean
    public UserDetailsService localTestUserDetailsService(
            PasswordEncoder passwordEncoder) {

        String username = System.getenv("GATEKEEPER_TEST_ADMIN_USERNAME");
        String password = System.getenv("GATEKEEPER_TEST_ADMIN_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Set GATEKEEPER_TEST_ADMIN_PASSWORD for local testing"
            );
        }

        var testAdmin = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("SUPER_ADMIN")
                .build();

        return new InMemoryUserDetailsManager(testAdmin);
    }
}
