package tg.ngstars.interv.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Map;

@Component
public class MediaClient {

    private static final Logger log = LoggerFactory.getLogger(MediaClient.class);

    private final RestClient restClient;
    private final String mediaBaseUrl;

    public MediaClient(@Qualifier("mediaRestClient") RestClient restClient,
            @Value("${media-service.url:http://localhost:8084}") String mediaBaseUrl) {
        this.restClient = restClient;
        this.mediaBaseUrl = mediaBaseUrl;
    }

    public record UploadResponse(String filename) {}

    @CircuitBreaker(name = "mediaClient", fallbackMethod = "uploadFallback")
    @Retry(name = "mediaClient")
    public String uploadFile(MultipartFile file) {
        var response = restClient.post()
                .uri("/upload")
                .body(createMultipartBody(file))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on upload: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Upload echoue: " + res.getStatusCode());
                })
                .body(UploadResponse.class);

        if (response == null || response.filename() == null)
            throw new tg.ngstars.interv.exception.MediaServiceException("Reponse media-service invalide : filename absent");

        return UriComponentsBuilder.fromUriString(mediaBaseUrl)
                .pathSegment("api", "media", response.filename())
                .build().toUriString();
    }

    @CircuitBreaker(name = "mediaClient", fallbackMethod = "uploadBase64Fallback")
    @Retry(name = "mediaClient")
    public String uploadBase64(String base64Data) {
        String data = base64Data.replaceAll("^data:image/[^;]+;base64,", "");
        var response = restClient.post()
                .uri("/upload-base64")
                .body(Map.of("data", data))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on base64 upload: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Upload base64 echoue: " + res.getStatusCode());
                })
                .body(UploadResponse.class);

        if (response == null || response.filename() == null)
            throw new tg.ngstars.interv.exception.MediaServiceException("Reponse media-service invalide : filename absent");

        return UriComponentsBuilder.fromUriString(mediaBaseUrl)
                .pathSegment("api", "media", response.filename())
                .build().toUriString();
    }

    @CircuitBreaker(name = "mediaClient", fallbackMethod = "deleteFallback")
    @Retry(name = "mediaClient")
    public void deleteFile(String filename) {
        restClient.delete()
                .uri("/{filename}", filename)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on delete: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Suppression echouee: " + res.getStatusCode());
                })
                .toBodilessEntity();
    }

    private String uploadFallback(MultipartFile file, Exception e) {
        log.error("Circuit breaker fallback for uploadFile: {}", e.getMessage());
        throw new tg.ngstars.interv.exception.MediaServiceException("Media service unavailable", e);
    }

    private String uploadBase64Fallback(String base64Data, Exception e) {
        log.error("Circuit breaker fallback for uploadBase64: {}", e.getMessage());
        throw new tg.ngstars.interv.exception.MediaServiceException("Media service unavailable", e);
    }

    private void deleteFallback(String filename, Exception e) {
        log.error("Circuit breaker fallback for deleteFile: {}", e.getMessage());
        throw new tg.ngstars.interv.exception.MediaServiceException("Media service unavailable", e);
    }

    private org.springframework.http.HttpEntity<?> createMultipartBody(MultipartFile file) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        return new org.springframework.http.HttpEntity<>(body, headers);
    }
}
