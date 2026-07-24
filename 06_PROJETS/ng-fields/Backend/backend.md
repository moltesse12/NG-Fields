
## Root POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/ma
         ven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>tg.ngstars</groupId>
    <artifactId>ng-fields-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>shared-lib</module>
        <module>auth-service</module>
        <module>client-service</module>
        <module>gateway-service</module>
        <module>intervention-service</module>
        <module>media-service</module>
        <module>notification-service</module>
        <module>report-service</module>
    </modules>

    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
        <spring-cloud.version>2025.1.2</spring-cloud.version>
        <keycloak.version>26.0.9</keycloak.version>
        <sentry.version>8.14.0</sentry.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>${springdoc.version}</version>
            </dependency>
            <dependency>
                <groupId>org.keycloak</groupId>
                <artifactId>keycloak-admin-client</artifactId>
                <version>${keycloak.version}</version>
            </dependency>
            <dependency>
                <groupId>io.sentry</groupId>
                <artifactId>sentry-bom</artifactId>
                <version>${sentry.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

---

## Environment Variables

### .env

```env
# ===========================
# NG-Fields Backend - Dev Local
# ===========================
# Backend services = lancés séparément (IDE / mvn spring-boot:run)

# --- Spring ---
SPRING_PROFILES_ACTIVE=dev
ENV_FILE=./.env
CORS_ORIGINS=http://localhost:4200,http://localhost:8100
SENTRY_DSN=https://YOUR_PUBLIC_KEY@oYOUR_ORG_ID.ingest.us.sentry.io/YOUR_PROJECT_ID

# --- PostgreSQL (shared DB, 3 schemas: auth, client, intervention) ---
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ng_fields
DB_USER=ng_fields_user
DB_PASSWORD=CHANGE_ME
DB_SSLMODE=disable

# --- Keycloak ---
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8088
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields
KEYCLOAK_REALM=ng-fields
KEYCLOAK_ADMIN_CLIENT_ID=ng-fields-backend
KEYCLOAK_ADMIN_CLIENT_SECRET=CHANGE_ME

# --- Redis (rate limiting - gateway) ---
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL=false

# --- SMTP (intervention + notification) ---
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USER=
SMTP_PASSWORD=

# --- Resend (email transactionnel) ---
RESEND_API_KEY=CHANGE_ME
RESEND_FROM_EMAIL=noreply@ng-stars.com
RESEND_FROM_NAME=NG-STARs
APP_LOGIN_URL=http://localhost:4200/login

# --- Media Storage (media-service) ---
MEDIA_UPLOAD_DIR=./uploads
MEDIA_MAX_FILE_SIZE=10485760
MEDIA_MAX_STORAGE=5368709120

# --- Service URLs (gateway routing) ---
AUTH_SERVICE_URL=http://localhost:8081
CLIENT_SERVICE_URL=http://localhost:8082
INTERVENTION_SERVICE_URL=http://localhost:8083
MEDIA_SERVICE_URL=http://localhost:8084
NOTIFICATION_SERVICE_URL=http://localhost:8085
REPORT_SERVICE_URL=http://localhost:8086
FRONTEND_URL=http://localhost:4200

```

---
=========================
	SHARED-LIB
=========================

### shared-lib/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>tg.ngstars</groupId>
    <artifactId>ng-fields-shared-lib</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>ng-fields-shared-lib</name>
    <description>Shared library for NG-Fields services</description>

    <properties>
        <java.version>25</java.version>
        <sentry.version>8.14.0</sentry.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.sentry</groupId>
                <artifactId>sentry-bom</artifactId>
                <version>${sentry.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>io.sentry</groupId>
            <artifactId>sentry-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>8.0</version>
        </dependency>
    </dependencies>
</project>

```

### shared-lib/src/main/java/tg.ngstars.common/
/config
	/SentryConfig
``java
package tg.ngstars.common.config;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    private static final Logger log = LoggerFactory.getLogger(SentryConfig.class);

    @Value("${sentry.dsn:}")
    private String dsn;

    @PostConstruct
    public void init() {
        if (dsn != null && !dsn.isBlank()) {
            Sentry.init(options -> {
                options.setDsn(dsn);
                options.setTracesSampleRate(0.2);
            });
            log.info("Sentry initialized (tracesSampleRate=0.2)");
        } else {
            log.info("Sentry DSN not configured, error tracking disabled");
        }
    }
}

```
	/WebConfig
``Java
package tg.ngstars.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tg.ngstars.common.logging.LoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/api/**");
    }
}

``
/dto
	/PaginateResponse
```Java
package tg.ngstars.common.dto;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <T> PaginatedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        if (size <= 0) throw new IllegalArgumentException("Page size must be positive");
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PaginatedResponse<>(content, page, size, totalElements, totalPages);
    }
}

```
	/StandardErrorResponse
```Java
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

```

/entity
	/AuditableEntity 
```Java 
package tg.ngstars.common.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

    private String createdBy;
    private OffsetDateTime createdAt;
    private String updatedBy;
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
```
	/BaseEntity
```Java
package tg.ngstars.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class BaseEntity {

    protected BaseEntity() {
    }

    private UUID id;

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

```

/exception 
	/BaseExceptionHandler
```Java 
package tg.ngstars.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base exception handler providing standard RFC 7807 ProblemDetail responses.
 * <p>
 * Services should extend this class and annotate with {@code @RestControllerAdvice}
 * to inherit common handlers. Add service-specific handlers as needed.
 * <p>
 * Exception hierarchy:
 * <ul>
 *   <li>{@link NotFoundException} → 404 Not Found: resource not found by ID or key</li>
 *   <li>{@link ConflictException} → 409 Conflict: duplicate resource, state conflict</li>
 *   <li>{@link ForbiddenException} → 403 Forbidden: business-level access denied</li>
 *   <li>{@link BusinessException} → 400 Bad Request: business rule violation with error code</li>
 *   <li>{@link MediaServiceException} → 502 Bad Gateway: downstream media service failure</li>
 * </ul>
 */
public abstract class BaseExceptionHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                (a, b) -> a + "; " + b));
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Bad Request");
        detail.setType(URI.create("about:blank"));
        detail.setProperty("errors", errors);
        return detail;
    }

    protected ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Not Found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleForbidden(AccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Forbidden");
        detail.setDetail("Access denied");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleForbidden(ForbiddenException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Forbidden");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleBusiness(BusinessException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Business Error");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        detail.setProperty("code", ex.getCode());
        return detail;
    }

    protected ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail("This document has been modified by another user. Please reload the page.");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail("Data constraint violation. The operation cannot be completed.");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Bad Request");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Internal Server Error");
        detail.setDetail("An unexpected error occurred");
        detail.setType(URI.create("about:blank"));
        return detail;
    }
}

