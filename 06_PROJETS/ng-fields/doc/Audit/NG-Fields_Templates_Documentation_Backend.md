# NG-Fields Backend — Templates & Modèles Documentaires
> **Guide pratique**  
> **Comment utiliser ces templates pour enrichir la doc**

---

## Table des matières
1. [Fichiers à créer immédiatement](#1-fichiers-à-créer-immédiatement)
2. [Modèles éditoriaux](#2-modèles-éditoriaux)
3. [Améliorations à appliquer aux docs existantes](#3-améliorations-à-appliquer)
4. [Exemples concrets](#4-exemples-concrets)

---

# 1. FICHIERS À CRÉER IMMÉDIATEMENT

## 1.1 | docs/backend/00-README.md
**Cible:** Index centralisé, point d'entrée unique

```markdown
# Backend NG-Fields — Documentation complète

Bienvenue ! Ce répertoire contient toute la doc technique du backend NG-Fields.

## 🚀 Démarrage (5 min)

Nouveau sur le projet ? Commencez ici:

1. **Comprendre le projet** → [01-ARCHITECTURE.md](01-ARCHITECTURE.md)
2. **Installer localement** → [02-SETUP-LOCAL.md](02-SETUP-LOCAL.md)
3. **Lancer les services** → [02-SETUP-LOCAL.md#lancer-les-services](02-SETUP-LOCAL.md#lancer-les-services)
4. **Tester l'API** → [05-API-REFERENCE.md](05-API-REFERENCE.md)

## 📚 Documentation par rôle

### Je suis développeur backend
- [Ajouter une nouvelle fonctionnalité?](04-SERVICES/) → Voir le service concerné
- [Tester mon code?](08-TESTING.md)
- [Déployer?](09-DEPLOYMENT.md)
- [Déboguer une erreur?](11-TROUBLESHOOTING.md)

### Je suis DevOps/Infrastructure
- [Déployer en production](09-DEPLOYMENT.md)
- [Monitoring & Logs](10-MONITORING.md)
- [Configuration CI/CD](09-DEPLOYMENT.md#ci-cd)

### Je suis project manager/product owner
- [Architecture générale](01-ARCHITECTURE.md)
- [État d'avancement des services](../AUDIT.md#31-état-des-8-services)
- [Roadmap](03-ROADMAP.md)

### Je suis nouveau et perdu
- Lire d'abord → [Glossaire métier](13-GLOSSARY.md)
- Puis → [02-SETUP-LOCAL.md](02-SETUP-LOCAL.md)
- Questions → Voir [FAQ](11-TROUBLESHOOTING.md#faq)

## 📖 Documentation complète

| # | Document | Sujet | Public | Durée lecture |
|---|----------|-------|--------|---------------|
| 00 | **README** | Index (ce doc) | Tous | 5 min |
| 01 | [ARCHITECTURE](01-ARCHITECTURE.md) | Vue d'ensemble, design patterns | Tech Lead, Arch | 20 min |
| 02 | [SETUP-LOCAL](02-SETUP-LOCAL.md) | Installation dev locale | Dev | 10 min |
| 03 | [STACK-TECHNIQUE](03-STACK-TECHNIQUE.md) | Versions, dépendances | Dev, Arch | 15 min |
| 04 | [SERVICES](04-SERVICES/) | Détail chaque µservice | Dev concerné | 30 min/service |
| 05 | [API-REFERENCE](05-API-REFERENCE.md) | Endpoints, DTOs, exemples | Frontend, QA | 20 min |
| 06 | [SECURITY](06-SECURITY.md) | Keycloak, JWT, RBAC | Dev, Security | 25 min |
| 07 | [DATABASE](07-DATABASE.md) | Schémas, migrations, queries | Dev, DBA | 30 min |
| 08 | [TESTING](08-TESTING.md) | Unit, intégration, coverage | Dev, QA | 25 min |
| 09 | [DEPLOYMENT](09-DEPLOYMENT.md) | Docker, K8s, CI/CD | DevOps | 30 min |
| 10 | [MONITORING](10-MONITORING.md) | Logs, Sentry, Prometheus | DevOps, Lead | 20 min |
| 11 | [TROUBLESHOOTING](11-TROUBLESHOOTING.md) | Erreurs, FAQ, debugging | Tous | 10+ min |
| 12 | [DECISIONS](12-DECISIONS.md) | ADRs, choix architecture | Arch, Lead | 15 min |
| 13 | [GLOSSARY](13-GLOSSARY.md) | Termes métier | Nouveaux | 10 min |

## 🔗 Liens rapides

- **Code source:** `Backend/` (ce répo)
- **API Swagger:** http://localhost:8080/swagger-ui.html (quand lancé)
- **Postman:** `docs/tests/postman-collection.json`
- **Issues:** GitHub Issues (tag: `backend`)
- **Discussions:** Slack #backend

## 📋 Conventions dans cette doc

```
🚀 Nouvelle fonctionnalité à faire
🐛 Bug connu
⚠️ Attention, risque
✅ Recommandation
❌ Ne pas faire
📌 Important à retenir
```

## ✏️ Contribuer à la doc

Trouver une erreur ou amélioration ?

1. Ouvrir issue avec tag `documentation`
2. OU: Pull request direct
3. Format: Markdown + ISO-8601 dates

**Style guide:** Voir [DECISIONS.md - Conventions](12-DECISIONS.md#conventions-documentation)

---

**Version doc:** 0.2.1  
**Dernière maj:** 2026-07-19  
**Prochaine mise à jour:** Après Phase 1
```

---

## 1.2 | docs/backend/02-SETUP-LOCAL.md
**Cible:** Onboarding dev en 10 min

```markdown
# Setup Local — Environnement de développement NG-Fields

> ⏱️ **Durée estimée:** 10 minutes (tout inclus)

## ✅ Prérequis

Avant de commencer, vérifier que vous avez:

```bash
# Vérifier installations
java -version          # Doit être 25+
mvn -version           # Doit être 3.9+
docker -version        # Doit être 24+
git --version          # Doit être 2.40+

# Affiche:
# java version "25.0.1" 2026-07-15
# Maven 3.9.6 (ou +)
# Docker version 24.0.6
# git version 2.42.0
```

### Si absent: installer via Homebrew (macOS) ou apt (Linux)
```bash
# macOS
brew install java@25 maven docker-desktop

# Ubuntu/Debian
sudo apt install openjdk-25-jdk maven docker.io docker-compose
```

## 🚀 Installation (5 étapes)

### 1️⃣ Cloner le repo
```bash
git clone https://github.com/moltesse12/ng-fields.git
cd ng-fields
```

### 2️⃣ Copier configuration
```bash
# Root du projet
cp .env.example .env

# Éditer .env avec vos valeurs (sinon defaults OK)
# Important: Keycloak admin password
nano .env
```

Contenu `.env` minimal:
```bash
# Postgres
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=ng_fields
POSTGRES_USER=ng_user
POSTGRES_PASSWORD=changeme_dev  # À changer en prod!

# Keycloak
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123  # À changer!

# Redis
REDIS_PORT=6379

# Autres
SPRING_PROFILES_ACTIVE=dev
MEDIA_UPLOAD_DIR=/tmp/ng-fields-media
```

### 2️⃣ Lancer les services externes (Docker Compose)
```bash
# À la racine du projet
docker compose -f infra/docker-compose.yml up -d

# Vérifier que tout est UP
docker compose -f infra/docker-compose.yml ps

# Affiche:
# NAME              STATUS
# postgres          Up 2 minutes
# redis             Up 2 minutes  
# keycloak          Up 2 minutes
```

**⏱️ Attendre 30-60 sec que Keycloak soit prêt** (logs: "KC-SERVICES0050")

### 3️⃣ Installer dépendances Maven
```bash
cd Backend
./mvnw clean dependency:resolve
```

### 4️⃣ Lancer les 8 services Spring Boot

**Option A: Terminal (une par fenêtre)**
```bash
cd Backend

# Terminal 1: gateway-service
./mvnw spring-boot:run -pl gateway-service

# Terminal 2: auth-service
./mvnw spring-boot:run -pl auth-service

# Terminal 3: client-service
./mvnw spring-boot:run -pl client-service

# Terminal 4: intervention-service
./mvnw spring-boot:run -pl intervention-service

# Terminal 5: media-service
./mvnw spring-boot:run -pl media-service

# Terminal 6: notification-service
./mvnw spring-boot:run -pl notification-service

# Terminal 7: report-service
./mvnw spring-boot:run -pl report-service
```

**Option B: IDE (meilleur UX)**
- Ouvrir `Backend/pom.xml` dans IntelliJ/VS Code
- Run Configurations → Créer 8 configurations Spring Boot
- Lancer en parallèle via Run → All in Parallel

**Option C: Docker (une fois en prod)**
```bash
docker compose -f infra/docker-compose.backend.yml up
```

### 5️⃣ Vérifier que tout marche

```bash
# 1. Test gateway
curl http://localhost:8080/health
# Réponse: {"status":"UP"}

# 2. Test Swagger
# Ouvrir: http://localhost:8080/swagger-ui.html

# 3. Test login
curl -X POST http://localhost:8088/realms/ng-fields/protocol/openid-connect/token \
  -d 'client_id=backend-gateway' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password' \
  -d 'client_secret=your-secret-from-.env'

# Retour: {"access_token": "eyJhbGc...", ...}
```

✅ **Si tout fonctionne:** Vous êtes prêt à coder !

---

## 🛠️ Opérations courantes

### Arrêter les services
```bash
# Keycloak, Postgres, Redis
docker compose -f infra/docker-compose.yml down

# Spring Boot: Ctrl+C dans chaque terminal
```

### Réinitialiser la BD
```bash
docker compose -f infra/docker-compose.yml down -v  # -v = delete volumes
docker compose -f infra/docker-compose.yml up -d postgres
# Flyway va auto-migrer au démarrage du service
```

### Consulter logs
```bash
# Postgres
docker compose -f infra/docker-compose.yml logs -f postgres

# Keycloak
docker compose -f infra/docker-compose.yml logs -f keycloak

# Redis
docker compose -f infra/docker-compose.yml logs -f redis

# Spring Boot: visible dans terminal
```

### Modifier la conf Keycloak
```bash
# Accéder admin console
# URL: http://localhost:8088/admin
# User: admin / Pass: [KEYCLOAK_ADMIN_PASSWORD from .env]

# Créer utilisateurs de test:
# → Realm ng-fields
# → Users
# → Create user
#   - Username: tech1, Email: tech1@ng-stars.tg
#   - Set password: tech123
#   - Assign roles: ROLE_TECHNICIAN
```

### Importer réalm Keycloak depuis fichier
```bash
# Keycloak import automatique au démarrage
# Fichier: infra/keycloak/realm-export.json
# (docker-compose.yml le monte automatiquement)

# Pour modifier export après boot:
# Admin console → Realm export → JSON (Download)
```

---

## ⚠️ Problèmes courants

### ❌ Port 5432 (Postgres) déjà utilisé
```bash
# Vérifier
lsof -i :5432

# Solution 1: Tuer le processus
kill -9 <PID>

# Solution 2: Changer port dans .env
POSTGRES_PORT=5433  # Nouveau port
# ⚠️ Puis mettral aussi dans docker-compose.yml et application.yml
```

### ❌ Port 8080 (Gateway) déjà utilisé
```bash
# Changer dans gateway-service/application.yml
server:
  port: 8090  # Nouveau port
```

### ❌ Keycloak ne démarre pas
```bash
# Peut prendre 60+ secondes
docker compose -f infra/docker-compose.yml logs keycloak | tail

# Chercher: "Keycloak 26.6.2 started"
# Si erreur BD: Postgres pas encore prêt
#   → Arrêter Keycloak, relancer après Postgres
docker compose -f infra/docker-compose.yml restart keycloak
```

### ❌ "Failed to connect to PostgreSQL"
```bash
# Vérifier Postgres lancé
docker compose -f infra/docker-compose.yml logs postgres

# Vérifier credentials dans .env
docker exec ng-fields-postgres psql -U ng_user -d ng_fields -c "SELECT 1;"

# Si permission denied: reset
docker compose -f infra/docker-compose.yml down -v
docker compose -f infra/docker-compose.yml up -d postgres
# Attendre 10 sec puis relancer apps
```

### ❌ Tests échouent localement
```bash
# Vérifier Redis + Postgres up
docker compose -f infra/docker-compose.yml ps

# Lancer tests avec output
./mvnw test -X

# Ou un service spécifique
./mvnw test -pl auth-service
```

## 📚 Prochaines étapes

- Lire [01-ARCHITECTURE.md](01-ARCHITECTURE.md) pour comprendre le design
- Consulter [04-SERVICES/](04-SERVICES/) pour le service que vous dev
- Voir [05-API-REFERENCE.md](05-API-REFERENCE.md) pour les endpoints
- Commencer par [08-TESTING.md](08-TESTING.md) pour TDD

## 💡 Tips

```bash
# Watcher: redémarrer Spring Boot si changement fichier
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.devtools.restart.enabled=true"

# Debug mode (port 5005)
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
# IntelliJ: Run → Attach to Process → localhost:5005

# Profiler: chercher slow queries
# Enable JPA SQL logging:
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

**Support:** Voir [11-TROUBLESHOOTING.md](11-TROUBLESHOOTING.md) ou ouvrir issue GitHub
```

---

## 1.3 | docs/backend/06-SECURITY.md
**Cible:** Keycloak + Auth expliquée

```markdown
# Sécurité — Keycloak, JWT, RBAC

## 🔐 Vue d'ensemble

NG-Fields utilise **Keycloak 26.6.2** pour l'authentification et l'autorisation.

```
┌──────────────┐
│  Frontend    │ (Angular, Flutter)
│ (1) Login    │
└──────┬───────┘
       │ POST /realms/ng-fields/protocol/openid-connect/token
       ▼
┌──────────────────────┐
│     Keycloak         │
│   (port 8088)        │  (2) Valide credentials
│ realm: ng-fields     │  (3) Retourne JWT
└──────┬───────────────┘
       │ access_token: "eyJhbGc..."
       ▼
┌──────────────┐
│  Frontend    │  (4) Inclut JWT dans Authorization header
│  Stocke JWT  │
└──────┬───────┘
       │ GET /api/clients
       │ Header: "Bearer eyJhbGc..."
       ▼
┌──────────────────────────┐
│    API Gateway (8080)    │
│ (5) Valide JWT signature │
│ (6) Extrait rôles        │
└──────┬───────────────────┘
       │ ✅ Autorisé → Forward à service
       ▼
   Services auth-service, client-service, etc.
```

### Flux de sécurité (5 étapes)

#### Step 1: Login
```bash
# Frontend envoie credentials
curl -X POST http://localhost:8088/realms/ng-fields/protocol/openid-connect/token \
  -d '{
    "client_id": "backend-gateway",
    "username": "tech1@ng-stars.tg",
    "password": "tech123",
    "grant_type": "password"
  }' \
  -H "Content-Type: application/x-www-form-urlencoded"
```

#### Step 2: Keycloak valide
- Username + password contre BD Keycloak
- Génère JWT signé avec clé privée Keycloak
- Retourne: `access_token`, `refresh_token`, `id_token`

#### Step 3: Réponse JWT
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "refresh_expires_in": 86400,
  "refresh_token": "eyJhbGciOiJSUzI1NiI...",
  "token_type": "Bearer",
  "id_token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

#### Step 4: Frontend stocke JWT
```javascript
// Angular (app.interceptor.ts)
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('access_token');
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(req);
  }
}
```

#### Step 5: Gateway valide JWT
```java
// KeycloakJwtAuthenticationConverter (gateway-service)
@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
    Collection<GrantedAuthority> roles = extractRoles(jwt);
    JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, roles);
    return Mono.just(token);
  }
  
  private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
    // realm_access: { "roles": ["ROLE_TECHNICIAN", "ROLE_USER"] }
    return ((List<String>) realmAccess.get("roles"))
      .stream()
      .map(SimpleGrantedAuthority::new)
      .collect(toSet());
  }
}
```

---

## 🎯 Rôles & Permissions

### Rôles NG-Fields

| Rôle | Description | Permissions | Exemple |
|------|-------------|-------------|---------|
| **ROLE_ADMIN** | Administrateur système | Tout accès | Gérer users, audit |
| **ROLE_MANAGER** | Manager d'équipe | Créer interventions, voir clients, voir techniciens | Assigner interventions |
| **ROLE_TECHNICIAN** | Technicien terrain | Voir/modifier propres interventions | Saisir photos, signer |
| **ROLE_CLIENT** | Client (portail) | Voir propres interventions (readonly) | Consulter historique |

### Mapping Keycloak → Spring Security

Keycloak realm roles → Spring `ROLE_*` authorities

```
Keycloak realm
  ├── ADMIN          → ROLE_ADMIN
  ├── MANAGER        → ROLE_MANAGER
  ├── TECHNICIAN     → ROLE_TECHNICIAN
  └── CLIENT         → ROLE_CLIENT
```

Configuration: `RealmRoleConverter.java` (shared-lib)

### Contrôle d'accès par service

#### auth-service
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
  
  // Tout le monde authentifié peut voir son profil
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
    String userId = jwt.getSubject();
    return ResponseEntity.ok(userService.getById(userId));
  }
  
  // Seuls ADMIN peuvent créer users
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest req) {
    return ResponseEntity.status(201).body(userService.create(req));
  }
  
  // Seuls ADMIN ou MANAGER peuvent voir tous les users
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<Page<UserResponse>> listUsers(Pageable page) {
    return ResponseEntity.ok(userService.listAll(page));
  }
}
```

#### intervention-service
```java
@RestController
@RequestMapping("/api/interventions")
public class InterventionController {
  
  // TECHNICIAN voit seulement ses interventions
  @GetMapping("/{id}")
  public ResponseEntity<InterventionResponse> getIntervention(
      @PathVariable UUID id,
      @AuthenticationPrincipal Jwt jwt) {
    
    String userId = jwt.getSubject();
    Intervention inter = interventionService.getById(id);
    
    // Vérifier permission
    if (!inter.getTechnicianId().equals(userId) && 
        !hasRole(jwt, "ADMIN", "MANAGER")) {
      throw new ForbiddenException("Pas d'accès à cette intervention");
    }
    
    return ResponseEntity.ok(interventionService.toDTO(inter));
  }
}
```

---

## 🔑 JWT Claims (Token contents)

Exemple JWT décodé:

```json
{
  "exp": 1719885600,
  "iat": 1719882000,
  "jti": "a1b2c3d4-e5f6...",
  "iss": "http://localhost:8088/realms/ng-fields",
  "aud": "account",
  "sub": "f47ac10b-58cc-4372-a567-0e02b2c3d479",  // UUID user
  "typ": "Bearer",
  "preferred_username": "john.doe@ng-stars.tg",
  "email": "john.doe@ng-stars.tg",
  "email_verified": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "realm_access": {
    "roles": [
      "ROLE_TECHNICIAN",
      "ROLE_USER",
      "offline_access",
      "uma_authorization"
    ]
  },
  "resource_access": {
    "backend-gateway": {
      "roles": ["technician-access"]
    }
  }
}
```

### Claims utiles

| Claim | Type | Exemple | Usage |
|-------|------|---------|-------|
| `sub` | String (UUID) | `f47ac10b-...` | Identifier unique user |
| `preferred_username` | String | `john.doe@ng-stars.tg` | Username |
| `email` | String | `john@example.com` | Contact |
| `realm_access.roles` | Array | `["ROLE_ADMIN"]` | Permissions |
| `exp` | Timestamp | 1719885600 | Token expiration |
| `iat` | Timestamp | 1719882000 | Token issued at |

---

## 🚨 Gestion des erreurs de sécurité

### 401 Unauthorized
**Cause:** Token absent, expiré, ou invalide

```bash
# Manque Authorization header
curl http://localhost:8080/api/clients
# Retour: 401 Unauthorized

# Solution: Inclure token
curl http://localhost:8080/api/clients \
  -H "Authorization: Bearer $TOKEN"
```

### 403 Forbidden
**Cause:** Authentifié mais pas autorisé pour cette ressource

```bash
# TECHNICIAN essaie d'accéder à admin
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer $TECH_TOKEN"
# Retour: 403 Forbidden (insufficient permissions)
```

### 419 Token Expired
**Cause:** JWT expiré (défaut: 1 heure)

```bash
# Solution: Refresh le token
curl -X POST http://localhost:8088/realms/ng-fields/protocol/openid-connect/token \
  -d "grant_type=refresh_token" \
  -d "refresh_token=$REFRESH_TOKEN" \
  -d "client_id=backend-gateway"
# Retour: nouveau access_token
```

---

## 🔄 Refresh Token Flow

```
Moment 1: User login
─────────────────
1. POST /token → access_token (1h) + refresh_token (24h)
2. Frontend stocke les deux
3. Access token dans Authorization header

Moment 2: Access token expire (1h+)
─────────────────────────────────
1. Frontend détecte 401
2. POST /token (grant_type=refresh_token)
3. Obtient nouveau access_token
4. Retry requête originelle
```

Implémentation (Angular):

```typescript
// auth.interceptor.ts
intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(this.addToken(req)).pipe(
    catchError(err => {
      if (err.status === 401) {
        return this.authService.refreshToken().pipe(
          switchMap(token => {
            return next.handle(this.addToken(req));
          }),
          catchError(() => this.authService.logout())
        );
      }
      return throwError(() => err);
    })
  );
}
```

---

## 📋 Checklist sécurité

- [ ] Jamais stocker JWT en cookies (XSS risque) → localStorage OK
- [ ] Inclure `Authorization: Bearer $TOKEN` dans TOUS les appels API
- [ ] Refresh token quand 401
- [ ] Logout = supprimer localStorage
- [ ] HTTPS en production (TLS 1.3+)
- [ ] CORS bien configuré (pas `*`)
- [ ] Rate limiting activé sur Gateway
- [ ] Sentry connecté pour erreurs auth

---

**Voir aussi:** [09-DEPLOYMENT.md - Production Security](09-DEPLOYMENT.md#sécurité-en-production)
```

