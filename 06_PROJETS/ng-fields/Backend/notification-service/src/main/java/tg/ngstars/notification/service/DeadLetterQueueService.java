package tg.ngstars.notification.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class DeadLetterQueueService {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueueService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final Path dlqDir;
    private final ObjectMapper objectMapper;

    public DeadLetterQueueService(
            @Value("${notification.dlq-dir:./dlq}") String dlqDir) {
        this.dlqDir = Path.of(dlqDir);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        try {
            Files.createDirectories(this.dlqDir);
        } catch (IOException e) {
            log.error("Impossible de créer le dossier DLQ: {}", this.dlqDir, e);
        }
    }

    public void enqueue(String type, String recipient, String subject, String template,
                        String payload, String errorMessage) {
        var entry = new DlqEntry(
                OffsetDateTime.now(),
                type,
                recipient,
                subject,
                template,
                payload,
                errorMessage);

        var filename = "%s_%s_%s.json"
                .formatted(
                        FORMATTER.format(OffsetDateTime.now()).replace(":", "-"),
                        type.toLowerCase(),
                        Math.abs(recipient.hashCode()));
        var file = dlqDir.resolve(filename);

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), entry);
            log.warn("Ajouté à la DLQ: type={}, recipient={}, error={}", type, recipient, errorMessage);
        } catch (IOException e) {
            log.error("Échec écriture DLQ: {}", e.getMessage());
        }
    }

    public List<DlqEntry> listPending() {
        var entries = new ArrayList<DlqEntry>();
        try (var stream = Files.list(dlqDir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .sorted()
                    .forEach(p -> {
                        try {
                            entries.add(objectMapper.readValue(p.toFile(), DlqEntry.class));
                        } catch (IOException e) {
                            log.warn("Impossible de lire l'entrée DLQ: {}", p.getFileName());
                        }
                    });
        } catch (IOException e) {
            log.error("Échec lecture DLQ: {}", e.getMessage());
        }
        return entries;
    }

    public int getPendingCount() {
        try (var stream = Files.list(dlqDir)) {
            return (int) stream.filter(p -> p.toString().endsWith(".json")).count();
        } catch (IOException e) {
            return 0;
        }
    }

    public record DlqEntry(
            OffsetDateTime timestamp,
            String type,
            String recipient,
            String subject,
            String template,
            String payload,
            String errorMessage) {}
}
