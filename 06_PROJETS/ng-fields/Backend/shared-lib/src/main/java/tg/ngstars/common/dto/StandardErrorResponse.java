package tg.ngstars.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp,
    String path,
    Map<String, String> details
) {
    public static StandardErrorResponse of(String code, String message, String path) {
        return new StandardErrorResponse(code, message, LocalDateTime.now(), path, null);
    }

    public static StandardErrorResponse of(String code, String message, String path, Map<String, String> details) {
        return new StandardErrorResponse(code, message, LocalDateTime.now(), path, details);
    }
}