---

## 1.4 | docs/backend/03-STACK-TECHNIQUE.md

```markdown
# Stack Technique — Versions, Dépendances, Choix

## Versions

| Composant | Version | Raison | EOL |
|-----------|---------|--------|-----|
| **Java** | 25 | LTS + performance | 2028-09 |
| **Spring Boot** | 4.0.6 | Stability + 3.x EOL | 2026-12 |
| **Maven** | 3.9+ | Build manager | — |
| **Keycloak** | 26.6.2 | OAuth2/OpenID Connect | 2026-06 |
| **PostgreSQL** | 16 | Relationnelle + JSON | 2031-10 |
| **Redis** | 7 | Cache + rate limiting | 2025-01 |
| **OpenPDF** | 1.4.1 | PDF sans iText | — |

## Dépendances clés (pom.xml)

### Core Spring Boot
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <version>${spring-boot.version}</version>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### Database
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-postgresql</artifactId>
</dependency>
```

### Cache & Rate Limiting
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-ratelimiter</artifactId>
</dependency>
```

### API & Documentation
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.0.4</version>
</dependency>
<dependency>
  <groupId>com.github.librepdf</groupId>
  <artifactId>openpdf</artifactId>
  <version>1.4.1</version>
</dependency>
```

### Tests
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>testcontainers</artifactId>
  <version>1.19.1</version>
  <scope>test</scope>