``` 
	/BusinessException
```Java
package tg.ngstars.common.exception;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

	/ConflictException
```Java 
package tg.ngstars.common.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}

```

	/ForbiddenException 
```Java 
package tg.ngstars.common.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}

```
	/MediaServiceException
```Java
package tg.ngstars.common.exception;

public class MediaServiceException extends RuntimeException {
    public MediaServiceException(String message) {
        super(message);
    }

    public MediaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

	/NotFoundException
```Java
package tg.ngstars.common.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resource, Object id) {
        super(String.format("%s not found with id: %s", resource, id));
    }
}

````


```
/logging 
	/CorrelationIdFilter
```Java
package tg.ngstars.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter implements Ordered {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank() || correlationId.length() > 128) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, correlationId);
        response.addHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

 ``` 

/Logginginterceptor
```Java
package tg.ngstars.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("requestStartTime", System.currentTimeMillis());
        log.debug("→ {} {} from {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startTimeObj = request.getAttribute("requestStartTime");
        if (startTimeObj == null) return;
        long duration = System.currentTimeMillis() - (long) startTimeObj;
        int status = response.getStatus();
        if (status >= 500) {
            log.error("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else if (status >= 400) {
            log.warn("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else {
            log.debug("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        }

        if (ex != null) {
            log.error("Exception during {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        }
    }
}

  ```
	/RequestResponseLoggingFilter 
```Java
package tg.ngstars.common.logging;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_BODY_LOG_SIZE = 4096;
    private static final int REQUEST_CACHE_LIMIT = 10240;
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "x-auth-token", "x-api-key");
    private static final Set<String> SENSITIVE_BODY_FIELDS = Set.of(
            "password", "secret", "token", "accessToken", "refreshToken",
            "client_secret", "admin-client-secret", "authorization");
    private static final Set<String> SKIP_PATHS = Set.of(
            "/actuator", "/health", "/swagger-ui", "/v3/api-docs");
    private static final List<Pattern> SENSITIVE_PATTERNS = SENSITIVE_BODY_FIELDS.stream()
            .map(field -> Pattern.compile(
                    "(?i)(\"" + Pattern.quote(field) + "\"\\s*:\s*\")([^\"]{1,50})(\")"))
            .toList();

    @Value("${logging.request-response.enabled:false}")
    private boolean enabled;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) return true;
        var path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var wrappedRequest = new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT);
        var wrappedResponse = new ContentCachingResponseWrapper(response);

        logRequest(wrappedRequest);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        if (!log.isDebugEnabled()) return;

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var queryString = request.getQueryString();
        var remoteAddr = request.getRemoteAddr();

        log.debug("→ {} {}{} from {}", method, uri,
                queryString != null ? "?" + queryString : "", remoteAddr);

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var name = headerNames.nextElement();
            if (SENSITIVE_HEADERS.contains(name.toLowerCase())) {
                log.debug("  Header: {}=***", name);
            } else {
                log.debug("  Header: {}={}", name, request.getHeader(name));
            }
        }

        var body = getBody(request.getContentAsByteArray(), request.getContentType());
        if (!body.isEmpty()) {
            log.debug("  Body: {}", body);
        }
    }

    private void logResponse(ContentCachingRequestWrapper request,
                             ContentCachingResponseWrapper response, long duration) {
        if (!log.isDebugEnabled()) return;

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var status = response.getStatus();

        log.debug("← {} {} → {} ({}ms)", method, uri, status, duration);

        var body = getBody(response.getContentAsByteArray(), response.getContentType());
        if (!body.isEmpty()) {
            log.debug("  Response body: {}", body);
        }
    }

    private String getBody(byte[] content, @Nullable String contentType) {
        if (content == null || content.length == 0) return "";
        if (contentType != null && !contentType.contains("json") && !contentType.contains("text")) return "";

        var body = new String(content, 0, Math.min(content.length, MAX_BODY_LOG_SIZE), StandardCharsets.UTF_8);
        return sanitizeBody(body);
    }

    private String sanitizeBody(String body) {
        if (body == null || body.isBlank()) return body;
        var sanitized = body;
        for (var pattern : SENSITIVE_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("$1***$3");
        }
        return sanitized;
    }
}

 ```
/security 	
	/RealmRoleConverter 
```Java
package tg.ngstars.common.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            return Collections.emptyList();
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles)) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toUnmodifiableList());
    }
}
 ``` 

	/SecurityUtils ```Java
