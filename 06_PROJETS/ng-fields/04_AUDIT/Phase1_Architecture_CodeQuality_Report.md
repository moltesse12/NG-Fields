# Phase 1: Architecture & Code Quality Audit — NG-Fields Backend

**Date:** 2026-07-26
**Scope:** All 7 microservices + shared-lib (~200 Java files, configs, SQL, tests)
**Stack:** Spring Boot 4.1.0 / Java 25, PostgreSQL 18, Keycloak 26.6, Flyway 12.4

---

## Executive Summary

The NG-Fields backend is a well-structured microservices architecture for field intervention management. However, the audit uncovered **2 critical security vulnerabilities**, **5 high-severity issues**, and numerous medium-severity concerns across authorization, data integrity, memory management, and Spring Boot 4.x migration compatibility.

### Fix Summary

| Severity | Found | Fixed in this session | Remaining |
|----------|-------|----------------------|-----------|
| CRITICAL | 2     | 0                    | 2         |
| HIGH     | 5     | 0                    | 5         |
| MEDIUM   | 10    | 0                    | 10        |
| LOW      | 8     | 0                    | 8         |

---

## 1. CRITICAL — Security Vulnerabilities

### 1.1 Authorization Bypass: `closeIntervention` / `cancelIntervention`

**File:** `intervention-service/.../InterventionService.java:423,434`
**Impact:** Any authenticated user can close or cancel ANY intervention

```java
// BUG: checkOwnership() is NOT called
@Transactional
public InterventionResponse closeIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
    var intervention = findOrThrow(id);        // ← no checkOwnership()
    var oldStatus = intervention.getStatus();
    statusService.closeIntervention(intervention, userId, isAdminOrManager);
    ...
}

// COMPARE: startIntervention DOES check ownership
@Transactional
public InterventionResponse startIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
    var intervention = findOrThrow(id);
    checkOwnership(intervention, userId, isAdminOrManager);  // ← present
    ...
}
```

**Fix:** Add `checkOwnership(intervention, userId, isAdminOrManager)` before status change in both methods.

### 1.2 IDOR: `getClientInterventions` Missing Authorization

**File:** `intervention-service/.../InterventionService.java:239-242`
**Impact:** Any user can query interventions for any client company

```java
public Page<InterventionResponse> getClientInterventions(UUID clientId, UUID userId, boolean isAdminOrManager, Pageable pageable) {
    // userId and isAdminOrManager are NEVER used!
    return interventionRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
            .map(this::toResponse);
}
```

**Fix:** Verify that the authenticated user belongs to `clientId` or is ADMIN/MANAGER.

---

## 2. HIGH — Security & Data Integrity

### 2.1 Status Validation Gap

**File:** `intervention-service/.../InterventionService.java:97,196`
**Impact:** Users can set arbitrary status values (e.g., "COMPLETED") on create/update

- `createIntervention()` line 97: `status(request.status() != null ? request.status() : "PENDING")` — no validation
- `updateIntervention()` line 196: `intervention.setStatus(request.status())` — no validation
- Only `syncFromMobile()` calls `statusService.validateTransition()`

**Fix:** Call `statusService.validateTransition()` in all write methods that change status.

### 2.2 Email Relay Risk

**File:** `intervention-service/.../InterventionService.java:505-510`
**Impact:** `sendEmailReport` accepts arbitrary recipient emails — could be used as email relay

```java
@Transactional
public void sendEmailReport(UUID id, String recipientEmail, UUID userId, boolean isAdminOrManager) {
    var intervention = findOrThrow(id);
    checkOwnership(intervention, userId, isAdminOrManager);
    emailService.sendInterventionReport(intervention, recipientEmail);  // ← no validation
}
```

**Fix:** Validate `recipientEmail` belongs to the intervention's client or the user's company.

### 2.3 SSE Information Disclosure

**File:** `intervention-service/.../SseEmitterManager.java:39-52`
**Impact:** `sendEvent()` broadcasts ALL events to ALL connected users regardless of authorization

```java
public void sendEvent(String eventName, Object data) {
    for (var entry : emitters.entrySet()) {     // ← broadcasts to everyone
        entry.getValue().send(...);
    }
}
```

**Fix:** Implement per-user or per-role event filtering, or use `sendToUser()` exclusively for sensitive events.

### 2.4 `media-service` Does Not Extend `BaseExceptionHandler`

