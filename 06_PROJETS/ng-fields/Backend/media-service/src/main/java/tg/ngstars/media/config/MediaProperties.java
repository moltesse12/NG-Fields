package tg.ngstars.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "media")
public record MediaProperties(
    String uploadDir,
    long maxFileSizeBytes,
    long maxStorageBytes
) {

    public MediaProperties {
        if (maxFileSizeBytes <= 0) maxFileSizeBytes = 10 * 1024 * 1024;
        if (maxStorageBytes <= 0) maxStorageBytes = 5L * 1024 * 1024 * 1024;
    }
}