package tg.ngstars.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getClaimAsString("sub");
            if (sub != null) {
                return UUID.fromString(sub);
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String username = jwt.getClaimAsString("preferred_username");
            if (username != null) {
                return username;
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + role.toUpperCase()));
    }

    public static boolean isAdminOrManager() {
        return hasRole("ADMIN") || hasRole("MANAGER");
    }
}

 ```
src/resources/logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProperty scope="context" name="APP_NAME" source="spring.application.name" defaultValue="ng-fields"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId:-}] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>{"timestamp":"%d{yyyy-MM-dd'T'HH:mm:ss.SSS}","service":"${APP_NAME}","thread":"%thread","correlationId":"%X{correlationId:-}","level":"%level","logger":"%logger{36}","message":"%msg"}%n</pattern>
        </encoder>
    </appender>

    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="tg.ngstars" level="DEBUG"/>
        <logger name="org.springframework.security" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <root level="WARN">
            <appender-ref ref="JSON_CONSOLE"/>
        </root>
        <logger name="tg.ngstars" level="INFO"/>
    </springProfile>

    <springProfile name="test">
        <root level="WARN">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
</configuration>
=====================================================================================================================================================================================

=========================
	GATEWAY
=========================
### gateway-service/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>
    <groupId>tg.ngstars</groupId>
    <artifactId>gateway-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>gateway-service</name>
    <description>API Gateway Service</description>
    <properties>
        <java.version>25</java.version>
        <spring-cloud.version>2025.1.2</spring-cloud.version>
        <springdoc.version>3.0.3</springdoc.version>
        <sentry.version>8.14.0</sentry.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>io.sentry</groupId>
                <artifactId>sentry-bom</artifactId>
                <version>${sentry.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        <dependency>
            <groupId>io.sentry</groupId>
            <artifactId>sentry-spring-boot-starter</artifactId>
        </dependency>
		<dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

### gateway-service/src/main/java/tg.ngstars.gateway/
/config
	/FallbackController
```java
package tg.ngstars.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tg.ngstars.gateway.dto.FallbackResponse;

