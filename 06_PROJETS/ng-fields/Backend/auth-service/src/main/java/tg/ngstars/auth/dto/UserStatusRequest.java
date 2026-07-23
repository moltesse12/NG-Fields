package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(
    @NotNull Boolean enabled
) {
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}
