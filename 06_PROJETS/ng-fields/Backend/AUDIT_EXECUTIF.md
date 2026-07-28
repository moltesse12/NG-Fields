# Rapport d'Audit Exécutif & Technique — NG-Fields

**Projet :** NG-Fields — Plateforme de Gestion d'Interventions Terrain  
**Date :** 27 Juillet 2026  
**Auditeur :** Directeur Technique (CTO)  
**Scope :** Backend (7 microservices + shared-lib), Frontend (Angular), CI/CD, Infrastructure  

---

## 1. Executive Summary

NG-Fields est une plateforme microservices robuste couvrant la gestion complète des interventions terrain (clients, interventions, photos, signatures, rapports, notifications). Le projet est fonctionnel et bien architecturé dans son ensemble, avec une couverture fonctionnelle de **91 endpoints API** et un frontend moderne Angular 21.

### État de Santé Global : **6.5 / 10**

| Dimension | Note | Commentaire |
|-----------|------|-------------|
| Architecture | 8/10 | Microservices bien découpés, shared-lib, patterns solides |
| Sécurité | 7/10 | Keycloak, RBAC, rate limiting, mais dépendances non patchées |
| Qualité du Code | 7/10 | Conventions respectées, records Java, mais `any` dans le frontend |
| Tests | 5/10 | 25 fichiers backend, mais 0 tests d'intégration, 4% frontend |
| DevOps | 5/10 | CI/CD existant mais Java 21 vs 25, pas de Dockerfiles services |
| Documentation | 8/10 | ARCHITECTURE.md, SECURITY.md, 91 endpoints documentés |
| Maintenabilité | 6/10 | 161 fichiers non commités, dette technique sur les versions |

**Verdict :** Le projet est **production-ready fonctionnellement** mais nécessite un sprint de stabilisation avant déploiement critique. Les risques majeurs sont : (1) Java 25 non supporté en CI, (2) aucune couche de test d'intégration, (3) dépendances hardcodées potentiellement vulnérables.

---

## 2. Matrice des Risques

| # | Risque | Criticité | Effort | Impact | Localisation |
|---|--------|-----------|--------|--------|--------------|
| R01 | **CI utilise Java 21, code compile en Java 25** — mismatch = build CI potentiellement cassé | 🔴 Bloquant | Faible | CI/CD inutilisable | `.github/workflows/backend-ci.yml:26` |
| R02 | **Aucun Dockerfile pour les 7 services Java** — pas de déploiement conteneurisé | 🔴 Bloquant | Élevé | Impossible à déployer en production | `Backend/*/` |
| R03 | **resilience4j spring-boot3 (2.2.0) dans report-service** vs spring-boot4 (2.4.0) dans les autres | 🔴 Majeur | Faible | Incompatibilité potentielle | `report-service/pom.xml:123` |
| R04 | **161 fichiers non commités** sur la branche main | 🟠 Majeur | Faible | Perte de travail, conflits | Git working tree |
| R05 | **0 tests d'intégration** (Testcontainers, @SpringBootTest) | 🟠 Majeur | Élevé | Régressions silencieuses | Tous les services |
| R06 | **Frontend : 4% de couverture de tests** (4 spec.ts / 100+ .ts) | 🟠 Majeur | Élevé | Régressions UI non détectées | `Frontend/ng-web/src/` |
| R07 | **Hardcoded URLs localhost** dans email-templates preview | 🟠 Majeur | Faible | Fonctionne pas en prod | `email-templates.component.ts:233-234` |
| R08 | **`bypassSecurityTrustHtml`** dans preview email templates | 🟠 Majeur | Moyen | XSS si contenu utilisateur | `email-templates.component.ts:225` |
| R09 | **Pas de rate limiting SSR** — server.ts sans middleware sécurité | 🟡 Mineur | Faible | DoS potentiel | `Frontend/ng-web/src/server.ts` |
| R10 | **Console.log de debug** laissé dans le code frontend | 🟡 Mineur | Faible | Fuite d'infos en prod | `intervention-files.component.ts:60` |
| R11 | **Couleurs hardcodées en hex** au lieu de tokens sémantiques | 🟡 Mineur | Moyen | Dette UI, incohérence | 14+ fichiers frontend |
| R12 | **Aucun linting** configuré (eslint/biome absent du frontend) | 🟡 Mineur | Faible | Dette de code non détectée | `Frontend/ng-web/` |
| R13 | **Spring Boot 4.1.0 sur Java 25** — combinaison très récente | 🟡 Mineur | Élevé | Stabilité à surveiller | Tous les `pom.xml` |
| R14 | **logstash-logback-encoder 8.0** hardcodé | 🟡 Mineur | Faible | Incompatible futures versions | `shared-lib/pom.xml:56` |
| R15 | **Aucune séparation environnements** (dev/staging/prod) côté backend | 🟡 Mineur | Moyen | Risque de config prod cassée | `application.yml` services |

