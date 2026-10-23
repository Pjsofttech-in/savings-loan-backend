package com.pjsoft.saving_loan_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Locks a user id (per client address) for a while after too many wrong passwords. */
@Component
public class LoginAttemptService {

    private static final class Attempt {
        int failures;
        long lockedUntil;
        long lastFailure;
    }

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final int maxFailures;
    private final long lockSeconds;

    public LoginAttemptService(@Value("${app.auth.max-failed-attempts:5}") int maxFailures,
                               @Value("${app.auth.lock-minutes:15}") long lockMinutes) {
        this.maxFailures = Math.max(1, maxFailures);
        this.lockSeconds = Math.max(1, lockMinutes) * 60;
    }

    /** Seconds the caller must still wait, or 0 when sign-in is allowed. */
    public long secondsLocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return 0;
        }
        long remaining = attempt.lockedUntil - Instant.now().getEpochSecond();
        return Math.max(remaining, 0);
    }

    public void recordFailure(String key) {
        long now = Instant.now().getEpochSecond();
        if (attempts.size() > 10_000) {
            attempts.entrySet().removeIf(e -> now - e.getValue().lastFailure > lockSeconds && e.getValue().lockedUntil < now);
        }
        attempts.compute(key, (k, attempt) -> {
            Attempt current = attempt == null ? new Attempt() : attempt;
            if (now - current.lastFailure > lockSeconds) {
                current.failures = 0;
            }
            current.failures++;
            current.lastFailure = now;
            if (current.failures >= maxFailures) {
                current.lockedUntil = now + lockSeconds;
                current.failures = 0;
            }
            return current;
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }
}
