# NG-Fields Backend - Documentation

**Mis à jour :** 23/07/2026 (Backend Complet)

## Architecture

Microservices architecture with 7 services + 1 shared library.

| Service | Port | Description |
|---------|------|-------------|
| gateway-service | 8080 | API Gateway (Spring Cloud Gateway) |
| auth-service | 8081 | Authentication & User Management (Keycloak) |
| client-service | 8082 | Client Management |
| intervention-service | 8083 | Intervention/Work Order Management |
| media-service | 8084 | File Upload & Storage (AV scanning, compression, quota) |
| notification-service | 8085 | Email Notifications (Resend) + Push (Firebase) |
| report-service | 8086 | Analytics & PDF/CSV Reports + Templates |
| shared-lib | - | Shared DTOs, entities, exceptions, security, logging, logstash-logback-encoder |

## Quick Start

1. Copy `.env.example` to `.env` and fill in values
2. Start infrastructure: PostgreSQL + Redis + Keycloak (see `Doc/Setup.md`)
3. Build: `cd Backend && mvn clean install`
4. Run each service on its port

## Documentation Files

- [API Endpoints](API_ENDPOINTS.md) — 91 endpoints
- [Security](SECURITY.md) — Brute force, AV scanning, HTML sanitization, rate limiting
- [Database](DATABASE.md) — Flyway migrations + Hibernate validate
- [Testing](TESTING.md) — 72 unit tests
- [Deployment](DEPLOYMENT.md) — Flyway, env vars, monitoring
- [Architecture](../ARCHITECTURE.md)

## Testing

```bash
# All tests
cd Backend && mvn test

# Single service
cd Backend/intervention-service && mvn test
```

72 unit tests across 4 services (intervention: 49, auth: 21, notification: 3, report: 7).

## Postman

Collection: `Backend/postman/NG-Fields API.postman_collection.json` (91 endpoints)
