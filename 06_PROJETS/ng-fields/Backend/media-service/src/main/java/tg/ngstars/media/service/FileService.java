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

    public FileService(MediaProperties mediaProperties, ObjectMapper objectMapper) {
        this.mediaProperties = mediaProperties;
        this.objectMapper = objectMapper;
        this.uploadPath = Path.of(mediaProperties.uploadDir()).toAbsolutePath().normalize();
        this.ownershipFile = uploadPath.resolve(".owners.json");
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
        if (Files.exists(ownershipFile)) {
            try (var reader = Files.newBufferedReader(ownershipFile)) {
                Map<String, String> loaded = objectMapper.readValue(reader, new TypeReference<Map<String, String>>() {});
                if (loaded != null) fileOwners.putAll(loaded);
            } catch (IOException e) {
                log.warn("Failed to load ownership file, starting fresh", e);
            }
        }
    }

    public String storeBytes(byte[] data, String extension, String userId) {
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
            Files.write(target, data);
            fileOwners.put(filename, userId);
            persistOwners();
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store bytes", e);
        }
    }

    public String store(MultipartFile file, String userId) {
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

        var filename = UUID.randomUUID() + ext;
        try (var is = file.getInputStream()) {
            var target = uploadPath.resolve(filename).normalize();
            if (!target.startsWith(uploadPath))
                throw new FileAccessException("Cannot store file outside upload directory");
            ensureStorageAvailable(file.getSize());
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            fileOwners.put(filename, userId);
            persistOwners();
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

    public void delete(String filename, String userId) {
        String clean = Path.of(filename).getFileName().toString();
        var owner = fileOwners.get(clean);
        if (owner == null)
            throw new FileAccessException("File not tracked: " + clean);
        if (!owner.equals(userId))
            throw new FileAccessException("Not the owner of this file");
        try {
            Files.deleteIfExists(uploadPath.resolve(clean));
            fileOwners.remove(clean);
            persistOwners();
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

    private void persistOwners() {
        var tmp = ownershipFile.resolveSibling(ownershipFile.getFileName() + ".tmp");
        try (var writer = Files.newBufferedWriter(tmp)) {
            objectMapper.writeValue(writer, fileOwners);
            Files.move(tmp, ownershipFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to persist file ownership — in-memory state may diverge from disk", e);
        }
    }
}
