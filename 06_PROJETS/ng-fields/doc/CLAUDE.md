# NG-Fields — Contexte pour agents IA

## Stack
- **Backend:** Spring Boot 4.1.0 / Java 25, Maven, JPA/Hibernate — microservices (gateway, auth, client, intervention, media, notification, report)
- **Frontend:** Angular 22+ standalone, TypeScript 5.9, Tailwind CSS 4, Vitest, angular-auth-oidc-client, Chart.js
- **Auth:** Keycloak 26.6.4 (OAuth2/OpenID Connect)
- **Mobile:** Flutter/Dart (Riverpod, GoRouter, Drift/SQLite) — **Non démarré**, répertoire vide
- **DB:** PostgreSQL 18 — `ddl-auto: update` (Hibernate), schéma unique `ng_fields`
- **PDF:** OpenPDF + ZXing (QR codes)
- **Push:** Firebase Admin SDK (conditional, toggle `firebase.enabled`)
- **Email:** Resend API (auth-service + intervention-service)
- **Logs:** Logback + logstash-logback-encoder 8.0 (JSON structuré)
- **Tests:** JUnit 5 + Mockito — **65 unit tests** (intervention: 49, auth: 21, notification: 3)
- **CI/CD:** GitHub Actions

## Structure des dossiers

| Dossier               | Contenu                                                                                      |
| --------------------- | -------------------------------------------------------------------------------------------- |
| `Backend/`            | Microservices Spring Boot (auth, client, gateway, intervention, media, notification, report) |
| `Backend/shared-lib/` | Bibliothèque partagée (DTOs, exceptions, security, logstash-logback-encoder)                 |
| `Frontend/ng-web/`    | Dashboard Angular 22+ (standalone, OnPush, auth Keycloak, Vitest)                            |
| `Frontend/templates/` | Template Next.js du dashboard NG-STARs                                                       |
| `mobile/`             | App Flutter (**Non démarré**, répertoire vide)                                               |
| `Doc/`                | Documentation organisée par thème                                                            |

## Règles de codage

### Backend
- Toujours utiliser des DTOs (records) pour les entrées/sorties API
- Injections par constructeur (pas de @Autowired)
- Les entités JPA utilisent `@PrePersist`/`@PreUpdate` pour les timestamps
- L'ID client `localId` sert à l'idempotence de synchronisation offline→online
- La génération PDF utilise OpenPDF (pas iText)
- Hibernate `ddl-auto: update` remplace Flyway pour la gestion du schéma
- Toujours logger les actions importantes
- **Ne jamais ajouter de commentaires** sauf demande explicite

### Frontend Angular
- Standalone components (pas de NgModules)
- `ChangeDetectionStrategy.OnPush` sur tous les composants
- `takeUntilDestroyed()` pour gérer les subscriptions (pas de unsubscribe manuel)
- Typage strict — pas de `any`, utiliser les DTOs partagés (`shared/models/`)
- Routes protégées par `authRoleGuard` avec rôles Keycloak (ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER)
- Services API via `ApiService` centralisé (HTTP wrapper avec headers auth)
- Tailwind CSS 4 utility-first (pas de styles globaux sauf base)

## Conventions git
- Branches: `feature/US-*`, `fix/US-*`, `release/v*.*`
- Commits en français (convention du projet)
- Ne pas tracker: Keycloak dist, fichiers build, .md à la racine, wireframes

## Commandes
```bash
# Backend — Build
cd Backend && mvn clean install

# Backend — Lancer un service (exemple)
cd Backend/intervention-service && mvnw spring-boot:run

# Backend — Tests unitaires
cd Backend/intervention-service && mvnw test

# Frontend Angular
cd Frontend/ng-web && npm install               # Dépendances
cd Frontend/ng-web && npm start                 # Lancer → http://localhost:4200
cd Frontend/ng-web && npm run build             # Build production
# Mobile (non démarré)
# cd mobile && flutter pub get
# cd mobile && flutter run
```

