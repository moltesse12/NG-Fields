package tg.ngstars.interv.dto;

import tg.ngstars.interv.model.InterventionPhoto;
import tg.ngstars.interv.model.PhotoType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PhotoResponse(
    UUID      id,
    String    url,
    PhotoType type,
    Double    latitude,
    Double    longitude,
    OffsetDateTime takenAt,
    String    originalFilename,
    OffsetDateTime createdAt
) {
    public static PhotoResponse from(InterventionPhoto p) {
        return new PhotoResponse(
            p.getId(), p.getUrl(), p.getType(),
            p.getLatitude(), p.getLongitude(),
            p.getTakenAt(), p.getOriginalFilename(),
            p.getCreatedAt()
        );
    }
}
