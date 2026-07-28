package tg.ngstars.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardErrorResponse(
    String code,
    String message,
    Instant timestamp,
    String path,
    Map<String, String> details
) {
    public static StandardErrorResponse of(String code, String message, String path) {
        return new StandardErrorResponse(code, message, Instant.now(), path, null);
    }

    public static StandardErrorResponse of(String code, String message, String path, Map<String, String> details) {
        return new StandardErrorResponse(code, message, Instant.now(), path, details);
    }
}