## Décisions d'architecture
- **Pourquoi Keycloak et pas Supabase Auth** : découplage auth/DB, compatible multi-projets
- **Pourquoi Spring Boot et pas Node.js** : stack Java existante dans l'entreprise
- **Pourquoi Offline first** : couverture réseau aléatoire sur le terrain (quand Flutter sera démarré)
- **Pourquoi Angular standalone et pas NgModules** : Angular 22+ recommande standalone, moins de boilerplate
- **Pourquoi angular-auth-oidc-client et pas Keycloak JS direct** : wrapper Angular idiomatique (Observables, guards), compatible SSR
- **Pourquoi Vitest et pas Jasmine/Karma** : 10-50x plus rapide, même API que Jest, compatible avec Angular CLI 21
- **Pourquoi OnPush partout** : arbre de détection minimal, pas de zone.js overhead sur les composants leaf
- **Pourquoi ApiService centralisé** : interceptors auth, error handling, typage uniforme, moins de duplication que HttpClient brut dans chaque service
- **Pourquoi ddl-auto: update et pas Flyway** : simplicité pour un projet avec Hibernate, moins de maintenance que des scripts SQL séparés
- **Pourquoi Firebase Admin SDK (conditional)** : toggle `firebase.enabled` permet de démarrer sans credentials Firebase en dev, PushServiceNoop en fallback
- **Pourquoi Resend et pas JavaMailSender** : API moderne, deliverability supérieure, pas de configuration SMTP
- **Pourquoi logstash-logback-encoder** : JSON structuré pour ELK/Grafana, compatible Spring Boot 4.1.0

## État d'avancement Backend (23/07/2026)

### US complétées
| US | Description | Statut |
|----|-------------|--------|
| US-001 | Environnement de développement | 🟢 COMPLETED |
| US-002 | Pipeline CI/CD GitHub Actions | 🟢 COMPLETED |
| US-004 | Configuration Realm Keycloak | 🟢 COMPLETED |
| US-005 | Spring Security Resource Server | 🟢 COMPLETED |
| US-006 | Audit Trail | 🟢 COMPLETED |
| US-007 | CRUD Utilisateurs | 🟢 COMPLETED |
| US-009 | CRUD Clients | 🟢 COMPLETED |
| US-011 | Création Intervention | 🟢 COMPLETED |
| US-014 | Pièces et Consommables | 🟢 COMPLETED |
| US-015 | Photos avant/après | 🟢 COMPLETED |
| US-016 | Signatures électroniques | 🟢 COMPLETED |
| US-019 | Synchronisation hors-ligne (batch) | 🟢 COMPLETED |
| US-021 | Rapport PDF automatique | 🟢 COMPLETED |
| US-024/025 | Notifications push Firebase | 🟢 COMPLETED |
| US-028 | Dashboard Manager (API) | 🟢 COMPLETED |
| US-029 | Planning Techniciens (API) | 🟢 COMPLETED |
| US-030 | Actuator + Métriques + Logs JSON | 🟢 COMPLETED |
| US-033 | Documentation API Swagger | 🟢 COMPLETED |
| US-035 | SSE Real-time Dashboard | 🟢 COMPLETED |
| US-036 | Clean Architecture (DTOs projection) | 🟢 COMPLETED |
| US-037/038/039 | Gestion Entreprises + Utilisateurs | 🟢 COMPLETED |
| US-040 | Portail Consultation Interventions | 🟢 COMPLETED |
| US-041 | RBAC CLIENT (multi-tenant) | 🟢 COMPLETED |
| US-042 | Email Bienvenue et Credentials | 🟢 COMPLETED |
| US-043 | Tableau de bord CLIENT_ADMIN (API) | 🟢 COMPLETED |

### US supprimées
| US | Raison |
|----|--------|
| US-003 | Flyway supprimé → ddl-auto: update |
| US-023 | WhatsApp supprimé (email UNIQUEMENT) |
| US-026 | Portail client public supprimé |
| US-027 | OpenProject supprimé |

### Tests unitaires (65 tests)
- `InterventionServiceTest` — 25 tests
- `InterventionStatusServiceTest` — 16 tests
- `ExportServiceTest` — 8 tests (CSV/HTML escaping inclus)
- `UserServiceTest` — 11 tests
- `CompanyServiceTest` — 10 tests
- `PushServiceNoopTest` — 3 tests

### Bugfixes critiques
- `InterventionService.getStats()`: `countAll()` pour totalInterventions
- `ExportController`: applique filtres status/technicianId
- `UserService.updateUser()`: supprime anciens rôles Keycloak avant ajout
- `UserService.changePassword()`: vérifie ancien mdp via token endpoint Keycloak
- `CompanyService.addCompanyUser()`: retire tempPassword des logs

### Postman
- Collection: 90 endpoints couvrant tous les US
- Fichier: `Backend/postman/NG-Fields API.postman_collection.json`