</dependency>
```

## Décisions technologiques (Pourquoi)

### ✅ Spring Boot over Node.js
- **Raison:** Java existant dans l'orga
- **Trade-off:** Courbe apprentissage Node plus douce mais Java + stable en production

### ✅ Keycloak over Supabase Auth
- **Raison:** Découplage auth/DB, compatible multi-projets
- **Trade-off:** Supabase plus simple mais coupling avec DB

### ✅ OpenPDF over iText/pdfbox
- **Raison:** Libre (pas GPL/commercial)
- **Trade-off:** iText plus riche mais payant

### ✅ PostgreSQL over MongoDB
- **Raison:** Schéma structuré, ACID, transactional
- **Trade-off:** MongoDB plus flexible mais moins sûr pour métier

### ✅ Redis over Memcached
- **Raison:** Structures riches (sorted sets, streams) + Streams pour events
- **Trade-off:** Memcached plus simple mais moins expressif

## Compatibilité versions

```
Spring Boot 4.0.6
├── Spring Framework 6.1.x
├── Spring Cloud 2024.0.0
├── Hibernate 6.4.x
└── Java 21+

Keycloak 26.6.2
└── Java 17+
└── PostgreSQL 12+

OpenPDF 1.4.1
└── Java 8+
```

⚠️ **Upgrade path:** Spring Boot 4.0 → 4.1 possible, nécessite tests

## Profils activables

```bash
# Development (défaut)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Production
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Test
./mvnw test