@RestController
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);
    private final ObjectMapper objectMapper;

    public FallbackController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/fallback")
    public Mono<Void> fallback(ServerWebExchange exchange) {
        Object routeObj = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = (routeObj instanceof org.springframework.cloud.gateway.route.Route route)
                ? route.getId() : "unknown";
        if (routeId == null) routeId = "unknown";

        log.warn("[GATEWAY] Fallback triggered for route={}", routeId);

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            FallbackResponse response = FallbackResponse.of(routeId,
                "Service " + routeId + " is temporarily unavailable. Please try again later.");
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Failed to serialize fallback response", e);
            return Mono.error(e);
        }
    }
}


```
	/GlobalExceptionHandler
```Java
package tg.ngstars.gateway.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String routeId = getRouteId(exchange);
        ProblemDetail problem = buildProblem(ex, routeId);

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(problem.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(problem);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        } catch (Exception e) {
            log.error("Failed to serialize ProblemDetail", e);
            return Mono.error(ex);
        }
    }

    private String getRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : "unknown";
    }

    private ProblemDetail buildProblem(Throwable ex, String routeId) {
        if (ex instanceof CallNotPermittedException) {
            log.warn("[GATEWAY] Circuit breaker OPEN for route={}", routeId);
            return buildProblem(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                "Circuit breaker is open for " + routeId + ". Service is temporarily unavailable.", routeId);
        }

        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
            return buildProblem(status, status.getReasonPhrase(), ex.getMessage(), routeId);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("timeout")) {
            log.warn("[GATEWAY] Request timeout for route={}: {}", routeId, ex.getMessage());
            return buildProblem(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout",
                "Service " + routeId + " did not respond in time.", routeId);
        }

        log.error("[GATEWAY] Unhandled exception on route={}", routeId, ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            "An unexpected error occurred", routeId);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, String routeId) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("routeId", routeId);
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}

```

	/KeycloakJwtAuthentificationConverter
```Java 
package tg.ngstars.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reactive JWT converter for Keycloak tokens in the WebFlux gateway.
 * <p>
 * Extracts realm_access roles from Keycloak JWT and maps them to Spring Security authorities.
 * Non-reactive equivalent: {@code tg.ngstars.common.security.RealmRoleConverter} (servlet-based).
 */
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = extractRealmRoles(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object raw = jwt.getClaim("realm_access");
        if (!(raw instanceof Map<?, ?> realmAccess)) return List.of();
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles)) return List.of();
        return roles.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toUnmodifiableList());
    }
}

```

	/RateLimiConfig
```Java 
package tg.ngstars.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            .map(principal -> principal.getName())
            .switchIfEmpty(Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown"
            ));
    }

    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown"
        );
    }
}

```

	/SecurityConfig
```Java 
package tg.ngstars.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private String allowedOrigins;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                    new KeycloakJwtAuthenticationConverter()
                ))
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
            List.of(allowedOrigins.split(",")).stream().map(String::trim).toList()
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "X-Correlation-ID"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

``

/dto
	/FallbackResponse
```Java
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


```
/filter
	/CorrelationIdFilter
```Java 
package tg.ngstars.gateway.filter;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements WebFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank() || correlationId.length() > 128) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, correlationId);
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        return chain.filter(exchange)
            .doFinally(signal -> MDC.remove(MDC_KEY));
    }
}

```

	/LoggingFilter
```Java 
package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : null;
        String correlationId = exchange.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        long startTime = System.currentTimeMillis();

        log.debug("[GATEWAY] >>> {} {} from {} | route={} | correlationId={}",
            request.getMethod(),
            request.getURI().getRawPath(),
            request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown",
            routeId != null ? routeId : "unknown",
            correlationId != null ? correlationId : "none");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();
            int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;

            if (statusCode >= 400) {
                log.warn("[GATEWAY] <<< {} {} | status={} | duration={}ms | route={}",
                    request.getMethod(),
                    request.getURI().getRawPath(),
                    statusCode,
                    duration,
                    routeId != null ? routeId : "unknown");
            } else if (duration > 3000) {
                log.warn("[GATEWAY] SLOW REQUEST: {} {} took {}ms", request.getMethod(), request.getURI().getRawPath(), duration);
            } else {
                log.debug("[GATEWAY] <<< {} {} | status={} | duration={}ms | route={}",
                    request.getMethod(),
                    request.getURI().getRawPath(),
                    statusCode,
                    duration,
                    routeId != null ? routeId : "unknown");
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}

```

	/MetricsFilter
