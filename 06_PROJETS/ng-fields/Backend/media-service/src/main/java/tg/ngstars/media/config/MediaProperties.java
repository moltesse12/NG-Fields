package tg.ngstars.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "media")
public record MediaProperties(
    String uploadDir,
    long maxFileSizeBytes,
    long maxStorageBytes,
    long maxStoragePerCompanyBytes,
    boolean antivirusEnabled,
    String clamavHost,
    int clamavPort,
    int imageCompressionQuality
) {

    public MediaProperties {
        if (maxFileSizeBytes <= 0) maxFileSizeBytes = 10 * 1024 * 1024;
        if (maxStorageBytes <= 0) maxStorageBytes = 5L * 1024 * 1024 * 1024;
        if (maxStoragePerCompanyBytes <= 0) maxStoragePerCompanyBytes = 500L * 1024 * 1024;
        if (clamavPort <= 0) clamavPort = 3310;
        if (imageCompressionQuality <= 0 || imageCompressionQuality > 100) imageCompressionQuality = 80;
    }
}
