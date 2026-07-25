package tg.ngstars.media.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.media.config.MediaProperties;
import tg.ngstars.media.exception.StorageLimitReachedException;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileService")
class FileServiceTest {

    @Mock private AntivirusScanner antivirusScanner;
    @Mock private ImageCompressor imageCompressor;
    @Mock private CompanyQuotaTracker quotaTracker;
    @Mock private FileAccessAuditLogger auditLogger;
    @Mock private MediaProperties mediaProperties;

    @InjectMocks
    private FileService fileService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(mediaProperties.uploadDir()).thenReturn(tempDir.toString());
        lenient().when(mediaProperties.maxFileSizeBytes()).thenReturn(10_485_760L);
        lenient().when(mediaProperties.maxStorageBytes()).thenReturn(5_368_709_120L);
        lenient().when(mediaProperties.maxStoragePerCompanyBytes()).thenReturn(1_073_741_824L);

        var objectMapper = new ObjectMapper();
        var field = FileService.class.getDeclaredField("objectMapper");
        field.setAccessible(true);
        field.set(fileService, objectMapper);

        fileService.init();
    }

    @Nested
    @DisplayName("load()")
    class Load {

        @Test
        @DisplayName("Retourne le chemin du fichier quand il existe")
        void load_existingFile_returnsPath() throws Exception {
            var filename = "test-file.txt";
            Files.createFile(tempDir.resolve(filename));

            var path = fileService.load(filename);

            assertNotNull(path);
            assertTrue(Files.exists(path));
        }

        @Test
        @DisplayName("Lance IllegalArgumentException pour chemin relatif")
        void load_relativePath_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> fileService.load("../../../etc/passwd"));
        }

        @Test
        @DisplayName("Lance IllegalArgumentException pour chemin avec ..")
        void load_pathTraversal_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> fileService.load("..\\windows\\system32"));
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Supprime un fichier existant")
        void delete_existingFile_success() throws Exception {
            var filename = "delete-me.txt";
            Files.createFile(tempDir.resolve(filename));

            var ownersField = FileService.class.getDeclaredField("owners");
            ownersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var owners = (java.util.Map<String, String>) ownersField.get(fileService);
            owners.put(filename, "user-1");

            var companiesField = FileService.class.getDeclaredField("companyIds");
            companiesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var companies = (java.util.Map<String, String>) companiesField.get(fileService);
            companies.put(filename, "company-1");

            assertDoesNotThrow(() -> fileService.delete(filename, "user-1"));
            assertFalse(Files.exists(tempDir.resolve(filename)));
            verify(quotaTracker).removeUsage(eq("company-1"), anyLong());
            verify(auditLogger).logDelete(eq("user-1"), eq(filename));
        }

        @Test
        @DisplayName("Lance FileAccessException si pas proprietaire")
        void delete_notOwner_throws() throws Exception {
            var filename = "protected.txt";
            Files.createFile(tempDir.resolve(filename));

            var ownersField = FileService.class.getDeclaredField("owners");
            ownersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var owners = (java.util.Map<String, String>) ownersField.get(fileService);
            owners.put(filename, "other-user");

            assertThrows(tg.ngstars.media.exception.FileAccessException.class,
                    () -> fileService.delete(filename, "wrong-user"));
        }
    }

    @Nested
    @DisplayName("isImageContentType()")
    class IsImageContentType {

        @Test
        @DisplayName("Retourne vrai pour les types image")
        void isImageContentType_imageTypes() {
            assertTrue(FileService.isImageContentType("image/jpeg"));
            assertTrue(FileService.isImageContentType("image/png"));
            assertTrue(FileService.isImageContentType("image/gif"));
            assertTrue(FileService.isImageContentType("image/webp"));
        }

        @Test
        @DisplayName("Retourne faux pour les types non-image")
        void isImageContentType_nonImageTypes() {
            assertFalse(FileService.isImageContentType("application/pdf"));
            assertFalse(FileService.isImageContentType("text/plain"));
            assertFalse(FileService.isImageContentType(null));
        }
    }

    @Nested
    @DisplayName("getOwner()")
    class GetOwner {

        @Test
        @DisplayName("Retourne le proprietaire du fichier")
        void getOwner_returnsOwner() throws Exception {
            var filename = "owned.txt";
            Files.createFile(tempDir.resolve(filename));

            var ownersField = FileService.class.getDeclaredField("owners");
            ownersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var owners = (java.util.Map<String, String>) ownersField.get(fileService);
            owners.put(filename, "user-42");

            assertEquals("user-42", fileService.getOwner(filename));
        }

        @Test
        @DisplayName("Retourne null pour fichier inconnu")
        void getOwner_unknownFile_returnsNull() {
            assertNull(fileService.getOwner("nonexistent.txt"));
        }
    }
}