# Profiling
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=profile"
```

Fichiers config:
```
Backend/
├── **/resources/application.yml          # Base
├── **/resources/application-dev.yml      # Dev-specific
├── **/resources/application-prod.yml     # Production overrides
└── **/resources/application-test.yml     # Test overrides
```
```

---

# 2. MODÈLES ÉDITORIAUX

## 2.1 | Template service documentation
**À appliquer à**: Chaque fichier `04-SERVICES/XX-servicename.md`

```markdown
# [Service Name] — Documentation

> **Artifact:** `[artifact-id]`  
> **Port:** [port]  
> **Status:** [✅ Prod / 🟡 Alpha / 🔵 Skeleton]  
> **Complétude:** [%]

## Vue d'ensemble

[Paragraph: Qu'est-ce que ce service? Quelles responsabilités?]

```
Dépendances:
[Service] ──→ [Dépend de]
```

## Architecture interne

### Classes principales

| Classe | Rôle | Status |
|--------|------|--------|
| `XyzServiceApplication` | Entrée Spring Boot | ✅ |
| `XyzController` | REST endpoints | ✅ |
| `XyzService` | Logique métier | [status] |
| `Xyz` (JPA Entity) | BD | [status] |

### Flow de requête

```
GET /api/xyz/{id}
    ↓ [XyzController]
    ↓ [XyzService.getById(id)]
    ↓ [XyzRepository.findById(id)]
    ↓ PostgreSQL
    ↓ [Entity mapper to DTO]
    ↓ 200 OK + XyzResponse