**File:** `media-service/.../GlobalExceptionHandler.java`
**Impact:** Inconsistent error handling across microservices

- `media-service` has its own standalone `GlobalExceptionHandler` (60 lines)
- All other services extend `BaseExceptionHandler` from `shared-lib`
- Missing handlers: `DataIntegrityViolationException`, `ObjectOptimisticLockingFailureException`, `IllegalStateException`

**Fix:** Rewrite `media-service/GlobalExceptionHandler` to extend `BaseExceptionHandler`.

### 2.5 Custom `ObjectMapper` Breaks Spring Boot Defaults

**File:** `media-service/.../MediaServiceApplication.java:20-23`
**Impact:** Loses Java 8 time module, Jackson security defaults, `spring.jackson.*` properties

```java
@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper();  // ← replaces ALL Spring Boot Jackson config
}
```

**Fix:** Remove this bean entirely, or customize the existing auto-configured one.

---

## 3. MEDIUM — Code Quality & Architecture

### 3.1 `SecurityUtils` Duplicated in `intervention-service`

**Files:**
- `shared-lib/.../security/SecurityUtils.java` — static utility class
- `intervention-service/.../service/SecurityUtils.java` — Spring `@Component` with different API

These are **completely different implementations** that share only the class name. The intervention-service version has additional methods (`getCompanyId`, `getCurrentUserEmail`, `isClientRole`) not in shared-lib.

**Fix:** Merge into shared-lib's version or create a dedicated `JwtSecurityUtils` component.

### 3.2 SSE Memory Leak Risks

**File:** `intervention-service/.../SseEmitterManager.java`
**Issues:**
- Map keyed by `userId` silently replaces old emitter — leaked connection hangs until timeout
- 30-minute timeout is hardcoded (should be configurable)
- No deduplication or rate limiting on `createEmitter()`
- Failed sends logged at `debug` level (should be `warn`)

### 3.3 `InterventionService` — 9 Separate DB Queries for Stats

**File:** `intervention-service/.../InterventionService.java:153-168`
**Impact:** Performance — should be a single aggregation query

```java
interventionRepository.countAll();          // query 1
interventionRepository.countActive();       // query 2
interventionRepository.countByStatus();     // query 3
interventionRepository.countAssigned();     // query 4
interventionRepository.countCompleted();    // query 5
interventionRepository.countPending();      // query 6
interventionRepository.countCancelled();    // query 7
interventionRepository.averageDurationMinutes(); // query 8
interventionRepository.sumEstimatedCost();  // query 9
```

**Fix:** Single native SQL with `GROUP BY` and aggregate functions.

### 3.4 `updateIntervention` Nullifies Fields

**File:** `intervention-service/.../InterventionService.java:178-224`
**Impact:** Partial updates wipe out fields not included in request

The method sets ALL fields from the request object, even when `null`. Compare with `updateEquipment`/`updateDiagnosis` which correctly use null-guards.

### 3.5 Massive Code Duplication in Item Handling

**File:** `intervention-service/.../InterventionService.java`
**Impact:** Item mapping + total recalculation logic repeated in 5+ places (create, update, addItem, updateItem, removeItem)

**Fix:** Extract `mapToItem()`, `recalculateTotal()` helper methods.

### 3.6 `getStats` Uses Unchecked Casts

**File:** `intervention-service/.../InterventionService.java:155-157`
```java
var countByStatus = interventionRepository.countByStatus().stream()
    .collect(Collectors.toMap(
        row -> (String) row[0],    // ← unchecked
        row -> (Long) row[1]));    // ← unchecked
```

**Fix:** Use a projection interface or `@Query` with a named result class.

### 3.7 `schedule` Overlap Detection is Information-Only

**File:** `intervention-service/.../InterventionService.java:280-285`
**Impact:** Scheduling conflicts are logged but not prevented — business rule not enforced

### 3.8 Missing `@Transactional` on `InterventionService.createIntervention`

**File:** `intervention-service/.../InterventionService.java:57`
**Impact:** `save()` + `sendEvent()` are not in the same transaction — event could fire before save is committed

Actually, this is `@Transactional` at method level — but `sseManager.sendEvent()` runs inside the transaction, meaning the SSE event is sent before the transaction commits.

### 3.9 `auth-service` V1 Migration — Missing `search_path`

