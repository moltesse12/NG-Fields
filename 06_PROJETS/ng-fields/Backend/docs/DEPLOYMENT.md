# Deployment

**Mis à jour :** 23/07/2026

## Development Setup

### Prerequisites
- Java 25
- Maven 3.9+
- PostgreSQL 18+
- Redis 7+
- Keycloak 26.0.9

### Quick Start

1. Create the database:
```sql
CREATE USER ng_fields_user WITH PASSWORD 'Pg_ng-fields1234';
CREATE DATABASE ng_fields OWNER ng_fields_user;
```

2. Start Keycloak:
```bash
cd keycloak-26.0.9/bin
./kc.bat start-dev --http-port=8088
```

3. Import realm: `Doc/infra/keycloak/realm-export.json`

4. Build all services:
```bash
cd Backend
# Build shared-lib first
cd shared-lib && mvn clean install -q && cd ..
# Build all services
mvn clean install
```

5. Run services (each in a separate terminal):
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

**Note:** Schema is managed by **Flyway** (auth, client, intervention, report). Migrations run automatically at startup. Hibernate validates schema but does not create/alter tables.

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
| `LOG_REQUEST_RESPONSE` | Enable request/response body logging (`true`/`false`, default `false`) |
| `FIREBASE_ENABLED` | Enable Firebase push notifications (`true`/`false`, default `false`) |
| `FIREBASE_SERVICE_ACCOUNT_PATH` | Path to Firebase service account JSON |

### Production Profile

When `spring.profiles.active=prod`:
- Swagger UI disabled
- Logging level: WARN
- Actuator endpoints limited to health, info, loggers
- JSON console log format for log aggregation

### Health Checks

All services expose Actuator endpoints:
- `GET /actuator/health` — Liveness + Readiness
- `GET /actuator/info` — Application info
- `GET /actuator/metrics` — Metrics (Prometheus)
- `GET /actuator/loggers` — Log level management

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

## Schema Management

Schema is managed by **Flyway** with migration scripts in `db/migration/V1__init.sql` for auth, client, intervention, and report services. Hibernate runs in `ddl-auto: validate` mode.

| Service | Strategy | Tables |
|---------|----------|--------|
| auth-service | Flyway + validate | users, audit_logs, companies, company_users, company_access_log, failed_login_attempts |
| client-service | Flyway + validate | clients, contacts |
| intervention-service | Flyway + validate | interventions, intervention_items, intervention_photos |
| report-service | Flyway + validate | pdf_templates, email_templates |
| media-service | Hibernate validate | None (disk storage) |
| notification-service | Hibernate validate | email_logs |

---

## Monitoring

- **Sentry**: Error tracking with 20% trace sample rate
- **Prometheus metrics**: Available at `/actuator/prometheus` (all services)
- **Loggers management**: `/actuator/loggers` (all services)
- **Correlation IDs**: Propagated via `X-Correlation-ID` header
- **Structured logging**: JSON format in production for log aggregation
- **Request/response logging**: Toggle via `LOG_REQUEST_RESPONSE=true` env var (shared-lib filter)