---

## 3. Quick Wins — 5 Actions Correctives Immédiates

Ces 5 actions apportent **80% de la valeur avec 20% de l'effort** :

### 3.1. 🔴 Corriger Java 21 → 25 dans le CI backend
**Effort :** 2 min | **Impact :** Critique
```yaml
# backend-ci.yml : ligne 26
java-version: '25'  # au lieu de '21'
```

### 3.2. 🔴 Unifier resilience4j spring-boot3 → spring-boot4 dans report-service
**Effort :** 5 min | **Impact :** Majeur
```xml
<!-- report-service/pom.xml : remplacer -->
<artifactId>resilience4j-spring-boot3</artifactId>  <!-- SUPPRIMER -->
<artifactId>resilience4j-spring-boot4</artifactId>   <!-- AJOUTER -->
<version>2.4.0</version>
```

### 3.3. 🟠 Fixer les URLs localhost hardcodées dans email-templates
**Effort :** 10 min | **Impact :** Majeur
```typescript
// email-templates.component.ts : remplacer les URLs fixées
const baseUrl = this.window.location.origin;
`${baseUrl}/login`
`${baseUrl}/reset-password?token=${token}`
```

### 3.4. 🟠 Retirer `console.log` de debug
**Effort :** 2 min | **Impact :** Mineur mais important pour la prod
```typescript
// intervention-files.component.ts:60 — supprimer
console.log('Fichiers sélectionnés:', files);
```

### 3.5. 🟠 Commiter les 161 changements en attente
**Effort :** 15 min | **Impact :** Majeur — 4 commits logiques
```
git add -A && git commit -m "feat: tests unitaires shared-lib + gateway + report"
git add -A && git commit -m "fix: security + Flyway migrations + dependencies"
# etc.
```

**Total Quick Wins : ~35 minutes → gain immédiat significatif**

---

## 4. Roadmap de Refactoring — Plan d'Action sur 1 Mois

### Semaine 1 : Sécurité & Performance (S1)

| Jour | Action | Priorité | Fichiers concernés |
|------|--------|----------|-------------------|
| L1 | Corriger Java 21→25 dans CI | P0 | `backend-ci.yml` |
| L1 | Unifier resilience4j spring-boot3→4 | P0 | `report-service/pom.xml` |
| L2 | Fixer URLs localhost hardcodées | P0 | `email-templates.component.ts` |
| L2 | Supprimer `bypassSecurityTrustHtml` (utiliser sanitizer angular) | P1 | `email-templates.component.ts` |
| L3 | Ajouter rate limiting SSR (express-rate-limit) | P1 | `server.ts`, `package.json` |
| L3 | Sécuriser `allowedHosts` dans angular.json | P1 | `angular.json` |
| J4 | Audit dépendances (`mvn dependency:check`, `npm audit`) | P1 | Tous les pom.xml + package.json |
| J5 | Ajouter security headers dans le gateway (CSP, X-Frame-Options) | P1 | `SecurityConfig.java` gateway |

### Semaine 2 : Architecture & Qualité (S2)