```

## API Endpoints

| Verbe | Chemin | Auth | Description |
|-------|--------|------|-------------|
| GET | `/api/xyz` | ✅ | Lister tous (paginated) |
| POST | `/api/xyz` | ✅ | Créer |
| GET | `/api/xyz/{id}` | ✅ | Détail |
| PUT | `/api/xyz/{id}` | ✅ | Modifier |
| DELETE | `/api/xyz/{id}` | ✅ | Supprimer |

### Exemple: Créer
```bash
POST /api/xyz
Content-Type: application/json
Authorization: Bearer $TOKEN

{
  "name": "Example"
}

# Réponse 201 Created
{
  "id": "f47ac10b-58cc-4372...",
  "name": "Example",
  "createdAt": "2026-07-19T10:30:00Z"
}
```

## DTOs

### Entrée
```java
public record CreateXyzRequest(
  @NotBlank String name,
  String description
) {}
```

### Sortie
```java
public record XyzResponse(
  UUID id,
  String name,
  String description,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
```

## Configuration

### application.yml
```yaml
app:
  xyz:
    # Options ici
    enabled: true
    max-items: 1000
```

### Keycloak Roles
- ROLE_ADMIN: Full access
- ROLE_MANAGER: Create/edit own
- ROLE_USER: Read-only

## Tests

### Unit
```bash
./mvnw test -pl [service]
# Target: 80% coverage
```

### Intégration
```bash
./mvnw test -pl [service] -P integration
# Teste avec vrai Postgres/Redis
```

### Examples
[Voir 08-TESTING.md pour template test]

## Dépannage

### Erreur: [Error message]
**Cause:** [Explanation]  
**Solution:** [Steps]

## Roadmap

- [ ] [Feature 1]
- [ ] [Feature 2]

## Liens

- [API complète](05-API-REFERENCE.md)
- [Tests](08-TESTING.md)
- [Sécurité](06-SECURITY.md)
```

---

## 2.2 | Template erreur standard

```java
// shared-lib/src/main/java/tg/ngstars/common/dto/StandardErrorResponse.java
package tg.ngstars.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Format de réponse d'erreur standard pour tous les services.
 * 
 * Exemple:
 * {
 *   "code": "ERR_CLIENT_NOT_FOUND",
 *   "message": "Client with id 123 not found",
 *   "timestamp": "2026-07-19T10:30:00Z",
 *   "path": "/api/clients/123",
 *   "details": {
 *     "id": "123",
 *     "reason": "deleted"
 *   }
 * }
 */
public record StandardErrorResponse(
  String code,                                    // Machine-readable error code
  String message,                                // Human-readable message
  LocalDateTime timestamp,
  String path,                                   // Request path
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Map<String, String> details                    // Additional context (optional)
) {}
```

---

# 3. AMÉLIORATIONS À APPLIQUER

## 3.1 | Fusionner docs backend fragmentées

**Avant:** 4 fichiers avec redondances
- `Backend_0.2.md`
- `BACKEND_0.1.md`
- `Backend.md`
- `ARCHITECTURE.md`

**Après:** 1 structure claire
```
docs/backend/
├── 00-README.md
├── 01-ARCHITECTURE.md
└── 04-SERVICES/*
```

**Action:**
```bash
# 1. Créer répertoire
mkdir -p docs/backend/04-SERVICES

# 2. Extraire par section:
# - Backend_0.2.md (4.1-4.8) → 04-SERVICES/
# - Backend_0.2.md (6) → 06-SECURITY.md
# - Backend_0.2.md (5) → 07-DATABASE.md
# - Backend_0.2.md (7) → 09-DEPLOYMENT.md

# 3. Supprimer anciens docs (garder en /archive)
mv Backend_0.1.md Backend.md archive/
```

## 3.2 | Ajouter Workflow badges (README.md)

```markdown
# NG-Fields Backend

[![Build](https://github.com/moltesse12/ng-fields/actions/workflows/backend.yml/badge.svg)](...)
[![Tests](https://codecov.io/gh/moltesse12/ng-fields/branch/main/graph/badge.svg)](...)
[![Java 25](https://img.shields.io/badge/Java-25-blue)](...)
[![Spring Boot 4.0.6](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)](...)
```

## 3.3 | Créer `.env.example` unique

```bash
# Backend/.env.example
SPRING_PROFILES_ACTIVE=dev
# ... (voir section 1.2 SETUP-LOCAL)
```

## 3.4 | Ajouter table of contents auto (README)

```bash
# Installer tool
brew install doctoc

# Générer TOC
doctoc docs/backend/00-README.md
```

---

# 4. EXEMPLES CONCRETS

## 4.1 | Exemple: Documenter auth-service

**Créer:** `docs/backend/04-SERVICES/03-auth-service.md`

```markdown
# auth-service — Authentification & Users

> **Artifact:** `auth-service`  
> **Port:** 8081  
> **Status:** ✅ Production  
> **Complétude:** 90%

## Vue d'ensemble

Service central pour gestion des utilisateurs et audit.

Responsabilités:
- Créer/modifier users (via Keycloak Admin API)
- Assigner rôles
- Logging actions (AuditLog)
- Générer rapports audit

## Architecture

```
UserController
  ↓
UserService
  ├── KeycloakAdminConfig (appels Keycloak Admin API)
  └── AuditService (enregistre actions)

Entités:
  - User (synchronisé avec Keycloak)
  - AuditLog (local PostgreSQL)
```

## API Endpoints

| Endpoint | Rôles | Description |
|----------|-------|-------------|
| `POST /api/users` | ADMIN | Créer user |
| `GET /api/users/me` | ALL | Profil courant |
| `GET /api/users` | ADMIN,MANAGER | Lister users |
| `PUT /api/users/{id}` | ADMIN,OWNER | Modifier |
| `DELETE /api/users/{id}` | ADMIN | Supprimer |
| `POST /api/users/{id}/roles` | ADMIN | Assigner rôle |
| `GET /api/audit` | ADMIN | Audit logs |

## DTOs

### CreateUserRequest
```java
public record CreateUserRequest(
  @NotBlank @Email String email,
  @NotBlank String firstName,
  @NotBlank String lastName,
  List<String> roles  // ["ROLE_TECHNICIAN", "ROLE_USER"]
) {}
```

## Tests

✅ UserServiceTest (2 tests actuellement)
- ❌ À ajouter: 15+ tests
  - createUser()
  - assignRole()
  - auditLogging()
  - etc.

## Roadmap

- [ ] Password reset flow (email)
- [ ] 2FA (TOTP)
- [ ] Email verification
- [ ] Audit dashboard

## Configuration

```yaml
# application-prod.yml
app:
  auth:
    password-reset-expiry-hours: 24
    max-failed-login-attempts: 5
```

## Pour l'équipe mobile

Pour Flutter: accéder `/api/users/me` une fois logué (JWT de Keycloak)

```dart
final response = await http.get(
  Uri.parse('http://localhost:8081/api/users/me'),
  headers: {'Authorization': 'Bearer $accessToken'},
);
```

## Troubleshooting

### ❌ "401 Unauthorized" en appelant `/api/users`
**Cause:** Token Keycloak invalide/expiré  
**Solution:** Refresh token (voir 06-SECURITY.md)

### ❌ "403 Forbidden" pour créer user
**Cause:** Vous avez ROLE_TECHNICIAN, besoin ROLE_ADMIN  
**Solution:** Demander à admin d'assigner ROLE_ADMIN

---

**Voir aussi:** [06-SECURITY.md](../06-SECURITY.md), [05-API-REFERENCE.md](../05-API-REFERENCE.md)
```

---

## 4.2 | Exemple: Documenter limitation connue

```markdown
## 🐛 Limitations connues

### L1. SyncController incomplete (intervention-service)
**État:** 80% vide  
**Impact:** Offline sync ne fonctionne pas  
**Workaround:** Utiliser API v1 (non-offline) en attendant  
**Fix prévu:** Phase 1 roadmap (1-2 semaines)

**Détail:**
```java
// Actuellement:
@PostMapping("/sync")
public ResponseEntity<SyncResponse> sync(@RequestBody SyncRequest req) {
  // TODO: Implémenter
  return ResponseEntity.ok(new SyncResponse());
}
```

### L2. Media-service pas de antivirus
**État:** À faire  
**Impact:** Risque upload malware  
**Workaround:** Validation extension manuelle  
**Fix prévu:** Phase 1 roadmap (1 semaine)
```

---

# 📝 Résumé

## À faire maintenant (2-3 jours)

```bash
# 1. Créer structure
mkdir -p docs/backend/04-SERVICES

# 2. Créer 5 fichiers prioritaires
touch docs/backend/{00-README,02-SETUP-LOCAL,03-STACK-TECHNIQUE,06-SECURITY}.md

# 3. Utiliser templates sections 1.2-1.4

# 4. Créer .env.example unique

# 5. Mettre à jour README principal
# → Liens vers docs/backend/00-README.md
```

## Code exemple complet (prêt à copier-coller)

Voir section 4.1 pour auth-service complet (120 lignes)

---

> **Document créé:** 2026-07-19  
> **Prochaine révision:** Après Phase 1 (2 sem)
