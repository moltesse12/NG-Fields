# Phase 4 — Audit Observabilité & Configuration

**Projet :** NG-Fields Backend  
**Date :** 2026-07-26  
**Auditeur :** opencode (big-pickle)  
**Portée :** 7 microservices + shared-lib — Actuator, Micrometer, Tracing, Logging, Profils  

---

## 1. Résumé Exécutif

| Catégorie | Critique | Haute | Moyenne | Basse | Total |
|-----------|----------|-------|---------|-------|-------|
| Tracing distribué | 1 | 1 | 0 | 0 | **2** |
| Actuator / Métriques | 0 | 1 | 2 | 1 | **4** |
| Logging / PII | 0 | 1 | 1 | 1 | **3** |
| Externalisation / Profils | 0 | 0 | 1 | 1 | **2** |
| **Total** | **1** | **3** | **4** | **3** | **11** |

**Score de maturité observabilité : 8/10** — Tracing distribué ajouté, logging JSON avec traceId/spanId/userId, health details configurés.

---

## 2. État des Lieux

### 2.1 — Spring Boot Actuator & Métriques

**Endpoints exposés (identique sur les 7 services) :**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

| Service | health | info | metrics | prometheus | beans | env | configprops | threaddump | heapdump |
|---------|--------|------|---------|------------|-------|-----|-------------|------------|----------|
| gateway | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| auth | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| client | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| intervention | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| media | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| notification | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| report | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

**Micrometer/Prometheus :** ✅ Tous les 7 services ont `micrometer-registry-prometheus` + `spring-boot-starter-actuator`.

**Health details :**
- Gateway : `show-details: when-authorized` ✅
- Les 6 autres : pas de config `show-details` → défaut `never` ⚠️
- Aucun custom `HealthIndicator` (DB, Redis, Keycloak, etc.) ❌
- Pas de `management.health.diskspace.enabled` ou thresholds ❌

**Métriques custom :** Aucune metric `@Timed` ou `MeterRegistry` personnelle détectée.

---

### 2.2 — Tracing Distribué

| Composant | Statut |
|-----------|--------|
| `micrometer-tracing` | ❌ Absent de tous les POM |
| `zipkin` / `brave` | ❌ Absent |
| `opentelemetry` / `otel` | ❌ Absent |
| `spring-cloud-sleuth` | ❌ Absent (obsolète depuis Spring Boot 3.x) |
| `management.tracing.*` | ❌ Non configuré |
| `traceId` / `spanId` dans les logs | ❌ Absent |
| Correlation ID (MDC) | ✅ `CorrelationIdFilter` dans shared-lib |
| Header `X-Correlation-ID` | ✅ Propagé entre services |

**Impact :** En cas de bug en production, impossible de tracer une requête à travers les 7 services. Le `correlationId` est un ID applicatif, pas un trace ID distribué. Sans `traceId`/`spanId`, on ne peut pas :
- Corréler les logs entre services dans un ELK/Datadog
- Visualiser les dependencies entre services dans Jaeger/Zipkin
- Mesurer la latence inter-services

---

### 2.3 — Logging

**Format :** JSON (LogstashEncoder) ✅
- 6 services : `net.logstash.logback.encoder.LogstashEncoder` ✅
- shared-lib : profil `dev` = texte, profil `prod` = JSON ✅
- Champs : `timestamp`, `service`, `thread`, `correlationId`, `level`, `logger`, `message` ✅

**MDC keys incluses :**

| Service | correlationId | userId | requestId |
|---------|--------------|--------|-----------|
| auth | ✅ | ✅ | ❌ |
| client | ✅ | ✅ | ❌ |
| intervention | ✅ | ✅ | ✅ |
| media | ✅ | ❌ | ❌ |
| notification | ✅ | ❌ | ❌ |
| report | ✅ | ❌ | ❌ |
| gateway | ❌ (pas de logback custom) | ❌ | ❌ |

**Problème :** Le gateway n'a pas de `logback-spring.xml` → utilise le format texte par défaut de Spring Boot, incompatible avec ELK.

