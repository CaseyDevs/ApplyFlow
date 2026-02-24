package com.casey.applyflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final RsaKeyProperties rsaKeys;

    public SecurityConfiguration(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())  // Enable CORS in Security filter chain
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll();  // Public auth endpoints
                    auth.requestMatchers("/h2-console/**").permitAll(); // H2 console (dev only)
                    auth.anyRequest().authenticated();  // Everything else requires auth
                })
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No sessions
                .oauth2ResourceServer(oauth2 -> 
                    oauth2
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(Customizer.withDefaults()))  // Enable JWT validation
                .headers(headers -> 
                    headers.frameOptions(frame -> frame.sameOrigin()))  // For H2 console
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Read token from cookie
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new BearerTokenResolver() {
            @Override
            public String resolve(HttpServletRequest request) {
                if (request.getCookies() == null) return null;
                for (Cookie c : request.getCookies()) {
                    if ("ACCESS_TOKEN".equals(c.getName())) {
                        return c.getValue();
                    }
                }
                return null;
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Decodes and validates incoming JWTs using the public key
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // Encodes (signs) JWTs using the private key
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey())
                .privateKey(rsaKeys.privateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