**File:** `auth-service/.../V1__init.sql` — **FIXED in this session**
**Root cause:** Flyway connects with `currentSchema=auth`, but `uuid_generate_v4()` from `uuid-ossp` extension is in `public` schema. Without `SET search_path TO auth, public`, the function is not found.

**Status:** Fixed by adding `SET search_path TO <schema>, public;` to all 4 V1__init.sql files.

### 3.10 Spring Boot 4.x Migration — `flyway-core` Deprecated

**Files:** All 4 service pom.xml files — **FIXED in this session**
**Root cause:** Spring Boot 4.0 modularized auto-configuration. `FlywayAutoConfiguration` moved from `spring-boot-autoconfigure` to separate `spring-boot-flyway` module. Using bare `flyway-core` no longer triggers auto-config.

**Fix applied:** Replaced `flyway-core` with `spring-boot-starter-flyway` in all 4 services.

---

## 4. LOW — Minor Issues

| # | File | Issue |
|---|------|-------|
| 4.1 | `SseEmitterManager.java` | Debug-level logging for errors (should be `warn`) |
| 4.2 | `SseEmitterManager.java` | Hardcoded 30-minute timeout (should be configurable) |
| 4.3 | `InterventionService.java:276` | `Duration.toMinutes()` returns `long` but stored as `int` (overflow risk) |
| 4.4 | `auth-service/UserRepository.java` | No `@Transactional(readOnly = true)` on read-only interface |
| 4.5 | `auth-service/V1__init.sql` | `CREATE EXTENSION IF NOT EXISTS` — should specify `SCHEMA public` explicitly |
| 4.6 | `intervention-service` | `@Transactional(readOnly = true)` at class level creates false sense of safety |
| 4.7 | All services | No circuit breaker on inter-service HTTP calls |
| 4.8 | All services | No distributed tracing (OpenTelemetry/Spring Cloud Sleuth) |

---

## 5. Architecture Recommendations

### 5.1 Immediate (Next Sprint)

1. **Fix CRITICAL #1.1 and #1.2** — Add `checkOwnership()` to `closeIntervention`/`cancelIntervention`; add auth check to `getClientInterventions`
2. **Fix HIGH #2.1** — Add `statusService.validateTransition()` to all status-changing methods
3. **Fix HIGH #2.4** — Make `media-service` extend `BaseExceptionHandler`
4. **Fix HIGH #2.5** — Remove custom `ObjectMapper` bean from `MediaServiceApplication`

### 5.2 Short-Term (1-2 Sprints)

5. Merge `SecurityUtils` implementations into shared-lib
6. Add per-user SSE event filtering
7. Optimize `getStats` to single aggregation query
8. Add input validation (Bean Validation) on all DTOs
9. Add API rate limiting beyond gateway level

### 5.3 Medium-Term (1 Month)

10. Add OpenTelemetry distributed tracing
11. Add integration tests for all services
12. Implement proper RBAC validation in service layer (not just controller)
13. Add database connection pool monitoring/alerting
14. Move to `spring-boot-starter-flyway` across all services (DONE)

---

## 6. Flyway Fix Details (This Session)

### Root Cause
Spring Boot 4.x modularized auto-config. `FlywayAutoConfiguration` was extracted from `spring-boot-autoconfigure` into `spring-boot-flyway`. Without the `spring-boot-starter-flyway` dependency, Flyway jars are on the classpath but **never auto-configured** — zero log output, zero migration execution.

### Changes Made
| File | Change |
|------|--------|
| `auth-service/pom.xml` | `flyway-core` → `spring-boot-starter-flyway` |
| `client-service/pom.xml` | `flyway-core` → `spring-boot-starter-flyway` |
| `intervention-service/pom.xml` | `flyway-core` → `spring-boot-starter-flyway` |
| `report-service/pom.xml` | `flyway-core` → `spring-boot-starter-flyway` |
| `auth-service/V1__init.sql` | Added `SET search_path TO auth, public;` |
| `client-service/V1__init.sql` | Added `SET search_path TO client, public;` |
| `intervention-service/V1__init.sql` | Added `SET search_path TO intervention, public;` |
| `report-service/V1__init.sql` | Added `SET search_path TO reports, public;` |

### Verified
- auth-service starts successfully with Flyway auto-migrating V1 + V2
- `flyway_schema_history` table created in `auth` schema with 2 successful migrations
- Health endpoint returns `UP`