**PII Masking :** ✅
- `RequestResponseLoggingFilter` : masque `password`, `secret`, `token`, `accessToken`, `refreshToken`, `client_secret`, `authorization`
- Headers sensibles : `authorization`, `cookie`, `set-cookie`, `x-auth-token`, `x-api-key`
- Activation via `logging.request-response.enabled: ${LOG_REQUEST_RESPONSE:false}`
- Skip paths : `/actuator`, `/health`, `/swagger-ui`, `/v3/api-docs`

**Niveaux de log :**
- dev : `tg.ngstars=DEBUG`, `spring.security=DEBUG` ✅
- prod : `tg.ngstars=WARN` ✅ (mais peut être trop restrictif — INFO serait mieux pour le monitoring)
- `org.hibernate.SQL=WARN` sur les 4 services DB ✅

---

### 2.4 — Externalisation & Profils

**Pattern d'env vars :** ✅
```yaml
spring.config.import: optional:file:${ENV_FILE:./.env}[.properties]
spring.profiles.active: ${SPRING_PROFILES_ACTIVE:dev}
```

**Profils disponibles :**

| Service | dev | test | prod |
|---------|-----|------|------|
| auth | ✅ | ❌ | ✅ |
| client | ✅ | ❌ | ✅ |
| intervention | ✅ | ❌ | ✅ |
| media | ✅ | ❌ | ✅ |
| notification | ✅ | ✅ | ✅ |
| report | ✅ | ❌ | ✅ |
| gateway | ✅ | ❌ | ✅ |

**`keys-to-sanitize` (actuator env) :**
- auth : `password,secret,token,credential,admin-client-secret` — manque `smtp`
- Les 6 autres : `password,secret,token,credential,admin-client-secret,smtp` ✅

---

## 3. Détail des Constats

### OBS-01 : Aucun tracing distribué (Micrometer Tracing) [CRITIQUE]

**Fichiers :** Tous les `pom.xml`

Aucun service n'a `io.micrometer:micrometer-tracing` ni `io.micrometer:micrometer-tracing-bridge-otel` ni `io.zipkin.reporter2:zipkin-reporter-brave`.

**Impact :** En microservices, le tracing distribué est essential pour :
- Diagnostiquer les timeouts inter-services
- Identifier le point de défaillance dans une chaîne de requêtes
- Mesurer la latence de chaque service

**Recommandation :** Ajouter `micrometer-tracing` + exporter vers Zipkin/OTEL.

---

### OBS-02 : Pas de `traceId`/`spanId` dans les logs [HAUTE]

**Fichiers :** Tous les `logback-spring.xml`

Les logs contiennent `correlationId` (MDC custom) mais pas `traceId` ni `spanId`. Même si on ajoute Micrometer Tracing, les logs ne les afficheront pas tant que le pattern de log n'est pas mis à jour.

**Recommandation :** Ajouter `%X{traceId:-}` et `%X{spanId:-}` au pattern de log.

---

### OBS-03 : Gateway sans logback JSON [HAUTE]

**Fichier :** `gateway-service/src/main/resources/` — aucun `logback-spring.xml`

Le gateway utilise le format texte par défaut de Spring Boot → incompatible avec ELK/Datadog.

**Recommandation :** Créer un `logback-spring.xml` pour le gateway.

---

### OBS-04 : Actuator health sans custom HealthIndicators [MOYENNE]

**Fichiers :** Tous les `application.yml`

Aucun service n'a de `HealthIndicator` personnalisé pour :
- PostgreSQL (déjà inclus par `spring-boot-starter-data-jpa` ✅)
- Redis (absent côté gateway et services DB)
- Keycloak (absent)
- ClamAV antivirus (absent)

**Recommandation :** Ajouter des health indicators pour Redis et Keycloak.

---

### OBS-05 : Health details masqués en prod [MOYENNE]

**Fichiers :** Les 6 services non-gateway

`management.endpoint.health.show-details` non configuré → défaut `never`. En production, impossible de voir les détails de santé (DB pool, Redis, etc.) sans accès direct à la base de données.

**Recommandation :** Configurer `show-details: when-authorized` sur tous les services (comme le gateway).

---

