# Security

## Authentication
- Keycloak OAuth2/JWT for all services
- Gateway validates JWT tokens
- Role-based access: ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL

## Key Security Features
- **Rate limiting**: Redis-based on gateway (per-user and per-IP)
- **Circuit breaker**: Resilience4j on inter-service calls
- **File upload security**: MIME magic byte validation, size limits, path traversal protection
- **CSV injection prevention**: Tab prefix for special characters in CSV exports
- **Correlation IDs**: X-Correlation-ID header propagated across services
- **Secrets sanitization**: Actuator env endpoint filters sensitive keys

## Security Headers
- X-Content-Type-Options: nosniff
- Content-Disposition: attachment for non-image files