| Jour | Action | Priorité | Fichiers concernés |
|------|--------|----------|-------------------|
| L1 | Créer Dockerfiles pour les 7 services Java | P0 | `Backend/*/Dockerfile` |
| L2 | Standardiser les versions hardcodées → BOM parent | P1 | `pom.xml` parent + 7 services |
| L2 | Ajouter `spring-boot-starter-test` au shared-lib (déjà fait ✅) | P0 | `shared-lib/pom.xml` |
| M3 | Ajouter ESLint/Prettier au frontend | P1 | `package.json` ng-web |
| M3 | Corriger les `any` TypeScript (30 occurrences) | P1 | 15+ fichiers frontend |
| J4 | Extraire les couleurs hardcodées en tokens sémantiques Tailwind | P2 | 14+ fichiers frontend |
| J5 | Ajouter environment `staging` (3 fichiers d'environnement) | P2 | `environment.staging.ts` |

### Semaine 3 : Tests & DevOps (S3)

| Jour | Action | Priorité | Fichiers concernés |
|------|--------|----------|-------------------|
| L1-M2 | Ajouter tests d'intégration Testcontainers (PostgreSQL + Redis) | P0 | 7 services |
| L3 | Ajouter @WebMvcTest pour les controllers (couche API) | P1 | 6 controllers |
| J4 | Ajouter tests de sécurité (@WithMockUser, RBAC) | P1 | Tous les SecurityConfig |
| J5 | Ajouter tests E2E frontend (Playwright ou Cypress) | P2 | `Frontend/ng-web/` |

### Semaine 4 : Stabilisation & Documentation (S4)

| Jour | Action | Priorité | Fichiers concernés |
|------|--------|----------|-------------------|
| L1 | Couverture de tests frontend (guards, interceptors, services) | P1 | `*.spec.ts` |
| L2 | Mettre à jour ARCHITECTURE.md avec l'état actuel | P2 | `Backend/ARCHITECTURE.md` |
| M3 | Pipeline de déploiement staging automatique | P1 | `.github/workflows/` |
| J4 | Revue de code globale + merge des 161 changements | P0 | Git |
| J5 | Réunion de clôture + plan de maintenance | P2 | — |

---

## 5. Recommandations de Mise à Jour des Dépendances

### 5.1. État des Lieux

| Composant | Version Actuelle | Dernière Stable | Écart | Risque |
|-----------|-----------------|-----------------|-------|--------|
| **Java** | 25 (LTS candidate) | 25.0.2 | ✅ À jour | Faible — LTS récente |
| **Spring Boot** | 4.1.0 | 4.1.0 | ✅ À jour | Faible — dernière release |
| **Spring Cloud** | 2025.1.2 | 2025.1.x | ✅ À jour | Faible |
| **Angular** | 21.2.0 | 21.2.x | ✅ À jour | Faible |
| **TypeScript** | 5.9.2 | 5.9.x | ✅ À jour | Faible |
| **Keycloak** | 26.6.4 | 26.6.x | ✅ À jour | Faible |
| **PostgreSQL** | 18 | 18 | ✅ À jour | Faible |
| **Node.js (CI)** | 22 | 22.x LTS | ✅ À jour | Faible |
| **Resilience4j** | 2.2.0 / 2.4.0 | 2.4.0 | ⚠️ Incohérent | **Majeur** — unifier |
| **OpenPDF** | 3.0.5 | 3.0.5 | ✅ À jour | Faible |
| **Firebase Admin** | 9.10.0 | 9.x | ✅ À jour | Faible |
| **Bucket4j** | 8.14.0 | 8.x | ✅ À jour | Faible |
| **logstash-logback-encoder** | 8.0 | 8.x | ✅ À jour | Faible |

### 5.2. Actions de Migration Requises

| Action | Effort | Priorité | Détail |
|--------|--------|----------|--------|
| **Unifier resilience4j** | Faible | P0 | report-service : `spring-boot3:2.2.0` → `spring-boot4:2.4.0` |
| **Java CI 21 → 25** | Faible | P0 | `backend-ci.yml` : `java-version: '25'` |
| **Stderr logstash-logback** | Faible | P2 | Gérer via BOM Spring Boot si possible |
| **Surveiller Spring Boot 4.x** | Moyen | P1 | Boot 4 est récent — monitorer les patch releases |

### 5.3. Stratégie de Dépendances Recommandée

```
Parent POM (ng-fields-backend) :
  → Spring Boot 4.1.0 BOM (gère 90% des versions)
  → Spring Cloud 2025.1.2 BOM (gateway)
  → Property centralisée pour les versions hardcodées :

  <properties>
    <springdoc.version>3.0.3</springdoc.version>
    <resilience4j.version>2.4.0</resilience4j.version>
    <openpdf.version>3.0.5</openpdf.version>
    <resend.version>4.13.0</resend.version>
    <keycloak-admin.version>26.0.9</keycloak-admin.version>
    <firebase-admin.version>9.10.0</firebase-admin.version>
    <bucket4j.version>8.14.0</bucket4j.version>
  </properties>
```

Cela permet de **versions unique** gérées depuis le parent POM au lieu de 7 fichiers séparés.

---

## 6. Annexe — Métriques du Projet

### Backend

| Métrique | Valeur |
|----------|--------|
| Microservices | 7 + 1 shared-lib |
| Fichiers Java | ~200+ |
| Endpoints API | 91 |
| Fichiers de test | 25 |
| Tests unitaires estimés | ~172 |
| Base de données | 4 schemas PostgreSQL (auth, client, intervention, reports) |
| Migrations Flyway | 8 fichiers (V1 + V2/V3) |
| Circuit breakers | 4 services (client, intervention, notification, report) |

### Frontend

| Métrique | Valeur |
|----------|--------|
| Framework | Angular 21.2 (Zoneless, SSR, Standalone) |
| Fichiers TypeScript | ~100+ |
| Composants | ~30+ |
| Pages | 15+ |
| Spec files | 4 (couverture ~4%) |
| Pages role-gardées | 8 (ADMIN/MANAGER) |

### DevOps

| Métrique | Valeur |
|----------|--------|
| CI/CD | 2 pipelines GitHub Actions |
| Docker Compose | 5 services (Postgres, Redis, Keycloak, Backup, API) |
| Fichiers non commités | 161 |
| Branche | main (4 commits en avance sur origin) |

---

*Rapport généré le 27/07/2026 — Prochain audit recommandé dans 30 jours.*
