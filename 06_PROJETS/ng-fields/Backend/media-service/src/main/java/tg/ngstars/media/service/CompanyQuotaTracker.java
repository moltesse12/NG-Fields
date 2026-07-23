package tg.ngstars.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tg.ngstars.media.config.MediaProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CompanyQuotaTracker {

    private static final Logger log = LoggerFactory.getLogger(CompanyQuotaTracker.class);

    private final MediaProperties properties;
    private final Path quotaFile;
    private final ConcurrentHashMap<String, Long> companyUsage = new ConcurrentHashMap<>();

    public CompanyQuotaTracker(MediaProperties properties, ObjectMapper objectMapper) throws IOException {
        this.properties = properties;
        this.quotaFile = Path.of(properties.uploadDir()).resolve(".company-quotas.json");

        if (Files.exists(quotaFile)) {
            try (var reader = Files.newBufferedReader(quotaFile)) {
                Map<String, Long> loaded = objectMapper.readValue(reader, new TypeReference<>() {});
                if (loaded != null) companyUsage.putAll(loaded);
            } catch (IOException e) {
                log.warn("Failed to load company quotas, starting fresh", e);
            }
        }
    }

    public void addUsage(String companyId, long bytes) {
        companyUsage.merge(companyId, bytes, Long::sum);
        persist();
    }

    public void removeUsage(String companyId, long bytes) {
        companyUsage.merge(companyId, -bytes, Long::sum);
        if (companyUsage.getOrDefault(companyId, 0L) < 0) {
            companyUsage.put(companyId, 0L);
        }
        persist();
    }

    public long getUsage(String companyId) {
        return companyUsage.getOrDefault(companyId, 0L);
    }

    public boolean wouldExceedQuota(String companyId, long incomingBytes) {
        long limit = properties.maxStoragePerCompanyBytes();
        long current = getUsage(companyId);
        return current + incomingBytes > limit;
    }

    public long getRemainingQuota(String companyId) {
        long limit = properties.maxStoragePerCompanyBytes();
        long current = getUsage(companyId);
        return Math.max(0, limit - current);
    }

    private void persist() {
        var tmp = quotaFile.resolveSibling(quotaFile.getFileName() + ".tmp");
        try (var writer = Files.newBufferedWriter(tmp)) {
            var objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, companyUsage);
            Files.move(tmp, quotaFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to persist company quotas", e);
        }
    }
}
