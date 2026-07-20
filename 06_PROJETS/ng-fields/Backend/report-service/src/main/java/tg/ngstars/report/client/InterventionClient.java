package tg.ngstars.report.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tg.ngstars.report.dto.InterventionReportDto;

@Component
public class InterventionClient {

    private static final Logger log = LoggerFactory.getLogger(InterventionClient.class);

    private final RestClient restClient;

    public InterventionClient(
            @Value("${intervention-service.url:http://localhost:8083}") String baseUrl) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl + "/api/interventions")
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        request.getHeaders().setBearerAuth(jwt.getTokenValue());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @CircuitBreaker(name = "interventionClient", fallbackMethod = "fetchFallback")
    @Retry(name = "interventionClient")
    public List<InterventionReportDto> fetchAllForReport(int size) {
        var typeRef = new ParameterizedTypeReference<Page<InterventionReportDto>>() {};
        var page = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("size", size).build())
                .retrieve()
                .body(typeRef);
        return page != null ? page.getContent() : List.of();
    }

    public List<InterventionReportDto> fetchFallback(int size, Exception e) {
        log.warn("Circuit breaker fallback for fetchAllForReport: {}", e.getMessage());
        return List.of();
    }
}
