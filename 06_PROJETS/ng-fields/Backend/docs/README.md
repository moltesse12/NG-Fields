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
| media-service | 8084 | File Upload & Storage |
| notification-service | 8085 | Email Notifications (Resend) + Push (Firebase) |
| report-service | 8086 | Analytics & PDF/CSV Reports + Templates |
| shared-lib | - | Shared DTOs, entities, exceptions, security, logstash-logback-encoder |

## Quick Start

1. Copy `.env.example` to `.env` and fill in values
2. Start infrastructure: PostgreSQL + Redis + Keycloak (see `Doc/Setup.md`)
3. Build: `cd Backend && mvn clean install`
4. Run each service on its port

## Documentation Files

- [API Endpoints](API_ENDPOINTS.md) — 90 endpoints
- [Security](SECURITY.md)
- [Database](DATABASE.md) — `ddl-auto: update` (Hibernate)
- [Testing](TESTING.md) — 65 unit tests
- [Deployment](DEPLOYMENT.md)
- [Architecture](../ARCHITECTURE.md)

## Testing

```bash
# All tests
cd Backend && mvn test

# Single service
cd Backend/intervention-service && mvn test
```

65 unit tests across 3 services (intervention: 49, auth: 21, notification: 3).

## Postman

Collection: `Backend/postman/NG-Fields API.postman_collection.json` (90 endpoints)
