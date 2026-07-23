# Security

**Mis à jour :** 23/07/2026

## Authentication
- Keycloak OAuth2/JWT for all services
- Gateway validates JWT tokens
- Role-based access: ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER
- Multi-tenant isolation: CLIENT_ADMIN sees only company data

## Key Security Features
- **Rate limiting**: Redis-based on gateway (per-user and per-IP)
- **Circuit breaker**: Resilience4j on inter-service calls
- **File upload security**: MIME magic byte validation, size limits, path traversal protection
- **CSV injection prevention**: Tab prefix for special characters in CSV exports
- **Correlation IDs**: X-Correlation-ID header propagated across services
- **Secrets sanitization**: Actuator env endpoint filters sensitive keys
- **RFC 7807 Problem Detail**: Gateway returns standardized error responses (`application/problem+json`)
- **Optimistic locking**: `@Version` on all critical entities
- **Password verification**: changePassword verifies old password via Keycloak token endpoint
- **Role cleanup**: updateUser removes old Keycloak roles before adding new (prevents accumulation)
- **Temp password not logged**: addCompanyUser no longer logs generated passwords

## Security Headers
- X-Content-Type-Options: nosniff
- Content-Disposition: attachment for non-image files

## Push Security
- Firebase Admin SDK is conditional (`firebase.enabled` toggle)
- PushServiceNoop fallback when Firebase disabled
- No Firebase credentials required in development