```Java 
package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class MetricsFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(MetricsFilter.class);
    private static final int PERFORMANCE_THRESHOLD_MS = 1000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";

        return chain.filter(exchange)
            .doOnSuccess(v -> logPerformanceStats(exchange, startTime, routeId))
            .doOnError(ex -> logPerformanceStats(exchange, startTime, routeId));
    }

    private void logPerformanceStats(ServerWebExchange exchange, Instant startTime, String routeId) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        if (durationMs > PERFORMANCE_THRESHOLD_MS) {
            log.warn("[METRICS] Slow {} request: route={} path={} duration={}ms",
                method, routeId, path, durationMs);
        } else {
            log.debug("[METRICS] {} request: route={} path={} duration={}ms",
                method, routeId, path, durationMs);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }
}
```

	/RateLimitHeadersFilter
```Java 
package tg.ngstars.gateway.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class RateLimitHeadersFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitHeadersFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends org.springframework.core.io.buffer.DataBuffer> body) {
                addRouteHeaders(exchange);
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private void addRouteHeaders(ServerWebExchange exchange) {
        try {
            String routeId = getRouteId(exchange);
            ServerHttpResponse response = exchange.getResponse();

            if (routeId != null && !"unknown".equals(routeId)) {
                response.getHeaders().add("X-Route-ID", routeId);
                response.getHeaders().add("X-Route-ID-Date", Instant.now().toString());
            }
        } catch (Exception e) {
            log.debug("Failed to add route headers: {}", e.getMessage());
        }
    }

    private String getRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

```

	/TimeoutFilter
```Java 
package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class TimeoutFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TimeoutFilter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();

        return chain.filter(exchange)
            .doOnSuccess(v -> logResponseTime(exchange, startTime))
            .doOnError(ex -> logResponseTime(exchange, startTime));
    }

    private void logResponseTime(ServerWebExchange exchange, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";

        if (durationMs > TIMEOUT.toMillis()) {
            log.warn("[TIMEOUT] Slow request from {} route={} duration={}ms",
                exchange.getRequest().getRemoteAddress(), routeId, durationMs);
        } else {
            log.debug("[TIMEOUT] Request processed in {}ms for route={}",
                durationMs, routeId);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
```
/health
	/DownstreamHealthIndicator 
```Java
package tg.ngstars.gateway.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DownstreamHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(DownstreamHealthIndicator.class);

    private final WebClient webClient;

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${CLIENT_SERVICE_URL:http://localhost:8082}")
    private String clientServiceUrl;

    @Value("${INTERVENTION_SERVICE_URL:http://localhost:8083}")
    private String interventionServiceUrl;

    @Value("${MEDIA_SERVICE_URL:http://localhost:8084}")
    private String mediaServiceUrl;

    @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8085}")
    private String notificationServiceUrl;

    @Value("${REPORT_SERVICE_URL:http://localhost:8086}")
    private String reportServiceUrl;

    public DownstreamHealthIndicator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Health health() {
        Map<String, String> services = new LinkedHashMap<>();
        services.put("auth-service", authServiceUrl);
        services.put("client-service", clientServiceUrl);
        services.put("intervention-service", interventionServiceUrl);
        services.put("media-service", mediaServiceUrl);
        services.put("notification-service", notificationServiceUrl);
        services.put("report-service", reportServiceUrl);

        Map<String, String> results = new java.util.concurrent.ConcurrentHashMap<>();

        Flux.fromIterable(services.entrySet())
            .flatMap(entry -> webClient.get()
                .uri(entry.getValue() + "/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .map(status -> Map.entry(entry.getKey(), "UP"))
                .onErrorResume(e -> Mono.just(Map.entry(entry.getKey(), "DOWN"))), 1)
            .doOnNext(entry -> results.put(entry.getKey(), entry.getValue()))
            .blockLast(Duration.ofSeconds(5));

        boolean allUp = results.values().stream().noneMatch("DOWN"::equals);

        Health.Builder builder = allUp ? Health.up() : Health.down();
        results.forEach(builder::withDetail);
        return builder.build();
    }
}

```

/GatewayServiceApplication
```Java
package tg.ngstars.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
```

=====================================================================================================================================================================================

=========================
	AUTH-SERVICE
=========================