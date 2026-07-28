# Phase 2: Audit Securite — NG-Fields Backend

**Date :** 2026-07-26
**Scope :** 7 microservices (gateway, auth, client, intervention, media, notification, report) + shared-lib
**Stack :** Spring Boot 4.1.0 / Java 25, Keycloak 26.6.4, PostgreSQL 18, Redis 7
**Reference OWASP :** OWASP Top 10 2021

---

## Resume Executif

| Criticite | Nombre | Corriges |
|-----------|--------|----------|
| **CRITIQUE** | 2 | 0 |
| **HAUTE** | 6 | 0 |
| **MOYENNE** | 8 | 0 |
| **BASSE** | 7 | 0 |
| **TOTAL** | **23** | **0** |

---

## 1. Identite & Gestion des Secrets (OWASP A07:2021)

### CRITIQUE-S1 : Mot de passe DB hardcoded dans 3 fichiers documentation (git-tracked)

| Fichier | Ligne | Contenu |
|---------|-------|---------|
| `Backend/docs/DEPLOYMENT.md` | 18 | `CREATE USER ng_fields_user WITH PASSWORD 'Pg_ng-fields1234';` |
| `Doc/docs/backlog-api-v2/guides/03-spring-security.md` | 85 | `password: ${DB_PASSWORD:Pg_ng-fields1234}` |
| `Doc/Backlog.md` | 129 | `PostgreSQL 18 en local (user ng_fields_user, password Pg_ng-fields1234)` |

**Impact :** N'importe qui avec acces au depot peut recuperer les credentials de la base.
**Correctif :** Remplacer par `CHANGE_ME` dans tous les fichiers. Tourner les secrets en production.

### CRITIQUE-S2 : Secrets Keycloak/Resend recuperables depuis l'historique git

Le commit `732458d` ("fix(securite): completter .env + nettoyer secrets Postman") a nettoye le fichier Postman, mais les valeurs reelles restent dans `git log -p` :
- `KEYCLOAK_ADMIN_CLIENT_SECRET` = `c5c0f83e-d922-42a5-8831-9a054a6ff53c`
- `RESEND_API_KEY` = `re_***masked***`

**Impact :** Tout collaborateur avec un clone du repo peut extraire ces secrets.
**Correctif :** Tourner tous les secrets, envisager `git filter-repo` ou BFG Repo-Cleaner pour purger l'historique.

### HAUTE-S3 : MDP par defaut dans `report-service/.env.template`

`report-service/.env.template:17` : `DB_PASSWORD=ngfields-dev` — mot de passe reel dans un fichier git-tracked.

### HAUTE-S4 : Pas de secrets manager en production

Tous les secrets sont dans `.env` (gitignore) mais il n'y a aucun vault (HashiCorp Vault, AWS Secrets Manager, Spring Cloud Config Server). Risque d'exposition en production si le `.env` fuit.

**Recommandation :** Planifier la migration vers un secrets manager pour la production.

---

## 2. Configuration CORS (OWASP A05:2021)

### HAUTE-C1 : Bean CORS absent dans 6 services

Les services `auth`, `client`, `intervention`, `media`, `notification`, `report` definissent `app.cors.allowed-origins` dans leur `application.yml` mais **aucune classe ne lit cette propriete**. Pas de `CorsConfigurationSource`, pas de `WebMvcConfigurer`.

CORS est uniquement gere au niveau du **gateway-service** (`SecurityConfig.java` avec `corsConfigurationSource()`).

**Risque :** Si un service est accessible directement (bypass du gateway), il n'y a aucune protection CORS.

**Correctif :** Soit ajouter un bean CORS dans chaque SecurityConfig, soit retirer les proprietes inutilisees et documenter que CORS est exclusivement gere par le gateway.

### HAUTE-C2 : Headers de securite HTTP absents dans TOUS les services

