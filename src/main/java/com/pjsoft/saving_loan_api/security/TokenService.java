package com.pjsoft.saving_loan_api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Issues and verifies signed, expiring login tokens (HMAC-SHA256).
 * Token = base64url(payload) + "." + base64url(signature)
 * payload = ROLE|memberId|base64url(name)|expiryEpochSeconds
 */
@Component
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final String HMAC = "HmacSHA256";

    private final byte[] secret;
    private final long ttlSeconds;

    public TokenService(@Value("${app.auth.secret:}") String configuredSecret,
                        @Value("${app.auth.token-minutes:60}") long tokenMinutes) {
        if (configuredSecret == null || configuredSecret.trim().length() < 32) {
            byte[] random = new byte[48];
            new SecureRandom().nextBytes(random);
            this.secret = random;
            log.warn("AUTH_SECRET is missing or shorter than 32 characters - using a random secret. "
                    + "All users will be signed out whenever the backend restarts. Set AUTH_SECRET in production.");
        } else {
            this.secret = configuredSecret.trim().getBytes(StandardCharsets.UTF_8);
        }
        this.ttlSeconds = Math.max(1, tokenMinutes) * 60;
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }

    public String issue(AuthUser user) {
        long expiry = Instant.now().getEpochSecond() + ttlSeconds;
        String name = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((user.name() == null ? "" : user.name()).getBytes(StandardCharsets.UTF_8));
        String payload = user.role() + "|" + (user.memberId() == null ? "" : user.memberId()) + "|" + name + "|" + expiry;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    public Optional<AuthUser> verify(String token) {
        try {
            if (token == null) {
                return Optional.empty();
            }
            int dot = token.indexOf('.');
            if (dot <= 0 || dot == token.length() - 1) {
                return Optional.empty();
            }
            String encodedPayload = token.substring(0, dot);
            String signature = token.substring(dot + 1);
            byte[] expected = sign(encodedPayload).getBytes(StandardCharsets.UTF_8);
            if (!MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }
            String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] parts = payload.split("\\|", -1);
            if (parts.length != 4) {
                return Optional.empty();
            }
            if (Instant.now().getEpochSecond() >= Long.parseLong(parts[3])) {
                return Optional.empty();
            }
            String role = parts[0];
            if (!AuthUser.ADMIN.equals(role) && !AuthUser.MEMBER.equals(role)) {
                return Optional.empty();
            }
            Long memberId = parts[1].isEmpty() ? null : Long.valueOf(parts[1]);
            String name = new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8);
            return Optional.of(new AuthUser(role, memberId, name));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(secret, HMAC));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign token", ex);
        }
    }
}
