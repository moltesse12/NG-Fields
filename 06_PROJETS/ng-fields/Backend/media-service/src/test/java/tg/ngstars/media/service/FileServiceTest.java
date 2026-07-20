package tg.ngstars.media.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import tg.ngstars.media.config.MediaProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    private static final String USER_OWNER = "user-001";
    private static final String USER_OTHER = "user-002";

    private static final byte[] PNG_MAGIC = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00
    };
    private static final byte[] JPEG_MAGIC = new byte[]{
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10
    };
    private static final byte[] PDF_MAGIC = new byte[]{
            0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34
    };
    private static final byte[] GIF_MAGIC = new byte[]{
            0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x00, 0x01
    };
    private static final byte[] WEBP_MAGIC = new byte[]{
            0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50
    };

    private static final long MAX_FILE_SIZE = 1024;
    private static final long MAX_STORAGE = 1024 * 1024;

    private Path tempDir;
    private FileService fileService;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("fileservice-test");
        var mediaProperties = new MediaProperties(tempDir.toString(), MAX_FILE_SIZE, MAX_STORAGE);
        var objectMapper = new ObjectMapper();
        fileService = new FileService(mediaProperties, objectMapper);
        fileService.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            try (var stream = Files.walk(tempDir)) {
                stream.sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> {
                            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                        });
            }
        }
    }

    @Test
    void store_allowedExtension_shouldStore() {
        var file = new MockMultipartFile("file", "photo.png", "image/png", PNG_MAGIC);
        String filename = fileService.store(file, USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".png"));
        Path stored = tempDir.resolve(filename);
        assertTrue(Files.exists(stored));
    }

    @Test
    void store_disallowedExtension_shouldThrow() {
        var file = new MockMultipartFile("file", "malware.exe", "application/octet-stream",
                new byte[]{0x00, 0x01, 0x02, 0x03});

        assertThrows(IllegalArgumentException.class,
                () -> fileService.store(file, USER_OWNER));
    }

    @Test
    void store_fileTooLarge_shouldThrow() {
        byte[] oversized = new byte[(int) (MAX_FILE_SIZE + 1)];
        var file = new MockMultipartFile("file", "big.png", "image/png", oversized);

        assertThrows(IllegalArgumentException.class,
                () -> fileService.store(file, USER_OWNER));
    }

    @Test
    void store_mismatchedMagicBytes_shouldThrow() {
        var file = new MockMultipartFile("file", "fake.png", "image/png", JPEG_MAGIC);

        assertThrows(IllegalArgumentException.class,
                () -> fileService.store(file, USER_OWNER));
    }

    @Test
    void storeBytes_validPng_shouldStore() {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".png"));
        Path stored = tempDir.resolve(filename);
        assertTrue(Files.exists(stored));
    }

    @Test
    void storeBytes_disallowedExtension_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> fileService.storeBytes(PNG_MAGIC, ".exe", USER_OWNER));
    }

    @Test
    void load_existingFile_shouldReturnPath() {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);
        Path loaded = fileService.load(filename);

        assertNotNull(loaded);
        assertTrue(Files.exists(loaded));
        assertEquals(tempDir.resolve(filename), loaded);
    }

    @Test
    void load_nonExistentFile_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> fileService.load("nonexistent.png"));
    }

    @Test
    void load_pathTraversal_shouldThrow() {
        assertThrows(SecurityException.class,
                () -> fileService.load(".."));
    }

    @Test
    void delete_ownerCanDelete() {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);
        Path stored = tempDir.resolve(filename);
        assertTrue(Files.exists(stored));

        fileService.delete(filename, USER_OWNER);

        assertFalse(Files.exists(stored));
    }

    @Test
    void delete_nonOwner_shouldThrow() {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);

        assertThrows(SecurityException.class,
                () -> fileService.delete(filename, USER_OTHER));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void delete_untrackedFile_shouldThrow() {
        assertThrows(SecurityException.class,
                () -> fileService.delete("never-uploaded.png", USER_OWNER));
    }

    @Test
    void storePathTraversal_shouldThrow() {
        var file = new MockMultipartFile("file", "../../evil.png", "image/png", PNG_MAGIC);

        String filename = fileService.store(file, USER_OWNER);
        assertNotNull(filename);
        Path stored = tempDir.resolve(filename);
        assertTrue(Files.exists(stored));
        assertFalse(filename.contains(".."));
    }

    @Test
    void storeBytes_validJpeg_shouldStore() {
        String filename = fileService.storeBytes(JPEG_MAGIC, ".jpg", USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void storeBytes_validGif_shouldStore() {
        String filename = fileService.storeBytes(GIF_MAGIC, ".gif", USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".gif"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void storeBytes_validWebp_shouldStore() {
        String filename = fileService.storeBytes(WEBP_MAGIC, ".webp", USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".webp"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void storeBytes_validPdf_shouldStore() {
        String filename = fileService.storeBytes(PDF_MAGIC, ".pdf", USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".pdf"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void storeBytes_fileTooLarge_shouldThrow() {
        byte[] oversized = new byte[(int) (MAX_FILE_SIZE + 1)];

        assertThrows(IllegalArgumentException.class,
                () -> fileService.storeBytes(oversized, ".png", USER_OWNER));
    }

    @Test
    void storeBytes_mismatchedMagicBytes_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> fileService.storeBytes(JPEG_MAGIC, ".png", USER_OWNER));
    }

    @Test
    void storeBytes_nullExtension_shouldStoreAsBin() {
        String filename = fileService.storeBytes(PNG_MAGIC, null, USER_OWNER);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".bin"));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void store_storeMultipleFiles_shouldSucceed() {
        var file1 = new MockMultipartFile("file", "a.png", "image/png", PNG_MAGIC);
        var file2 = new MockMultipartFile("file", "b.png", "image/png", PNG_MAGIC);

        String name1 = fileService.store(file1, USER_OWNER);
        String name2 = fileService.store(file2, USER_OWNER);

        assertNotEquals(name1, name2);
        assertTrue(Files.exists(tempDir.resolve(name1)));
        assertTrue(Files.exists(tempDir.resolve(name2)));
    }

    @Test
    void delete_fileRemovedFromOwnershipTracking() {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);

        fileService.delete(filename, USER_OWNER);

        assertThrows(SecurityException.class,
                () -> fileService.delete(filename, USER_OWNER));
    }

    @Test
    void getUsedStorageBytes_afterStore_shouldReflectSize() throws IOException {
        long before = fileService.getUsedStorageBytes();

        fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);

        long after = fileService.getUsedStorageBytes();
        assertEquals(before + PNG_MAGIC.length, after);
    }

    @Test
    void getUsedStorageBytes_afterDelete_shouldDecrease() throws IOException {
        String filename = fileService.storeBytes(PNG_MAGIC, ".png", USER_OWNER);
        long afterStore = fileService.getUsedStorageBytes();

        fileService.delete(filename, USER_OWNER);

        long afterDelete = fileService.getUsedStorageBytes();
        assertEquals(afterStore - PNG_MAGIC.length, afterDelete);
    }

    @Test
    void store_noExtension_shouldThrow() {
        var file = new MockMultipartFile("file", "noext", "application/octet-stream",
                new byte[]{0x00, 0x01, 0x02});

        assertThrows(IllegalArgumentException.class,
                () -> fileService.store(file, USER_OWNER));
    }

    @Test
    void store_uppercaseExtension_shouldStore() {
        var file = new MockMultipartFile("file", "photo.PNG", "image/png", PNG_MAGIC);

        String filename = fileService.store(file, USER_OWNER);

        assertNotNull(filename);
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void isImageContentType_shouldReturnTrue_forImageTypes() {
        assertTrue(FileService.isImageContentType("image/jpeg"));
        assertTrue(FileService.isImageContentType("image/png"));
        assertTrue(FileService.isImageContentType("image/gif"));
        assertTrue(FileService.isImageContentType("image/webp"));
    }

    @Test
    void isImageContentType_shouldReturnFalse_forNonImageTypes() {
        assertFalse(FileService.isImageContentType("application/pdf"));
        assertFalse(FileService.isImageContentType("text/plain"));
        assertFalse(FileService.isImageContentType(null));
    }
}