Aucun service ne configure :
- `Strict-Transport-Security` (HSTS)
- `Content-Security-Policy`
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection`
- `Referrer-Policy`

Spring Security 6+ applique certains defaults (X-Frame-Options, X-Content-Type-Options), mais ils ne sont pas verifies ni renforces.

**Correctif :** Configurer explicitement les security headers dans chaque `SecurityFilterChain`.

---

## 3. Authentification & Authorisation (OWASP A01:2021)

### HAUTE-A1 : ROPC (Resource Owner Password Credentials) utilise pour verification de mot de passe

`auth-service/.../UserService.java:318-350` utilise le grant ROPC (`grant_type=password`) pour verifier le mot de passe courant lors du changement de mot de passe.

**Probleme :** ROPC est **deprecie dans OAuth 2.1** et **desactive par defaut dans Keycloak 26+** (ce projet utilise Keycloak 26.6.4). Si le client `verification-client` n'a pas "Direct Access Grants Enabled", cette verification echouera silencieusement.

**Correctif :** Remplacer par une verification via l'API Admin Keycloak (`GET /admin/realms/ng-fields/users/{id}/credentials`) ou par une introspection de token.

### HAUTE-A2 : `forward-headers-strategy: native` dans intervention-service

`intervention-service/application.yml:4` : `forward-headers-strategy: native` — dit a Spring Boot de **faire confiance a tous les headers forwardes** (`X-Forwarded-For`, `X-Forwarded-Proto`, etc.).

Si le reverse proxy n'est pas configure pour ecraser ces headers, un attaquant peut :
- Falsifier `X-Forwarded-For` pour bypasser des restrictions IP
- Manipuler `X-Forwarded-Proto` pour forcer HTTP au lieu de HTTPS

**Correctif :** Utiliser `forward-headers-strategy: framework` (plus securise, traite uniquement les headers conformes aux specs).

### MOYENNE-A3 : Thresholds brute force trop permissifs

`auth-service/application.yml:64-68` : 10 tentatives/15min par username, 20 tentatives/15min par IP, lockout 30min.

**Correctif :** Reduire a 5 tentatives par username, 10 par IP. Augmenter le lockout a 60min.

### MOYENNE-A4 : Rate limiting in-memory (non distribue)

Limiter `ConcurrentHashMap` dans auth-service et notification-service — pas partage entre instances en cas de deploiement multi-instances.

**Correctif :** Migrer vers Redis-backed rate limiting (ex: bucket4j avec Redis) pour un deploiement distribue.

### MOYENNE-A5 : Flag `mustChangePassword` non enforce par filtre de securite

Le champ existe dans le modele User mais aucun filtre/intercepteur ne force le changement de mot de passe avant l'acces aux autres endpoints.

**Correctif :** Ajouter un `OncePerRequestFilter` qui redirige vers `/api/users/change-password` tant que `mustChangePassword == true`.

### BASSE-A6 : Validation de mot de passe incoherente

- `CreateUserRequest.java` : regex `^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$`
- `UserService.validatePasswordStrength()` : accepte tout special char `[^a-zA-Z0-9]`
- `UpdateUserRequest.java` : seulement `@Size(min = 8)`, pas de complexite

**Correctif :** Unifier la validation dans `UserService` et appliquer la meme regle partout.

---

## 4. Securite des Endpoints (OWASP A01:2021)

### MOYENNE-E1 : Actuator `loggers` expose dans tous les services

`management.endpoints.web.exposure.include: health,info,metrics,prometheus,loggers`

L'endpoint `/actuator/loggers` (authentifie) permet de :
- Lire les niveaux de log actuels
- **Modifier les niveaux de log a runtime** (ex: passer a DEBUG pour extraire des donnees sensibles des logs, ou desactiver des logs d'audit)

**Correctif :** Retirer `loggers` de l'exposition. Ou limiter a `health,info,metrics,prometheus` uniquement.

### MOYENNE-E2 : Pas de rate limiting sur `/api/public/**` (sauf register) au gateway

La route `auth-public` (`/api/public/**`) n'a pas de filtre `RequestRateLimiter`. Si ces endpoints incluent login, forgot-password, etc., ils sont vulnérables au brute-force.

**Correctif :** Ajouter un `RequestRateLimiter` sur toutes les routes publiques.

### MOYENNE-E3 : Pas de rate limiting sur `/api/events/**` et `/api/users/me/**`

Ces routes n'ont aucun rate limiting au gateway.

### MOYENNE-E4 : Fuite de messages d'erreur internes

`intervention-service/.../GlobalExceptionHandler.java` : `problem.setDetail("Erreur d'entree/sortie: " + ex.getMessage())` — expose les details internes (chemins de fichiers, erreurs reseau).

**Correctif :** Remplacer par un message generique. Logger le detail cote serveur.

### MOYENNE-E5 : Report/notification GlobalExceptionHandler ne derive pas de BaseExceptionHandler

`report-service` et `notification-service` ont des handlers independants qui ne beneficient pas des protections standardisees de `BaseExceptionHandler` (gestion DataIntegrityViolation, OptimisticLocking, etc.).

**Correctif :** Faire heriter de `BaseExceptionHandler` comme les autres services.

### BASSE-E6 : `SignatureController` et `PhotoController` sans `@PreAuthorize` au niveau classe

Seule l'annotation methode est utilisee. Un oubli sur un futur endpoint le laisserait sans protection.

**Correctif :** Ajouter `@PreAuthorize` au niveau classe pour la defense en profondeur.

### BASSE-E7 : Photo GET utilise `isAuthenticated()` — tout utilisateur connecte peut voir les photos de n'importe quelle intervention

`PhotoController.java` : `@PreAuthorize("isAuthenticated()")` sur le GET ne verifie pas l'appartenance a l'entreprise.

**Correctif :** Verifier que le `clientId` de l'intervention correspond au `company_id` du JWT pour les roles CLIENT_*.

---

## 5. Injection & XSS (OWASP A03:2021)

### POSITIF : Pas de risque SQL injection detecte

- Toutes les requetes JPQL utilisent des parametres liees (`:q`, `:name`, etc.)
- La seule requete native (`nextval`) n'a pas d'input utilisateur
- Aucune concatenation de chaines dans les requetes SQL/JPQL

### POSITIF : XSS protections en place

- `report-service/HtmlSanitizer.java` : stripping complet (`<script>`, `<iframe>`, `javascript:`, event handlers, `data:script`, CSS `expression()`)
- `report-service/ReportService.java` : prevention CSV injection (prefixe `\t` sur `=`, `+`, `-`, `@`)
- `media-service/FileService.java` : validation magic bytes, whitelist extensions, stripping metadonnees EXIF
- `shared-lib/RequestResponseLoggingFilter.java` : masquage des headers/body sensibles dans les logs
- Thymeleaf (notification-service) : auto-escaping par defaut

---

## 6. CSRF (OWASP A01:2021)

### POSITIF : CSRF desactive de maniere appropriee

Tous les services desactivent CSRF avec `.csrf(csrf -> csrf.disable())` — **correct** pour des APIs stateless JWT. Le gateway gere la protection CORS qui previent les attaques cross-origin.

---

## 7. Sessions & Tokens (OWASP A07:2021)

### POSITIF : Sessions stateless

Tous les services utilisent `SessionCreationPolicy.STATELESS` — pas de session serveur, pas de session fixation.

### POSITIF : JWT valide par issuer URI

`issuer-uri: ${KEYCLOAK_ISSUER_URI}` — JWKS-based validation, pas de secret symetrique partage.

### POSITIF : Validation cle JWT au demarrage

`EmailVerificationService` verifie que la cle HMAC fait >= 32 bytes au `@PostConstruct`.

### BASSE-T1 : Token de verification email meme secret que les autres JWT

Si `app.jwt.secret` est utilise ailleurs, une fuite de cle permet de forger des tokens de verification email.

**Correctif :** Utiliser une cle dediee pour les tokens de verification email.

---

## 8. Communication Inter-Services

### HAUTE-I1 : `client-service/InterventionSyncClient` ne transmet PAS le JWT

`client-service/.../InterventionSyncClient.java` n'a pas de `requestInterceptor` pour forwarder le token Bearer vers `intervention-service`.

Compare a `report-service/InterventionClient` qui le fait correctement.

**Impact :** Les appels sync client->intervention peuvent manquer d'authentification si intervention-service ne les traite pas comme internes.

---

## 9. Exposition des Secrets (OWASP A02:2021)

### POSITIF : Aucun secret hardcoded dans les fichiers YML/Java

Tous les 22 fichiers `application*.yml` utilisent `${ENV_VAR}`. Aucune cle API, mot de passe ou secret JWT n'est code en dur dans le code source d'application.

### POSITIF : `.env` est dans `.gitignore`

Le fichier `.env` n'est jamais tracke par git.

### POSITIF : Actuator env sanitization

`keys-to-sanitize: password,secret,token,credential,admin-client-secret,smtp` — les valeurs sensibles sont masquees dans `/actuator/env`.

---

## 10. Gestion des Erreurs

### POSITIF : RFC 7807 ProblemDetail

Tous les services retournent des reponses standardisees `ProblemDetail` avec des messages generiques (pas de stack traces).

### POSITIF : Request/Response logging filter

`shared-lib/RequestResponseLoggingFilter` masque les headers sensibles (`authorization`, `cookie`, `x-auth-token`, `x-api-key`) et les champs de body (`password`, `secret`, `token`, `accessToken`, `refreshToken`).

---

## 11. Bonnes Pratiques Detectees

| Pratique | Services |
|----------|----------|
| `ddl-auto: validate` | auth, client, intervention, report |
| `open-in-view: false` | auth, client, intervention, report |
| Swagger desactive en prod | TOUS |
| Circuit Breaker Resilience4j | gateway, client, intervention, report, notification |
| Rate limiting Bucket4j | auth (register, change-password), notification (email, push) |
| Audit logging comprehensif | auth, media, notification |
| Optimistic locking (`@Version`) | auth, client |
| Validation Bean (`@Valid`, `@NotBlank`, `@Email`, `@Pattern`) | TOUS |
| Role-based access (`@PreAuthorize`) | TOUS |
| Graceful shutdown | TOUS |
| Correlation ID tracking | TOUS (shared-lib) |
| HTML sanitizer | report, auth (email) |
| CSV injection prevention | report |
| Path traversal prevention | media |
| File magic byte validation | media |
| Antivirus scanning (ClamAV) | media |
| Image metadata stripping (EXIF) | intervention |
| Template whitelisting | notification |
| Brute force protection (lockout) | auth |
| SecureRandom password generation | auth |
| Keycloak session invalidation on password change | auth |
| `forward-headers-strategy` configure | TOUS |

---

## Plan de Correction par Priorite

### P0 — Immédiat (securite compromettante)
1. Retirer les mots de passe hardcoded des fichiers documentation
2. Tourner tous les secrets exposés dans l'historique git

### P1 — Court terme (avant mise en production)
3. Ajouter les security headers HTTP dans tous les SecurityConfig
4. Retirer `loggers` de l'exposition actuator
5. Remplacer ROPC par verification Keycloak Admin API
6. Ajouter rate limiting sur les routes publiques au gateway
7. Corriger `forward-headers-strategy: native` → `framework`
8. Propager le JWT dans `client-service/InterventionSyncClient`

### P2 — Moyen terme
9. Faire heriter report/notification GlobalExceptionHandler de BaseExceptionHandler
10. Ajouter filtre `mustChangePassword`
11. Unifier la validation de mot de passe
12. Ajouter CORS bean dans les services (ou retirer les proprietes inutilisees)
13. Renforcer les thresholds brute force
14. Ajouter `@PreAuthorize` classe a `SignatureController` et `PhotoController`
15. Corriger le message d'erreur IOException dans intervention GlobalExceptionHandler
16. Ajouter rate limiting au gateway sur `events` et `users/me`

### P3 — Long terme
17. Migrer vers un secrets manager (Vault)
18. Ajouter CI/CD secret scanning (gitleaks)
19. Ajouter CAPTCHA sur l'inscription
20. Migrer rate limiting vers Redis pour le distribué
21. Enforce auth sur photo GET par appartenance entreprise
