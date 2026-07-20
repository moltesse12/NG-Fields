# NG-Fields — Contexte pour agents IA

## Stack
- **Backend:** Spring Boot 4.0.6 / Java 25, Maven, JPA/Hibernate — microservices (gateway, auth, client, intervention, media, notification, report)
- **Frontend:** Angular 21 standalone, TypeScript 5.9, Tailwind CSS 4, Vitest, angular-auth-oidc-client, Chart.js
- **Auth:** Keycloak 26.6.2 (OAuth2/OpenID Connect)
- **Mobile:** Flutter/Dart (Riverpod, GoRouter, Drift/SQLite)
- **DB:** PostgreSQL via Supabase
- **PDF:** OpenPDF + ZXing (QR codes)
- **CI/CD:** GitHub Actions

## Structure des dossiers

| Dossier               | Contenu                                                                                      |
| --------------------- | -------------------------------------------------------------------------------------------- |
| `Backend/`            | Microservices Spring Boot (auth, client, gateway, intervention, media, notification, report) |
| `Frontend/ng-web/`    | Dashboard Angular 21 (standalone, OnPush, auth Keycloak, Vitest)                             |
| `Frontend/templates/` | Template Next.js du dashboard NG-STARs                                                       |
| `mobile/`             | App Flutter                                                                                  |
| `Doc/`                | Documentation organisée par thème                                                            |

## Règles de codage

### Backend
- Toujours utiliser des DTOs (records) pour les entrées/sorties API
- Injections par constructeur (pas de @Autowired)
- Les entités JPA utilisent `@PrePersist`/`@PreUpdate` pour les timestamps
- L'ID client `localId` sert à l'idempotence de synchronisation offline→online
- La génération PDF utilise OpenPDF (pas iText)
- Toujours logger les actions importantes

### Frontend Angular
- Standalone components (pas de NgModules)
- `ChangeDetectionStrategy.OnPush` sur tous les composants
- `takeUntilDestroyed()` pour gérer les subscriptions (pas de unsubscribe manuel)
- Typage strict — pas de `any`, utiliser les DTOs partagés (`shared/models/`)
- Routes protégées par `authRoleGuard` avec rôles Keycloak (ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL)
- Services API via `ApiService` centralisé (HTTP wrapper avec headers auth)
- Tests avec Vitest + jsdom + Angular TestBed
- Tailwind CSS 4 utility-first (pas de styles globaux sauf base)
- `resetTestingModule()` dans chaque `beforeEach` pour les tests parallèles

## Conventions git
- Branches: `feature/US-*`, `fix/US-*`, `release/v*.*`
- Commits en français (convention du projet)
- Ne pas tracker: Keycloak dist, fichiers build, .md à la racine, wireframes

## Commandes
```bash
# Backend
cd Backend && ./mvnw spring-boot:run            # Lancer
cd Backend && ./mvnw clean verify               # Tester + builder

# Frontend Angular
cd Frontend/ng-web && npm install               # Dépendances
cd Frontend/ng-web && npm start                 # Lancer → http://localhost:4200
cd Frontend/ng-web && npm run build             # Build production
cd Frontend/ng-web && npm test                  # Tests Vitest

# Mobile (quand prêt)
cd mobile && flutter pub get                    # Dépendances
cd mobile && flutter run                        # Lancer sur device
cd mobile && flutter test                       # Tests
```

## Décisions d'architecture
- **Pourquoi Keycloak et pas Supabase Auth** : découplage auth/DB, compatible multi-projets
- **Pourquoi Spring Boot et pas Node.js** : stack Java existante dans l'entreprise
- **Pourquoi Offline first** : couverture réseau aléatoire sur le terrain
- **Pourquoi Drift/SQLite** : seule solution Flutter mature avec migrations typées
- **Pourquoi Angular standalone et pas NgModules** : Angular 17+ recommande standalone, moins de boilerplate
- **Pourquoi angular-auth-oidc-client et pas Keycloak JS direct** : wrapper Angular idiomatique (Observables, guards), compatible SSR
- **Pourquoi Vitest et pas Jasmine/Karma** : 10-50x plus rapide, même API que Jest, compatible avec Angular CLI 21
- **Pourquoi OnPush partout** : arbre de détection minimal, pas de zone.js overhead sur les composants leaf
- **Pourquoi ApiService centralisé** : interceptors auth, error handling, typage uniforme, moins de duplication que HttpClient brut dans chaque service