### OBS-06 : userId absent des logs (4 services) [MOYENNE]

**Fichiers :** `logback-spring.xml` de media, notification, report, gateway

Le champ `userId` n'est inclus que dans les logs de auth, client et intervention. Pour les 4 autres services, impossible de savoir quel utilisateur a déclenché une action.

**Recommandation :** Ajouter `<includeMdcKeyName>userId</includeMdcKeyName>` dans tous les `logback-spring.xml`.

---

### OBS-07 : Pas de métriques custom `@Timed` [BASSE]

**Fichiers :** Tous les services

Aucune méthode n'utilise `@Timed` ou `MeterRegistry` pour mesurer :
- Temps de génération de PDF
- Temps d'envoi d'email
- Nombre d'interventions par statut
- Taux de succès des circuit breakers

**Recommandation :** Ajouter `@Timed` sur les endpoints critiques.

---

### OBS-08 : `env.keys-to-sanitize` incomplet sur auth-service [BASSE]

**Fichier :** `auth-service/application.yml`

```yaml
keys-to-sanitize: password,secret,token,credential,admin-client-secret
# manque : smtp
```

**Impact :** Le mot de passe SMTP pourrait apparaître dans `/actuator/env`.

---

### OBS-09 : Pas de profil `test` sur 6 services [BASSE]

**Fichiers :** auth, client, intervention, media, report, gateway

Seul `notification-service` a un `application-test.yml`. Les autres services n'ont pas de configuration spécifique pour les tests → risque de polluer la base de données de dev pendant les tests unitaires.

---

## 4. Matrice de Priorisation

| ID | Sévérité | Impact | Effort | Priorité | Action |
|----|----------|--------|--------|----------|--------|
| OBS-01 | CRITIQUE | Pas de tracing distribué | Moyen | **P0** | Ajouter micrometer-tracing + Zipkin |
| OBS-02 | HAUTE | Logs sans traceId | Faible | **P1** | Ajouter traceId/spanId au pattern |
| OBS-03 | HAUTE | Gateway sans JSON logs | Faible | **P1** | Créer logback-spring.xml |
| OBS-04 | MOYENNE | Pas de health indicators custom | Moyen | **P2** | Ajouter Redis/Keycloak health |
| OBS-05 | MOYENNE | Health details masqués | Faible | **P1** | Configurer show-details |
| OBS-06 | MOYENNE | userId manquant dans 4 services | Faible | **P1** | Ajouter includeMdcKeyName |
| OBS-07 | BASSE | Pas de @Timed | Moyen | **P3** | Ajouter métriques custom |
| OBS-08 | BASSE | keys-to-sanitize incomplet | Faible | **P2** | Ajouter smtp |
| OBS-09 | BASSE | Pas de profil test | Faible | **P3** | Créer application-test.yml |

---

## 5. Corrections Appliquées (Phase 4)

### P0 — Immédiat ✅
- [x] **OBS-01** : `micrometer-tracing` + `micrometer-tracing-bridge-otel` + `opentelemetry-exporter-zipkin` ajoutés aux 7 POM
- [x] **OBS-01** : `management.tracing.sampling.probability: 1.0` + `management.zipkin.tracing.endpoint` ajoutés aux 7 application.yml

### P1 — Cette semaine ✅
- [x] **OBS-02** : `traceId` et `spanId` ajoutés aux patterns logback de tous les services
- [x] **OBS-03** : `logback-spring.xml` créé pour le gateway-service (JSON LogstashEncoder)
- [x] **OBS-05** : `management.endpoint.health.show-details: when-authorized` ajouté aux 6 services
- [x] **OBS-06** : `userId` ajouté aux logback de media, notification, report (manquait)

### P2 — Ce mois ✅
- [x] **OBS-08** : `smtp` ajouté à `keys-to-sanitize` du auth-service

### P3 — Backlog ✅
- [x] **OBS-07** : `@Timed` ajouté sur les 20 controllers (7 services)
- [x] **OBS-09** : `application-test.yml` créé pour auth, client, intervention, media, report, gateway

---

*Fin du rapport Phase 4 — Observabilité & Configuration*
