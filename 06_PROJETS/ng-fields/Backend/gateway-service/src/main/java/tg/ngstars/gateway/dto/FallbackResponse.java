package tg.ngstars.gateway.dto;

import java.time.Instant;

public record FallbackResponse(
    String type,
    String title,
    int status,
    String detail,
    String routeId,
    Instant timestamp
) {
    public static FallbackResponse of(String routeId, String detail) {
        return new FallbackResponse(
            "about:blank",
            "Service Unavailable",
            503,
            detail,
            routeId,
            Instant.now()
        );
    }
}
