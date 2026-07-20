package tg.ngstars.media.controller;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;
import tg.ngstars.media.service.FileService;

@RestController
@RequestMapping("/api/media")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt)
            return jwt.getSubject();
        throw new IllegalStateException("Authenticated user not found");
    }

    public record UploadBase64Request(@NotBlank @Size(max = 10_000_000) String data) {}

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        var filename = fileService.store(file, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filename", filename));
    }

    @PostMapping("/upload-base64")
    public ResponseEntity<Map<String, String>> uploadBase64(@Valid @RequestBody UploadBase64Request body) {
        var data = java.util.Base64.getDecoder().decode(body.data());
        var filename = fileService.storeBytes(data, "png", currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filename", filename));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        var path = fileService.load(filename);
        var resource = new FileSystemResource(path);
        var contentType = determineContentType(filename);

        var builder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType));

        if (FileService.isImageContentType(contentType)) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "inline");
            builder.header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600");
        } else {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            builder.header(HttpHeaders.CACHE_CONTROL, "no-store");
        }
        builder.header("X-Content-Type-Options", "nosniff");

        return builder.body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) {
        fileService.delete(filename, currentUserId());
        return ResponseEntity.noContent().build();
    }

    private static String determineContentType(String filename) {
        var name = filename.toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}
