package tech.raxit.gatekeeper.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain monitoringSecurityFilterChain(
            HttpSecurity httpSecurity) {

        httpSecurity
                .securityMatcher("/health")
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain accountSetupSecurityFilterChain(
            HttpSecurity httpSecurity) {

        httpSecurity
                .securityMatcher("/api/v1/management-users/complete-setup")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        HttpMethod.POST
                                ).permitAll()
                                .anyRequest().denyAll()
                );

        return httpSecurity.build();

    }

    @Bean
    @Order(3)
    public SecurityFilterChain managementApiSecurityFilterChain(
            HttpSecurity httpSecurity) {

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
