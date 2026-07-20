package tg.ngstars.interv.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tg.ngstars.interv.dto.PhotoResponse;
import tg.ngstars.interv.service.PhotoService;

@RestController
@RequestMapping("/api/interventions/{id}/photos")
public class PhotoController {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp");

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<PhotoResponse> upload(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Type de fichier non autorise: " + file.getContentType());

        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(photoService.addPhoto(id, file, type, latitude, longitude));
        } catch (java.io.IOException e) {
            throw new tg.ngstars.interv.exception.MediaServiceException("Erreur upload photo: " + e.getMessage(), e);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PhotoResponse>> list(@PathVariable UUID id) {
        return ResponseEntity.ok(photoService.listPhotos(id));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PhotoResponse>> listByType(
            @PathVariable UUID id,
            @PathVariable String type) {
        return ResponseEntity.ok(photoService.listPhotosByType(id, type));
    }

    @DeleteMapping("/{photoId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID photoId) {
        photoService.deletePhoto(id, photoId);
        return ResponseEntity.noContent().build();
    }
}
