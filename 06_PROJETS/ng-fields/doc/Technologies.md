---
tags:
  - projet
  - ng-fields
  - technologies
created: 2026-07-03
status: v5.0
---

# Stack Technique — NG-Fields

**Mis à jour :** 23/07/2026 (Backend Complet)

## Architecture microservices

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌────────────┐     ┌──────────┐
│ Gateway  │────▶│ Auth     │────▶│ Client   │────▶│Intervention│────▶│ Media    │
│ :8080    │     │ :8081    │     │ :8082    │     │ :8083      │     │ :8084    │
│ WebFlux  │     │ MVC      │     │ MVC      │     │ MVC        │     │ MVC      │
└──────────┘     └──────────┘     └──────────┘     └────────────┘     └──────────┘
                                                              │
                                                    ┌─────────┴─────────┐
                                                    ▼                   ▼
                                              ┌──────────┐       ┌──────────┐
                                              │Notif     │       │ Report   │
                                              │:8085     │       │ :8086    │
                                              │MVC       │       │ MVC      │
                                              └──────────┘       └──────────┘
```

Tous les appels clients passent par le **Gateway** (Spring Cloud Gateway WebFlux). Chaque service expose son API REST derrière le gateway. Le gateway gère l'authentification JWT, le rate limiting (Redis), et le routage.

---

## Stack retenue

| Couche | Technologie | Version | Usage |
|--------|-------------|---------|-------|
| Backend | Spring Boot | 4.1.0 | API REST (7 microservices + shared-lib) |
| Gateway | Spring Cloud Gateway | 2025.1.2 | Routage, auth, rate limiting |
| Langage backend | Java | 25 | — |
| Build | Maven | Wrapper | — |
| ORM | Spring Data JPA + Hibernate | — | Persistance, `ddl-auto: update` |
| Auth | Keycloak (OAuth2/OIDC) | 26.6.4 | SSO + RBAC |
| API Docs | SpringDoc OpenAPI | 3.0.3 | Swagger UI |
| PDF | OpenPDF | 1.4.1 | Génération rapports |
| QR Code | ZXing | 3.5.3 | QR dans les PDF |
| Base de données | PostgreSQL | 18 | Principale |
| Migrations | Hibernate DDL | — | `ddl-auto: update` (Flyway supprimé) |
| Cache / Rate Limiting | Redis | 7+ | Gateway |
| Email | Resend API | — | Emails transactionnels |
| Push | Firebase Admin SDK | 9.2.0 | Push notifications (conditional) |
| SSE | SseEmitter | — | Real-time dashboard |
| Logs | Logback + logstash-logback-encoder | 8.0 | JSON structuré |
| Tests | JUnit 5 + Mockito | — | 65 unit tests |
| Mobile | Flutter | 3.x | App terrain (**Non démarré**) |
| Langage mobile | Dart | 3.x | — (**Non démarré**) |
| State management | Riverpod | — | Flutter |
| Navigation | GoRouter | — | Flutter |
| Base locale | Drift (SQLite) | — | Mode hors-ligne |
| Web | Angular | 22+ | Dashboard manager |
| CI/CD | GitHub Actions | — | Pipeline |
| Monitoring | Sentry | free | Errors |

---

## Services backend

| Service | Port | Technologie | Dépendances |
|---------|------|-------------|-------------|
| gateway-service | 8080 | Spring Cloud Gateway (WebFlux) | Redis, Keycloak |
| auth-service | 8081 | Spring Boot MVC | PostgreSQL (schema `auth`), Keycloak Admin API, Resend |
| client-service | 8082 | Spring Boot MVC | PostgreSQL (schema `client`) |
| intervention-service | 8083 | Spring Boot MVC | PostgreSQL (schema `intervention`), media-service, OpenPDF, Resend |
| media-service | 8084 | Spring Boot MVC | Filesystem (`./uploads`) |
| notification-service | 8085 | Spring Boot MVC | Firebase Admin SDK (conditional), Resend |
| report-service | 8086 | Spring Boot MVC | intervention-service (REST), OpenPDF |

---

## Stack Backend — Spring Boot

| Composant | Technologie |
|-----------|-------------|
| Framework | Spring Boot 4.1.0 |
| Runtime | Java 25 |
| Build | Maven |
| ORM | Spring Data JPA + Hibernate (`ddl-auto: update`) |
| Auth | Spring Security + OAuth2 Resource Server |
| Gateway | Spring Cloud Gateway WebFlux + CircuitBreaker + GlobalExceptionHandler (RFC 7807) |
| Migrations | Hibernate DDL (`ddl-auto: update`) — Flyway supprimé |
| Validation | Jakarta Validation + Hibernate Validator |
| Documentation | SpringDoc OpenAPI (Swagger) 3.0.3 |
| PDF | OpenPDF + ZXing |
| Cache / Rate Limiting | Redis (Spring Data Redis Reactive) |
| Email | Resend API |
| Push | Firebase Admin SDK (`@ConditionalOnProperty`) |
| SSE | SseEmitter (Server-Sent Events) |
| Logs | Logback + logstash-logback-encoder 8.0 (JSON structuré) |
| Tests | JUnit 5 + Mockito — **65 unit tests** |

---

## Base de Données

| Composant | Technologie |
|-----------|-------------|
| Primary | PostgreSQL 18 (localhost) |
| Cache / Rate Limiting | Redis 7+ |
| Files | Filesystem (`./uploads/`) |
| Mobile offline | Drift (SQLite) |
| Schema management | Hibernate `ddl-auto: update` |

---

## Infrastructure

| Composant | Technologie |
|-----------|-------------|
| CI/CD | GitHub Actions |
| Monitoring | Sentry (free tier) |

---

## Budget

| Poste | Coût |
|-------|------|
| GitHub (free) | 0 € |
| Sentry (Free) | 0 € |
| **Total projet** | **0 €** |

---

## Sécurité

| Exigence | Implémentation |
|----------|----------------|
| HTTPS | TLS 1.3 |
| Auth | JWT + Refresh Token + RBAC (Keycloak) |
| Offline | Chiffrement local (Drift encrypt) |
| Audit trail | Table `audit_logs` (schema `auth`) |
| RGPD | Consentement + droit effacement + registre |
| Password | Vérification old password via Keycloak token endpoint |
| Roles | Suppression anciens rôles avant ajout (évite accumulation) |

---

_Version 5.0 — 23/07/2026 (Backend Complet)_
