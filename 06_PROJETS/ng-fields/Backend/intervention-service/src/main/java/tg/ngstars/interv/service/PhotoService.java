package tg.ngstars.interv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import tg.ngstars.interv.client.MediaClient;
import tg.ngstars.interv.dto.PhotoResponse;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.interv.model.InterventionPhoto;
import tg.ngstars.interv.model.PhotoType;
import tg.ngstars.interv.repository.InterventionPhotoRepository;
import tg.ngstars.interv.repository.InterventionRepository;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private static final int MAX_PHOTOS_PER_CATEGORY = 5;

    private final InterventionPhotoRepository photoRepo;
    private final InterventionRepository interventionRepo;
    private final MediaClient mediaClient;
    private final SecurityUtils securityUtils;

    public PhotoService(
            InterventionPhotoRepository photoRepo,
            InterventionRepository interventionRepo,
            MediaClient mediaClient,
            SecurityUtils securityUtils) {
        this.photoRepo        = photoRepo;
        this.interventionRepo = interventionRepo;
        this.mediaClient      = mediaClient;
        this.securityUtils    = securityUtils;
    }

    // ponytail: JVM-wide lock, per-intervention lock if throughput matters
    @Transactional
    public synchronized PhotoResponse addPhoto(
            UUID interventionId,
            MultipartFile file,
            String type,
            Double latitude,
            Double longitude) throws IOException {

        var intervention = interventionRepo.findById(interventionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Intervention introuvable : " + interventionId));
        checkOwnership(intervention);

        PhotoType photoType = PhotoType.valueOf(type.toUpperCase());

        long count = photoRepo.countByInterventionIdAndType(interventionId, photoType);
        if (count >= MAX_PHOTOS_PER_CATEGORY) {
            throw new IllegalStateException(
                "Limite atteinte : maximum " + MAX_PHOTOS_PER_CATEGORY
                + " photos par catégorie (" + type + ")");
        }

        String url = mediaClient.uploadFile(file);
        String filename = extractFilename(url);

        InterventionPhoto photo = new InterventionPhoto();
        photo.setIntervention(intervention);
        photo.setUrl(url);
        photo.setType(photoType);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
        photo.setTakenAt(OffsetDateTime.now());
        photo.setOriginalFilename(file.getOriginalFilename());

        InterventionPhoto saved = photoRepo.save(photo);
        log.info("Photo {} ajoutée à l'intervention {} (total {} {})",
            saved.getId(), interventionId, count + 1, type);

        registerCleanupOnRollback(filename);

        return PhotoResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> listPhotos(UUID interventionId) {
        return photoRepo.findByInterventionId(interventionId)
            .stream().map(PhotoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> listPhotosByType(UUID interventionId, String type) {
        PhotoType photoType = PhotoType.valueOf(type.toUpperCase());
        return photoRepo.findByInterventionIdAndType(interventionId, photoType)
            .stream().map(PhotoResponse::from).toList();
    }

    @Transactional
    public void deletePhoto(UUID interventionId, UUID photoId) {
        InterventionPhoto photo = photoRepo.findById(photoId)
            .filter(p -> p.getIntervention().getId().equals(interventionId))
            .orElseThrow(() -> new IllegalArgumentException("Photo introuvable : " + photoId));
        checkOwnership(photo.getIntervention());
        photoRepo.delete(photo);
        log.info("Photo {} supprimée de l'intervention {}", photoId, interventionId);
    }

    private void registerCleanupOnRollback(String filename) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    log.warn("Transaction rolled back — cleaning up uploaded file: {}", filename);
                    try {
                        mediaClient.deleteFile(filename);
                    } catch (Exception e) {
                        log.error("Failed to cleanup file {}", filename, e);
                    }
                }
            }
        });
    }

    private void checkOwnership(tg.ngstars.interv.model.Intervention intervention) {
        if (!securityUtils.isAdminOrManager()
                && (intervention.getAssignedTo() == null
                    || !intervention.getAssignedTo().equals(securityUtils.getCurrentUserId()))) {
            throw new ForbiddenException("Not assigned to this intervention");
        }
    }

    private static String extractFilename(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
