package tg.ngstars.notification.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailAuditLogger {

    private static final Logger log = LoggerFactory.getLogger(EmailAuditLogger.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final Path auditFile;

    public EmailAuditLogger(@Value("${notification.audit-dir:./audit}") String auditDir) {
        var dir = Path.of(auditDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Impossible de créer le dossier d'audit: {}", dir, e);
        }
        this.auditFile = dir.resolve("emails-sent.log");
    }

    public void logSent(String to, String subject, String template, String status) {
        var line = "%s | %s | %s | %s | %s%n"
                .formatted(
                        FORMATTER.format(OffsetDateTime.now()),
                        to,
                        subject != null ? subject : "",
                        template != null ? template : "",
                        status);

        try {
            Files.writeString(auditFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Échec écriture audit email: {}", e.getMessage());
        }

        log.info("EMAIL_AUDIT: to={}, subject={}, template={}, status={}", to, subject, template, status);
    }

    public void logFailed(String to, String subject, String template, String error) {
        logSent(to, subject, template, "FAILED: " + error);
    }
}
