# Deployment

## Development Setup

### Prerequisites
- Java 25
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 18+
- Redis 7+
- Keycloak 26.6.4

### Quick Start

1. Start infrastructure:
```bash
docker compose up -d
```
This starts PostgreSQL, Redis, and Keycloak on their default ports.

2. Create the Keycloak realm:
```
Access Keycloak Admin Console at http://localhost:8088
Import the realm configuration (or create manually)
Realm: ng-fields
Client: ng-fields-backend (confidential, service account enabled)
```

3. Build all services:
```bash
cd Backend
# Build shared-lib first (required by all services)
cd shared-lib && mvn clean install -q && cd ..
# Build all services
mvn clean install
```

4. Run services (each in a separate terminal):
```bash
# Gateway (port 8080)
cd gateway-service && mvn spring-boot:run

# Auth (port 8081)
cd auth-service && mvn spring-boot:run

# Client (port 8082)
cd client-service && mvn spring-boot:run

# Intervention (port 8083)
cd intervention-service && mvn spring-boot:run

# Media (port 8084)
cd media-service && mvn spring-boot:run

# Notification (port 8085)
cd notification-service && mvn spring-boot:run

# Report (port 8086)
cd report-service && mvn spring-boot:run
```

### Service Ports

| Service | Port |
|---------|------|
| Gateway | 8080 |
| Auth | 8081 |
| Client | 8082 |
| Intervention | 8083 |
| Media | 8084 |
| Notification | 8085 |
| Report | 8086 |
| Keycloak | 8088 |
| PostgreSQL | 5432 |
| Redis | 6379 |

---

## Production Deployment

### Environment Variables

Copy `.env.example` to `.env` and fill in production values. Key variables:

| Variable | Description |
|----------|-------------|
| `KEYCLOAK_ISSUER_URI` | Keycloak realm URI |
| `KEYCLOAK_CLIENT_SECRET` | Backend client secret |
| `POSTGRES_HOST` | Database host |
| `POSTGRES_PASSWORD` | Database password |
| `REDIS_PASSWORD` | Redis password |
| `SENTRY_DSN` | Sentry error tracking DSN |
| `SPRING_PROFILES_ACTIVE` | Set to `prod` |

### Production Profile

When `spring.profiles.active=prod`:
- Swagger UI disabled
- Logging level: WARN
- Actuator endpoints limited to health, info
- JSON console log format for log aggregation

### Health Checks

All services expose Actuator endpoints:
- `GET /actuator/health` -- Liveness + Readiness
- `GET /actuator/info` -- Application info
- `GET /actuator/metrics` -- Metrics (when enabled)

### Graceful Shutdown

All services are configured with graceful shutdown (30s timeout). When stopping:
1. The service stops accepting new requests
2. In-flight requests complete (up to 30s)
3. The service exits

### CI/CD

GitHub Actions workflow (`.github/workflows/backend.yml`):
- Triggers on push/PR to `main`
- JDK 25 setup
- Runs `mvn -B verify`
- Uploads test results as artifacts

---

## Docker Compose (Infrastructure)

```yaml
# docker-compose.yml at Backend root
version: '3.8'
services:
  postgres:
    image: postgres:18-alpine
    ports: ["5432:5432"]
    environment:
      POSTGRES_DB: ngfields
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  keycloak:
    image: quay.io/keycloak/keycloak:26.6.4
    ports: ["8088:8080"]
    command: start-dev
    environment:
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: admin
```

---

## Monitoring

- **Sentry**: Error tracking with 20% trace sample rate
- **Prometheus metrics**: Available at `/actuator/prometheus`
- **Correlation IDs**: Propagated via `X-Correlation-ID` header
- **Structured logging**: JSON format in production for log aggregation
