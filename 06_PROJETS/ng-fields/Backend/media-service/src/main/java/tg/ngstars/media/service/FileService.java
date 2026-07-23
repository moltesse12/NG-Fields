package tg.ngstars.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.media.config.MediaProperties;
import tg.ngstars.media.exception.FileAccessException;
import tg.ngstars.media.exception.StorageLimitReachedException;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private static final Map<String, String> ALLOWED_EXTENSIONS = Map.of(
        ".jpg", "image/jpeg", ".jpeg", "image/jpeg",
        ".png", "image/png", ".gif", "image/gif",
        ".webp", "image/webp", ".pdf", "application/pdf");

    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
        "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
        "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
        "image/gif", new byte[]{0x47, 0x49, 0x46, 0x38},
        "image/webp", new byte[]{0x52, 0x49, 0x46, 0x46},
        "application/pdf", new byte[]{0x25, 0x50, 0x44, 0x46});

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private final MediaProperties mediaProperties;
    private final Path uploadPath;
    private final Path ownershipFile;
    private final ObjectMapper objectMapper;
    private final Map<String, String> fileOwners = new ConcurrentHashMap<>();
    private final Map<String, String> fileCompanyIds = new ConcurrentHashMap<>();

    private final AntivirusScanner antivirusScanner;
    private final ImageCompressor imageCompressor;
    private final CompanyQuotaTracker quotaTracker;
    private final FileAccessAuditLogger auditLogger;

    public FileService(MediaProperties mediaProperties, ObjectMapper objectMapper,
                       AntivirusScanner antivirusScanner, ImageCompressor imageCompressor,
                       CompanyQuotaTracker quotaTracker, FileAccessAuditLogger auditLogger) {
        this.mediaProperties = mediaProperties;
        this.objectMapper = objectMapper;
        this.antivirusScanner = antivirusScanner;
        this.imageCompressor = imageCompressor;
        this.quotaTracker = quotaTracker;
        this.auditLogger = auditLogger;
        this.uploadPath = Path.of(mediaProperties.uploadDir()).toAbsolutePath().normalize();
        this.ownershipFile = uploadPath.resolve(".owners.json");
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
        if (Files.exists(ownershipFile)) {
            try (var reader = Files.newBufferedReader(ownershipFile)) {
                Map<String, FileOwnership> loaded = objectMapper.readValue(reader, new TypeReference<>() {});
                if (loaded != null) {
                    loaded.forEach((k, v) -> {
                        fileOwners.put(k, v.owner());
                        fileCompanyIds.put(k, v.companyId());
                    });
                }
            } catch (IOException e) {
                log.warn("Failed to load ownership file, starting fresh", e);
            }
        }
    }

    public String storeBytes(byte[] data, String extension, String userId) {
        return storeBytes(data, extension, userId, null);
    }

    public String storeBytes(byte[] data, String extension, String userId, String companyId) {
        validateFileSize(data.length);
        var safeExt = sanitizeExtension(extension);
        var expectedMime = ALLOWED_EXTENSIONS.get(safeExt);
        if (expectedMime != null) {
            validateMagicBytes(data, expectedMime);
        }
        var filename = UUID.randomUUID() + safeExt;
        try {
            var target = uploadPath.resolve(filename).normalize();
            if (!target.startsWith(uploadPath))
                throw new FileAccessException("Invalid file path");
            ensureStorageAvailable(data.length);
            if (companyId != null) checkCompanyQuota(companyId, data.length);
            Files.write(target, data);
            trackOwnership(filename, userId, companyId, data.length);
            auditLogger.logUpload(filename, userId, data.length);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store bytes", e);
        }
    }

    public String store(MultipartFile file, String userId) {
        return store(file, userId, null);
    }

    public String store(MultipartFile file, String userId, String companyId) {
        validateFileSize(file.getSize());
        var originalName = file.getOriginalFilename();
        var ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.containsKey(ext))
            throw new IllegalArgumentException("File type not allowed: " + ext);

        var expectedMime = ALLOWED_EXTENSIONS.get(ext);
        try {
            var bytes = file.getBytes();
            validateMagicBytes(bytes, expectedMime);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file for validation", e);
        }

        try {
            var target = uploadPath.resolve(UUID.randomUUID() + ext).normalize();
            if (!target.startsWith(uploadPath))
                throw new FileAccessException("Cannot store file outside upload directory");
            ensureStorageAvailable(file.getSize());
            if (companyId != null) checkCompanyQuota(companyId, file.getSize());

            antivirusScanner.scan(target);

            MultipartFile toStore = imageCompressor.compressIfNeeded(file);

            try (var is = toStore.getInputStream()) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String filename = target.getFileName().toString();
            trackOwnership(filename, userId, companyId, toStore.getSize());
            auditLogger.logUpload(filename, userId, toStore.getSize());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path load(String filename) {
        String clean = Path.of(filename).getFileName().toString();
        var file = uploadPath.resolve(clean).normalize();
        if (!file.startsWith(uploadPath))
            throw new FileAccessException("Invalid path");
        if (!Files.exists(file))
            throw new IllegalArgumentException("File not found: " + clean);
        return file;
    }

    public String getOwner(String filename) {
        return fileOwners.get(filename);
    }

    public String getCompanyId(String filename) {
        return fileCompanyIds.get(filename);
    }

    public void delete(String filename, String userId) {
        String clean = Path.of(filename).getFileName().toString();
        var owner = fileOwners.get(clean);
        if (owner == null)
            throw new FileAccessException("File not tracked: " + clean);
        if (!owner.equals(userId))
            throw new FileAccessException("Not the owner of this file");
        try {
            var path = uploadPath.resolve(clean);
            long size = Files.exists(path) ? Files.size(path) : 0;
            Files.deleteIfExists(path);
            fileOwners.remove(clean);
            var companyId = fileCompanyIds.remove(clean);
            if (companyId != null) quotaTracker.removeUsage(companyId, size);
            persistOwners();
            auditLogger.logDelete(clean, userId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public long getUsedStorageBytes() throws IOException {
        try (var stream = Files.walk(uploadPath)) {
            return stream.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .mapToLong(p -> {
                        try { return Files.size(p); } catch (IOException e) { return 0; }
                    })
                    .sum();
        }
    }

    public static boolean isImageContentType(String contentType) {
        return contentType != null && IMAGE_TYPES.contains(contentType);
    }

    private void validateFileSize(long size) {
        if (size > mediaProperties.maxFileSizeBytes()) {
            throw new IllegalArgumentException(
                    "File size " + size + " bytes exceeds maximum allowed " + mediaProperties.maxFileSizeBytes() + " bytes");
        }
    }

    private void checkCompanyQuota(String companyId, long incomingSize) {
        if (quotaTracker.wouldExceedQuota(companyId, incomingSize)) {
            throw new StorageLimitReachedException(
                    "Quota de stockage dépassé pour la company " + companyId
                    + ". Utilisé: " + quotaTracker.getUsage(companyId)
                    + ", limite: " + mediaProperties.maxStoragePerCompanyBytes());
        }
    }

    private void ensureStorageAvailable(long incomingSize) {
        try {
            long used = getUsedStorageBytes();
            if (used + incomingSize > mediaProperties.maxStorageBytes()) {
                throw new StorageLimitReachedException(
                        "Storage limit reached. Used: " + used + ", requested: " + incomingSize
                                + ", limit: " + mediaProperties.maxStorageBytes());
            }
        } catch (IOException e) {
            log.warn("Could not verify storage space, proceeding with upload", e);
        }
    }

    private static void validateMagicBytes(byte[] data, String expectedMime) {
        var magic = MAGIC_BYTES.get(expectedMime);
        if (magic == null || data.length < magic.length) return;
        for (int i = 0; i < magic.length; i++) {
            if (data[i] != magic[i]) {
                throw new IllegalArgumentException(
                        "File content does not match expected type " + expectedMime);
            }
        }
    }

    private String sanitizeExtension(String ext) {
        if (ext == null || !ext.startsWith(".")) return ".bin";
        String lower = ext.toLowerCase();
        if (!ALLOWED_EXTENSIONS.containsKey(lower))
            throw new IllegalArgumentException("File type not allowed: " + ext);
        return lower;
    }

    private void trackOwnership(String filename, String userId, String companyId, long size) {
        fileOwners.put(filename, userId);
        fileCompanyIds.put(filename, companyId);
        if (companyId != null) {
            quotaTracker.addUsage(companyId, size);
        }
        persistOwners();
    }

    private void persistOwners() {
        var tmp = ownershipFile.resolveSibling(ownershipFile.getFileName() + ".tmp");
        try (var writer = Files.newBufferedWriter(tmp)) {
            var data = new java.util.HashMap<String, FileOwnership>();
            fileOwners.forEach((k, v) -> data.put(k, new FileOwnership(v, fileCompanyIds.get(k))));
            objectMapper.writeValue(writer, data);
            Files.move(tmp, ownershipFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to persist file ownership", e);
        }
    }

    public record FileOwnership(String owner, String companyId) {}
}
