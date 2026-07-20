# NG-Fields Backend - Documentation

## Architecture

Microservices architecture with 7 services + 1 shared library.

| Service | Port | Description |
|---------|------|-------------|
| gateway-service | 8080 | API Gateway (Spring Cloud Gateway) |
| auth-service | 8081 | Authentication & User Management (Keycloak) |
| client-service | 8082 | Client Management |
| intervention-service | 8083 | Intervention/Work Order Management |
| media-service | 8084 | File Upload & Storage |
| notification-service | 8085 | Email Notifications |
| report-service | 8086 | Analytics & PDF/CSV Reports |
| shared-lib | - | Shared DTOs, entities, exceptions, security |

## Quick Start

1. Copy `.env.example` to `.env` and fill in values
2. Start infrastructure: `docker compose up -d` (PostgreSQL + Redis + Keycloak)
3. Build: `mvn clean install`
4. Run each service on its port

## Documentation Files

- [API Endpoints](API_ENDPOINTS.md)
- [Security](SECURITY.md)
- [Database](DATABASE.md)
- [Testing](TESTING.md)
- [Deployment](DEPLOYMENT.md)
