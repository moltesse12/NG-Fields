package tg.ngstars.auth.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.model.AuditLog;
import tg.ngstars.auth.repository.AuditLogRepository;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, String action, String resource, String resourceId, String details, String ipAddress) {
        if (userId == null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                try {
                    userId = UUID.fromString(jwt.getSubject());
                } catch (IllegalArgumentException e) {
                    log.warn("Cannot parse JWT subject as UUID: {}", jwt.getSubject());
                }
            }
        }
        var auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLogRepository.save(auditLog);
    }
}
