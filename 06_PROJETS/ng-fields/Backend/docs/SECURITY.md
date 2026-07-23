# Security

**Mis à jour :** 23/07/2026

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
- **Password verification**: changePassword verifies old password via Keycloak token endpoint
- **Role cleanup**: updateUser removes old Keycloak roles before adding new (prevents accumulation)
- **Temp password not logged**: addCompanyUser no longer logs generated passwords
- **Intervention lock**: `InterventionLockManager` prevents concurrent modifications
- **Schedule conflict detection**: Overlap validation before intervention assignment
- **Image metadata stripping**: `ImageMetadataStripper` removes EXIF/GPS data from uploaded photos

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
