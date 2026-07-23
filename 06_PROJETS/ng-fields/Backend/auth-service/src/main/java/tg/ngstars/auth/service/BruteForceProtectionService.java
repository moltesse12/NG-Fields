package tg.ngstars.auth.service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.model.FailedLoginAttempt;
import tg.ngstars.auth.repository.FailedLoginAttemptRepository;

@Service
public class BruteForceProtectionService {

    private static final Logger log = LoggerFactory.getLogger(BruteForceProtectionService.class);

    private final FailedLoginAttemptRepository failedLoginAttemptRepository;

    @Value("${app.security.lockout.max-attempts:10}")
    private int maxAttempts;

    @Value("${app.security.lockout.window-minutes:15}")
    private int windowMinutes;

    @Value("${app.security.lockout.duration-minutes:30}")
    private int lockoutDurationMinutes;

    @Value("${app.security.lockout.ip-max-attempts:20}")
    private int ipMaxAttempts;

    public BruteForceProtectionService(FailedLoginAttemptRepository failedLoginAttemptRepository) {
        this.failedLoginAttemptRepository = failedLoginAttemptRepository;
    }

    public boolean isLockedOut(String username) {
        var lastLockout = failedLoginAttemptRepository.findTop20ByUsernameOrderByAttemptedAtDesc(username)
            .stream()
            .filter(a -> a.getLockedUntil() != null)
            .findFirst();

        if (lastLockout.isPresent() && lastLockout.get().getLockedUntil().isAfter(OffsetDateTime.now())) {
            long remainingMinutes = ChronoUnit.MINUTES.between(OffsetDateTime.now(), lastLockout.get().getLockedUntil());
            log.warn("Account locked: username={}, remainingMinutes={}", username, remainingMinutes);
            return true;
        }

        long failedCount = failedLoginAttemptRepository
            .countByUsernameAndSuccessfulFalseAndAttemptedAtAfter(username, OffsetDateTime.now().minusMinutes(windowMinutes));

        if (failedCount >= maxAttempts) {
            lockAccount(username);
            return true;
        }

        return false;
    }

    public boolean isIpBlocked(String ipAddress) {
        long failedCount = failedLoginAttemptRepository
            .countByIpAddressAndSuccessfulFalseAndAttemptedAtAfter(ipAddress, OffsetDateTime.now().minusMinutes(windowMinutes));
        return failedCount >= ipMaxAttempts;
    }

    public void recordFailedAttempt(String username, String ipAddress) {
        var attempt = new FailedLoginAttempt(username, ipAddress, false);
        failedLoginAttemptRepository.save(attempt);

        long failedCount = failedLoginAttemptRepository
            .countByUsernameAndSuccessfulFalseAndAttemptedAtAfter(username, OffsetDateTime.now().minusMinutes(windowMinutes));

        if (failedCount >= maxAttempts) {
            lockAccount(username);
        }

        log.debug("Failed login attempt: username={}, ipAddress={}, totalFailed={}", username, ipAddress, failedCount);
    }

    public void recordSuccessfulAttempt(String username, String ipAddress) {
        var attempt = new FailedLoginAttempt(username, ipAddress, true);
        failedLoginAttemptRepository.save(attempt);
    }

    private void lockAccount(String username) {
        var lastAttempt = failedLoginAttemptRepository.findTop20ByUsernameOrderByAttemptedAtDesc(username)
            .stream()
            .findFirst();

        if (lastAttempt.isPresent()) {
            lastAttempt.get().setLockedUntil(OffsetDateTime.now().plusMinutes(lockoutDurationMinutes));
            failedLoginAttemptRepository.save(lastAttempt.get());
            log.warn("Account locked for {} minutes: username={}", lockoutDurationMinutes, username);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldAttempts() {
        var cutoff = OffsetDateTime.now().minusDays(7);
        failedLoginAttemptRepository.deleteOlderThan(cutoff);
        log.info("Cleaned up failed login attempts older than 7 days");
    }
}
