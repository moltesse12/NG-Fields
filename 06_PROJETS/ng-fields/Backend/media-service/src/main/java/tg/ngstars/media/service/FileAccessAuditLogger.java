package tg.ngstars.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tg.ngstars.media.config.MediaProperties;

@Component
public class FileAccessAuditLogger {

    private static final Logger log = LoggerFactory.getLogger(FileAccessAuditLogger.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final Path auditDir;

    public FileAccessAuditLogger(MediaProperties properties) {
        this.auditDir = Path.of(properties.uploadDir()).resolve(".audit");
        try {
            Files.createDirectories(auditDir);
        } catch (IOException e) {
            log.warn("Impossible de créer le dossier d'audit: {}", auditDir, e);
        }
    }

    public void logAccess(String action, String filename, String userId, String detail) {
        var line = "%s | %s | %s | %s | %s%n"
                .formatted(
                        FORMATTER.format(OffsetDateTime.now()),
                        action,
                        filename,
                        userId,
                        detail != null ? detail : "");

        var logFile = auditDir.resolve("file-access.log");
        try {
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Échec écriture audit log: {}", e.getMessage());
        }

        log.info("FILE_AUDIT: action={}, file={}, user={}, detail={}", action, filename, userId, detail);
    }

    public void logUpload(String filename, String userId, long size) {
        logAccess("UPLOAD", filename, userId, "size=" + size);
    }

    public void logDownload(String filename, String userId) {
        logAccess("DOWNLOAD", filename, userId, null);
    }

    public void logDelete(String filename, String userId) {
        logAccess("DELETE", filename, userId, null);
    }

    public void logAccessDenied(String filename, String userId, String reason) {
        logAccess("ACCESS_DENIED", filename, userId, reason);
    }
}
