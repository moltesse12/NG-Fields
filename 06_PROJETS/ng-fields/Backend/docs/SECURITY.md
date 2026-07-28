# Security

**Mis à jour :** 24/07/2026 (Audit sécurité complet)

## Authentication
- Keycloak OAuth2/JWT for all services
- Gateway validates JWT tokens
- Role-based access: ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER
- Multi-tenant isolation: CLIENT_ADMIN sees only company data

## Key Security Features
- **Rate limiting**: Redis-based on gateway (per-user and per-IP) + Bucket4j on auth-service
- **Brute force protection**: OWASP hybrid — 10 failed attempts → 30min lockout (`BruteForceProtectionService` + `FailedLoginAttempt` entity)
- **Email verification**: JWT stateless tokens (no separate table)
- **Circuit breaker**: Resilience4j on inter-service calls (client→intervention, report→intervention)
- **Antivirus scanning**: ClamAV on all file uploads (media-service)
- **HTML sanitization**: Strip `<script>`, event handlers, `javascript:`, `<iframe>`, `<object>`, `<embed>`, `<form>`, CSS `expression()` on email/PDF templates (report-service)
- **File upload security**: MIME magic byte validation, size limits, path traversal protection
- **Company quota**: Per-company file upload quota (media-service)
- **File ownership**: Verification before file deletion (media-service)
- **CSV injection prevention**: Tab prefix for special characters in CSV exports
- **Correlation IDs**: X-Correlation-ID header propagated across services
- **Request/response logging**: `RequestResponseLoggingFilter` in shared-lib (toggled via `LOG_REQUEST_RESPONSE=true`)
- **Secrets sanitization**: Actuator env endpoint filters sensitive keys
- **RFC 7807 Problem Detail**: Gateway returns standardized error responses (`application/problem+json`)
- **Optimistic locking**: `@Version` on all critical entities
- **Password verification**: changePassword verifies old password via Keycloak token endpoint (throws if not configured)
- **Role cleanup**: updateUser removes old Keycloak roles before adding new (prevents accumulation)
- **Temp password not logged**: addCompanyUser no longer logs generated passwords
- **Intervention lock**: `InterventionLockManager` prevents concurrent modifications
- **Schedule conflict detection**: Overlap validation before intervention assignment
- **Image metadata stripping**: `ImageMetadataStripper` removes EXIF/GPS data from uploaded photos
- **Email XSS protection**: HTML escape of `firstName` in all email templates (intervention-notification, password-reset, welcome, intervention-assigned, intervention-completed)
- **KC rollback**: `registerClient` cleans up Keycloak user if database insert fails
- **HMAC key validation**: `EmailVerificationService` validates HMAC key length ≥32 bytes at startup (`@PostConstruct`)
- **Dynamic rate limits**: `RateLimitConfig` uses `compute()` for per-company rate limits (thread-safe)
- **BruteForce readOnly**: `isIpBlocked` uses `@Transactional(readOnly=true)`
- **Route ID logging**: Gateway filters use `GATEWAY_ROUTE_ATTR` + `Route` object (no `ClassCastException`)
- **Correlation ID**: `@Order(Ordered.HIGHEST_PRECEDENCE)` + `.headers(h -> h.set())` (no duplicate headers)
- **Gateway error handling**: `GlobalExceptionHandler` injects Spring `ObjectMapper` for RFC 7807
- **Gateway timeout fix**: `LOWEST_PRECEDENCE-1/-2` instead of `+1/+2` (integer overflow prevention)
- **Thread-safe health**: `DownstreamHealthIndicator` uses `ConcurrentHashMap` + serialized flatMap
- **Rate limit headers**: `ServerHttpResponseDecorator` ensures headers set before body

## Security Headers
- X-Content-Type-Options: nosniff
- Content-Disposition: attachment for non-image files

## Push Security
- Firebase Admin SDK is conditional (`firebase.enabled` toggle)
- PushServiceNoop fallback when Firebase disabled
- No Firebase credentials required in development

## Audit Trail
- `company_access_log`: All company/user operations logged (auth-service)
- `FileAccessAuditLogger`: All file operations logged (media-service)
- `EmailAuditLogger`: All email operations logged (notification-service)
- `DeadLetterQueueService`: Failed emails queued for investigation (notification-service)

## Rate Limiting Summary
| Service | Mechanism | Limits |
|---------|-----------|--------|
| Gateway | Redis + Resilience4j | Per-user + per-IP (configurable per route) |
| Auth | Bucket4j | Registration, login, reset-password |
| Notification | RateLimiter (Bucket4j) | 50 emails/hour per recipient |
