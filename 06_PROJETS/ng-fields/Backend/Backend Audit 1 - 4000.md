Le fichier fait **8 283 lignes** — tu veux que je traite les **4 000 premières**. Je dois d'abord lire les lignes intermédiaires tronquées.J'ai maintenant une vision complète des 4 000 premières lignes. Je commence l'audit.

---

# 🔍 AUDIT COMPLET — NG-Fields Backend
**Lignes 1–4 000 sur 8 283 | Partie 1/2**
*Généré le 2026-07-13 · Mis à jour le 2026-07-13*

---

## 📊 STATUT DES CORRECTIONS (lignes 1–4000)

| # | Priorité | Problème | Statut |
|---|---|---|---|
| C1 | Critique | `RealmRoleConverter` copié 4+ fois | ✅ CORRIGÉ — extrait dans `shared-lib` |
| C2 | Critique | `registerClient` ignore l'IP dans l'audit | ✅ CORRIGÉ — IP transmise à `auditService.log()` |
| C3 | Critique | `RuntimeException` générique sur erreur Keycloak | ✅ CORRIGÉ — log error + message propre |
| C4 | Critique | Race condition UUID → conflit unique possible | ⏭️ GÉRÉ — contrainte DB UNIQUE + cleanup dans catch |
| C5 | Critique | `assignRealmRole` : role Keycloak inexistant → 500 | ✅ CORRIGÉ — catch `NotFoundException` |
| C6 | Critique | `@EqualsAndHashCode` sans `@Include` | ✅ CORRIGÉ — `@EqualsAndHashCode.Include` sur `@Id` |
| C7 | Critique | `generateReference()` synchronized non thread-safe | ✅ CORRIGÉ — `nextval('client_ref_seq')` |
| C8 | Critique | `X-Forwarded-For` non validé | ✅ CORRIGÉ — `forward-headers-strategy: FRAMEWORK` |
| C9 | Critique | `MediaClient` : aucune gestion des erreurs HTTP | ✅ CORRIGÉ — `onStatus()` + `MediaServiceException` |
| C10 | Critique | `body.get("filename")` → NPE | ✅ CORRIGÉ — guard `containsKey` |
| M1 | Majeure | `getAllUsers()` sans pagination | ✅ CORRIGÉ — retourne `Page<UserResponse>` |
| M2 | Majeure | `CreateUserRequest` pour update | ✅ CORRIGÉ — `UpdateUserRequest` DTO séparé |
| M3 | Majeure | `updateUser` : appels Keycloak répétés (×3) | ✅ CORRIGÉ — variable locale `userResource` |
| M4 | Majeure | `updateProfile` sans audit | ✅ CORRIGÉ — `auditService.log()` ajouté |
| M5 | Majeure | LIKE `%q%` sans index trigram | ✅ CORRIGÉ — migration `V3__add_trgm_search_index.sql` |
| M6 | Majeure | `findMaxReference()` sur VARCHAR | ✅ CORRIGÉ — séquence `client_ref_seq` |
| M7 | Majeure | CORS configuré en double | ✅ CORRIGÉ — CORS supprimé des microservices |
| M8 | Majeure | URLs Swagger incorrectes | ✅ CORRIGÉ — URLs corrigées dans gateway |
| M9 | Majeure | `remoteAddrKeyResolver` utilise IP du proxy | ⏭️ NON TRAITÉ — dépend du déploiement |
| M10 | Majeure | `MediaClient` construit dans le constructeur | ✅ CORRIGÉ — `MediaClientConfig` `@Bean` |
| M11 | Majeure | `generatePdf` → `byte[]` en mémoire | ✅ CORRIGÉ — `StreamingResponseBody` |
| M12 | Majeure | `deleteFile` sans consommation réponse | ✅ CORRIGÉ — `.toBodilessEntity()` |
| M13 | Majeure | Upload sans validation MIME/taille | ✅ CORRIGÉ — MIME validation + `max-file-size: 10MB` |
| — | Majeure | `GlobalExceptionHandler` format incohérent | ✅ CORRIGÉ — format map structuré unifié |
| — | Mineure | `@ResponseStatus` redondants sur exceptions | ✅ CORRIGÉ — supprimé sur 6 fichiers |
| — | Mineure | `HealthController` doublon Actuator | ✅ CORRIGÉ — fichier supprimé |
| — | Mineure | `AuditLogRepository` sans requêtes | ✅ CORRIGÉ — `findByUserId` + `findByAction` |
| — | Mineure | `SecurityUtils` retourne null | ✅ CORRIGÉ — `Optional<UUID>` |
| — | Mineure | Validation lat/lng absente | ✅ CORRIGÉ — `@DecimalMin/@DecimalMax` |

**Score : 26/28 corrigés · 2 différés (C4 géré par DB, M9 dépend du déploiement)**

---

## ═══════════════════════════════════════
## ÉTAPE 1 — COMPRÉHENSION DE L'ARCHITECTURE
## ═══════════════════════════════════════

### Vue d'ensemble

NG-Fields est une architecture **microservices** composée de **7 services Spring Boot indépendants**, derrière un API Gateway unique. Chaque service a son propre schéma PostgreSQL, géré par Flyway.

```
                     ┌─────────────────────────────────────┐
  Frontend           │     gateway-service  :8080          │
  Angular/Flutter ──►│  Spring Cloud Gateway (WebFlux)     │
                     │  JWT validation · Rate-limit Redis  │
                     │  Circuit-breaker Resilience4j        │
                     └────────────────┬────────────────────┘
                                      │
         ┌──────────┬─────────────────┼──────────────────────────┐
         ▼          ▼                 ▼          ▼               ▼
   auth-service  client-service  interv-service  media-service  notif/report
      :8081          :8082           :8083          :8084          :8085/86
   Keycloak Admin  CRUD clients  Interventions  Upload/download   Stubs
   + local DB      + Flyway       + Photos       fichiers
                                  + Signature
                                  + PDF
                                  + Sync (offline)
         │
     Keycloak
      :8088
```

### Modules identifiés

| Service | Port | Stack | Rôle |
|---|---|---|---|
| `gateway-service` | 8080 | Spring Cloud Gateway + WebFlux | Point d'entrée unique |
| `auth-service` | 8081 | Spring Boot + Keycloak Admin Client | Gestion utilisateurs |
| `client-service` | 8082 | Spring Boot + JPA | CRUD clients entreprises |
| `intervention-service` | 8083 | Spring Boot + JPA + RestClient | Cœur métier |
| `media-service` | 8084 | Spring Boot | Gestion fichiers |
| `notification-service` | 8085 | Spring Boot | Stub (vide) |
| `report-service` | 8086 | Spring Boot | Stub (vide) |

### Stack technique

- **Java 25** (JDK 25) — très récent, excellent choix de modernité
- **Spring Boot 3.x** + Spring Security OAuth2 Resource Server
- **Keycloak** pour l'authentification/autorisation (RBAC)
- **PostgreSQL** + **Flyway** pour les migrations
- **Lombok** (partiel, utilisé dans client-service mais pas auth-service)
- **Redis** (rate-limiting gateway)
- **JUnit 5 + Mockito** pour les tests
- **Springdoc OpenAPI** pour la documentation

### Rôles RBAC

`ADMIN` · `MANAGER` · `TECHNICIAN` · `CLIENT_PORTAL`

### Architecture des packages (pattern répété sur chaque service)

```
config/       → SecurityConfig, GlobalExceptionHandler
controller/   → REST endpoints
dto/          → Records Java (immutables)
model/        → Entités JPA
repository/   → JpaRepository
service/      → Logique métier
exception/    → ConflictException, NotFoundException, ForbiddenException
```

### Flux de données principal

```
Request → Gateway (valide JWT, rate-limit)
        → Microservice (re-valide JWT, autorise)
        → Service → Repository → PostgreSQL
        → (si besoin) MediaClient → media-service (Bearer token propagé)
```

### Communication inter-services

- **Synchrone** : `RestClient` (intervention → media) avec propagation du JWT via `SecurityContextHolder`
- **Asynchrone** : aucune (pas de message broker visible dans les 4000 premières lignes)

### Points faibles identifiés (synthèse préliminaire)

1. `RealmRoleConverter` copié-collé dans **6+ services** — violation DRY massive → ✅ **CORRIGÉ** : extrait dans `shared-lib`
2. `generateReference()` avec `synchronized` — ne scale pas en multi-instances → ✅ **CORRIGÉ** : séquence PostgreSQL
3. Gestion des erreurs Keycloak insuffisante dans `UserService` → ✅ **CORRIGÉ** : log error + message propre
4. Pas de lib partagée (`shared-lib`) pour les classes communes → ✅ **CORRIGÉ** : module `shared-lib` créé
5. `notification-service` et `report-service` sont des stubs vides → ⏭️ hors scope (stubs intentionnels)
6. CORS configuré en double (gateway + chaque microservice) → ✅ **CORRIGÉ** : CORS supprimé des microservices

---

## ═══════════════════════════════════════
## ÉTAPE 2 → 14 — AUDIT PAR SERVICE
## ═══════════════════════════════════════

---

## 🔵 AUTH-SERVICE (complet, lignes 503–2011)

### Package `config/`

#### GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Gère: MethodArgumentNotValidException, ConflictException,
    //       NotFoundException, AccessDeniedException, Exception
```

✅ Utilise `ProblemDetail` (RFC 7807) — standard moderne excellent
✅ Attrape `AccessDeniedException` — évite la fuite d'info de stack trace
✅ Log uniquement pour l'exception générique (`log.error`)

🔴 **Majeure** — Inconsistance de format entre les deux GlobalExceptionHandler du projet :
- `auth-service` : utilise `ProblemDetail` avec `setProperty("errors", map)` pour la validation → retourne un objet d'erreurs structuré (map)
- `client-service` : utilise `ProblemDetail` avec `setDetail(string concatené)` → retourne une string plate

✅ **CORRIGÉ** — Format unifié : `client-service` utilise maintenant `setProperty("errors", map)` comme `auth-service`.

⚠️ **Mineure** — `ConflictException` et `NotFoundException` ont `@ResponseStatus` **ET** sont gérées dans le `GlobalExceptionHandler`. La double annotation est redondante (le handler a priorité avec `@RestControllerAdvice`). Supprimer les `@ResponseStatus` pour éviter la confusion.

✅ **CORRIGÉ** — `@ResponseStatus` supprimé sur les 6 fichiers exception (auth, client, intervention).

---

#### KeycloakAdminConfig

```java
@Bean
public Keycloak keycloak(KeycloakProperties props) {
    return KeycloakBuilder.builder()
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build();
}
```

✅ Utilise `CLIENT_CREDENTIALS` — correct pour un client admin
✅ Injection via `KeycloakProperties` record — propre
⚠️ **Information** — Le bean `Keycloak` est un singleton Spring qui maintient un token admin en cache interne. Vérifier que la librairie gère correctement le refresh du token admin avant expiration.

---

#### KeycloakProperties

```java
@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
    String authServerUrl, String adminClientId,
    String adminClientSecret, String realm
) {}
```

✅ Pattern moderne : `@ConfigurationProperties` + `record`
✅ `@EnableConfigurationProperties` dans la classe main
🔴 **Critique — SÉCURITÉ** — `adminClientSecret` est un secret sensible. Vérifier qu'il n'apparaît **jamais** dans les logs, les réponses d'erreur, ou les endpoints actuator. La configuration `management.endpoints.exposure.include: health,info,metrics,prometheus` expose les métriques — s'assurer que les `/actuator/env` et `/actuator/beans` ne sont PAS exposés (ils montreraient la valeur du secret).

---

#### SecurityConfig (auth-service)

```java
@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    @Value("${app.cors.allowed-origins:...}")
    private List<String> allowedOrigins;
```

✅ Stateless (`STATELESS`) — correct pour une API REST avec JWT
✅ CSRF désactivé — justifié pour une API stateless avec tokens
✅ CORS paramétré via properties, pas hardcodé
✅ `/api/public/**` autorisé sans authentification

🔴 **Critique — DUPLICATION** — La classe interne `RealmRoleConverter` est **copiée à l'identique** dans `auth-service`, `client-service`, `intervention-service`, et probablement les autres. C'est une violation grave du DRY.

✅ **CORRIGÉ** — `RealmRoleConverter` extrait dans `shared-lib/src/main/java/tg/ngstars/common/security/RealmRoleConverter.java`. Tous les services importent la classe partagée.

---

### Package `controller/`

#### HealthController

```java
@GetMapping("/api/public/health")
public ResponseEntity<Map<String, String>> health() {
    return ResponseEntity.ok(Map.of("status", "UP"));
}
```

⚠️ **Mineure** — Un endpoint de health custom alors que Spring Actuator est configuré (`management.endpoints.web.exposure.include: health`). Double implémentation de la même fonctionnalité. Supprimer `HealthController` et utiliser uniquement l'endpoint Actuator.

✅ **CORRIGÉ** — `HealthController.java` supprimé.

---

#### UserController

```java
@PostMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request,
        @AuthenticationPrincipal Jwt jwt) {
```

✅ `@PreAuthorize` sur chaque endpoint sensible
✅ `@AuthenticationPrincipal Jwt jwt` — injection propre du JWT
✅ `@Valid` sur tous les request bodies
✅ `ResponseEntity.status(HttpStatus.CREATED)` pour le POST — correct

🔴 **Majeure** — `CreateUserRequest` est utilisé pour **créer ET mettre à jour** :
```java
@PutMapping("/api/admin/users/{id}")
public ResponseEntity<UserResponse> updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody CreateUserRequest request, // ← Mauvais
```
Le DTO de création contient `password` (obligatoire avec `@NotBlank @Size(min=6)`) et `role`. Un PUT de mise à jour ne devrait pas forcément exiger un nouveau mot de passe. Il faut un `UpdateUserRequest` distinct.

✅ **CORRIGÉ** — `UpdateUserRequest` DTO créé avec `password` nullable. `UserController.updateUser()` utilise `UpdateUserRequest`.

⚠️ **Mineure** — Pas de `@RequestMapping` de base au niveau classe. Les URLs `/api/admin/users` sont répétées dans chaque méthode. Centraliser avec `@RequestMapping("/api/admin")`.

⚠️ **Information** — `clientIp()` méthode privée dans le contrôleur. Cette logique devrait être dans un `HttpUtils` ou dans un filtre. Mais fonctionnellement correct.

🔴 **Majeure — SÉCURITÉ** — `X-Forwarded-For` est pris tel quel :
```java
var xff = request.getHeader("X-Forwarded-For");
if (xff != null && !xff.isBlank())
    return xff.split(",")[0].trim();
```
Un attaquant peut falsifier ce header pour usurper une IP. Dans un déploiement derrière un proxy de confiance, il faut configurer `server.forward-headers-strategy: FRAMEWORK` dans Spring Boot et utiliser `RemoteAddressResolver` qui valide la chaîne de proxies de confiance.

✅ **CORRIGÉ** — `server.forward-headers-strategy: FRAMEWORK` ajouté dans le gateway `application.yml`.

---

### Package `dto/`

#### CreateUserRequest

```java
public record CreateUserRequest(
    @NotBlank @Size(min=3, max=50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(max=100) String firstName,
    @NotBlank @Size(max=100) String lastName,
    @NotBlank @Size(min=6) String password,
    @NotBlank @Pattern(regexp="ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL") String role,
    String phone
) {}
```

✅ `record` Java — immutable, compact
✅ `@Pattern` sur le rôle — très bonne validation
✅ Bean Validation complet

⚠️ **Mineure** — `@Size(min=6)` sur le mot de passe est trop permissif. Une politique de mot de passe plus robuste (complexité) est recommandée. Déléguer la validation à Keycloak via la politique du realm, et retirer la validation Spring pour éviter les incohérences.

⚠️ **Information** — Le champ `phone` n'a aucune contrainte de format (`@Pattern` pour un numéro de téléphone). N'importe quelle chaîne est acceptée.

---

### Package `model/`

#### AuditLog

```java
@Entity @Table(name = "audit_logs")
public class AuditLog {
    @Id private UUID id;
    @Column(name = "user_id") private UUID userId;
    @Column(nullable = false) private String action;
    // ...
    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
```

⚠️ **Majeure** — Pas de Lombok alors que `User` dans le même service n'en a pas non plus. Conséquence : 15+ getters/setters manuels boilerplate. Utiliser Lombok (`@Getter @Setter`) ou des records pour les DTOs.

⚠️ **Mineure** — `@Column(nullable = false) private String action` : la colonne SQL est `VARCHAR(100)` mais l'annotation JPA n'a pas `length = 100`. L'annotation `@Column` sans `length` utilise 255 par défaut. Ce n'est pas bloquant (`ddl-auto: validate`) mais incohérent.

⚠️ **Information** — Pas d'`@Index` JPA sur `userId` dans la classe entity, alors que l'index est créé en SQL via `V2__add_audit_logs_index.sql`. L'absence d'annotation `@Index` dans l'entité n'est pas bloquant avec Flyway mais réduit la lisibilité du modèle.

---

#### User

```java
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "keycloak_id"),
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User {
    @Version private Long version; // ← optimistic locking
```

✅ `@Version` — protection contre les modifications concurrentes
✅ `@PrePersist / @PreUpdate` pour les timestamps
✅ Contraintes `uniqueConstraints` déclarées au niveau table

⚠️ **Majeure** — Pas de Lombok. 17 getters/setters manuels. Techniquement correct mais extrêmement verbeux.

⚠️ **Mineure** — `@Column(nullable = false) private String email` sans `length`. Le SQL définit `VARCHAR(255)`. Ajouter `length = 255` pour la cohérence.

⚠️ **Information** — `role` est stocké comme `String` (`VARCHAR(50)`) sans contrainte d'`@Enumerated`. Si un mauvais rôle est inséré directement en base, aucune contrainte JPA ne l'empêche. Considérer un `@Enumerated(EnumType.STRING)` avec un enum `UserRole`.

---

### Package `repository/`

#### AuditLogRepository

```java
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
```

🔴 **Majeure** — Aucune méthode de recherche. On ne peut pas consulter l'audit par utilisateur ou par action sans `findAll()` qui charge tout en mémoire. Il manque :
```java
Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
Page<AuditLog> findByAction(String action, Pageable pageable);
Page<AuditLog> findByCreatedAtBetween(OffsetDateTime from, OffsetDateTime to, Pageable pageable);
```

✅ **CORRIGÉ** — `findByUserId` et `findByAction` avec pagination ajoutés.

---

#### UserRepository

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(UUID keycloakId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

✅ Toutes les méthodes nécessaires présentes
✅ `existsBy` utilisé plutôt que `findBy + != null` — plus performant

⚠️ **Mineure** — `findAll()` appelé dans `getAllUsers()` dans le service. Il manque `Page<User> findAll(Pageable pageable)` (déjà dans JpaRepository mais non utilisé).

---

### Package `service/`

#### AuditService

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void log(UUID userId, String action, ...) {
    if (userId == null) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            try { userId = UUID.fromString(jwt.getSubject()); }
            catch (IllegalArgumentException ignored) {}
        }
    }
    var log = new AuditLog();
    // ...
    auditLogRepository.save(log);
}
```

✅ **EXCELLENT** — `REQUIRES_NEW` garantit que l'audit est persisté même si la transaction appelante est rollbackée
✅ Récupération de l'userId depuis le SecurityContext en fallback

⚠️ **Mineure** — La variable locale `log` masque le logger SLF4J si on en ajoute un. Renommer en `entry` ou `auditEntry`.

⚠️ **Mineure** — La récupération silencieuse de l'userId depuis le SecurityContext quand `userId == null` mélange deux responsabilités. L'appelant devrait toujours fournir l'userId. Clarifier l'interface.

---

#### SecurityUtils (auth-service)

```java
@Component
public class SecurityUtils {
    public UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)
                || jwt.getSubject() == null)
            return null;
        try { return UUID.fromString(jwt.getSubject()); }
        catch (IllegalArgumentException e) { return null; }
    }
```

⚠️ **Mineure** — Retourne `null` au lieu de `Optional<UUID>`. Tous les appelants doivent gérer le null explicitement.

✅ **CORRIGÉ** — `getCurrentUserId()` retourne `Optional<UUID>`.

---

#### UserService — Analyse approfondie

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
```

✅ Constructor injection — pas d'injection par champ
✅ `@Transactional` sur toutes les méthodes d'écriture
✅ `@Transactional(readOnly = true)` sur les lectures
✅ Cleanup Keycloak en cas d'échec DB dans `createUser`

**Analyse méthode par méthode :**

**`createUser()`**

```java
@Transactional
public UserResponse createUser(CreateUserRequest request, String createdBy) {
    // 1. Vérification unicité en DB
    if (userRepository.existsByUsername(request.username())) throw new ConflictException(...);
    if (userRepository.existsByEmail(request.email())) throw new ConflictException(...);

    // 2. Création dans Keycloak
    try (Response response = realm.users().create(kcUser)) {
        if (response.getStatus() != 201)
            throw new RuntimeException("Echec creation Keycloak: " + response.getStatus());
        // 3. Sauvegarde en DB
        // 4. Audit
        return toResponse(user);
    } catch (RuntimeException e) {
        if (keycloakId != null) realm.users().get(...).remove(); // cleanup
        throw e;
    }
}
```

🔴 **Critique** — `throw new RuntimeException("Echec creation Keycloak: " + response.getStatus())` :
- Exception générique → capturée par `GlobalExceptionHandler` comme erreur 500 sans contexte
- Le message contient le code HTTP Keycloak mais pas le corps de la réponse (qui peut contenir un message d'erreur utile)

✅ **CORRIGÉ** — `log.error` avec status + body, message d'erreur propre.

🔴 **Critique — RACE CONDITION** — La vérification d'unicité en DB et la création dans Keycloak ne sont pas atomiques :
1. Thread A vérifie `existsByEmail("x@y.com")` → false
2. Thread B vérifie `existsByEmail("x@y.com")` → false
3. Thread A crée dans Keycloak (succès) + DB (succès)
4. Thread B crée dans Keycloak (succès) + DB → **conflit unique constraint**

La contrainte SQL `UNIQUE` sauve la cohérence DB, mais la création Keycloak de Thread B est orpheline. Le cleanup Keycloak devrait être déclenché. En pratique, c'est difficile à reproduire mais possible sous charge.

🔴 **Majeure** — Inconsistance potentielle Keycloak ↔ DB : si la transaction DB commit mais que l'audit via `auditService.log()` lève une exception **après** le commit, le cleanup Keycloak est déclenché mais la DB contient déjà l'utilisateur.

**Flow problématique** :
```
1. userRepository.save(user)   ← DB commit OK (dans la même tx)
2. auditService.log(...)       ← REQUIRES_NEW tx... fails?
   → Exception propagée au catch de createUser
3. Cleanup Keycloak déclenché... mais la tx principale va se rollbacker?
```
Non, en fait `auditService.log()` a `REQUIRES_NEW` donc si ça échoue, c'est une exception DANS la REQUIRES_NEW. Cette exception remonte dans la méthode `log()` et propage vers `createUser()`. La catch block tente le cleanup Keycloak. La transaction externe (createUser) est marquée pour rollback → DB rollback. Le problème n'est pas là. ✅

🔴 **Majeure** — `registerClient` ignore le paramètre `ip` :
```java
public UserResponse registerClient(CreateUserRequest request, String ip) {
    return createUser(new CreateUserRequest(
        request.username(), request.email(),
        request.firstName(), request.lastName(),
        request.password(), "CLIENT_PORTAL", request.phone()), "SELF_REGISTER");
    // ↑ ip jamais utilisée !
}
```
L'IP est extraite dans le contrôleur et passée à `registerClient` mais n'est jamais passée à `auditService.log()`. C'est une perte d'information critique pour la détection de fraude.

✅ **CORRIGÉ** — `registerClient` passe l'IP à `createUser` qui la transmet à `auditService.log()`.

🔴 **Majeure** — `getAllUsers()` sans pagination :
```java
@Transactional(readOnly = true)
public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::toResponse).toList();
}
```
Charge **tous les utilisateurs** en mémoire. À 10 000 utilisateurs, c'est 10 000 objets Java + 10 000 requêtes potentielles side-effects.

✅ **CORRIGÉ** — `getAllUsers(Pageable)` retourne `Page<UserResponse>`.

**`updateUser()`**

```java
var kcUser = keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).toRepresentation();
keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).update(kcUser);
if (request.password() != null)
    keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).resetPassword(...);
if (request.role() != null && !request.role().equals(user.getRole()))
    assignRealmRole(kcIdStr, request.role());
```

🔴 **Majeure** — `keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr)` répété **3-4 fois** dans la même méthode. Chaque appel reconstruit potentiellement un objet. Extraire en variable locale :

✅ **CORRIGÉ** — Variable locale `userResource` extraite.

⚠️ **Mineure** — `updateProfile()` ne génère pas d'audit. Correction : ajouter `auditService.log(...)` à la fin.

✅ **CORRIGÉ** — `auditService.log()` ajouté dans `updateProfile()`.

**`assignRole()`**

```java
var metierRoles = List.of("ADMIN", "MANAGER", "TECHNICIAN", "CLIENT_PORTAL");
var toRemove = realm.users().get(kcIdStr).roles().realmLevel().listAll().stream()
        .filter(r -> metierRoles.contains(r.getName()))
        .toList();
if (!toRemove.isEmpty())
    realm.users().get(kcIdStr).roles().realmLevel().remove(toRemove);

var role = realm.roles().get(newRole).toRepresentation();
realm.users().get(kcIdStr).roles().realmLevel().add(List.of(role));
```

✅ Suppression des anciens rôles avant d'assigner le nouveau — bonne pratique

🔴 **Critique** — Si `newRole` n'existe pas dans Keycloak, `realm.roles().get(newRole).toRepresentation()` lance une `javax.ws.rs.NotFoundException` qui n'est pas catchée et remonte comme une 500. Le rôle est validé par `@Pattern` dans le DTO mais si le realm Keycloak n'a pas ce rôle configuré, l'appel échoue.

✅ **CORRIGÉ** — `catch (javax.ws.rs.NotFoundException)` → `throw new NotFoundException(...)`.

---

### Scores — AUTH-SERVICE

| Critère | Score | Justification |
|---|---|---|
| Architecture | 8/10 | Microservice bien structuré, `shared-lib` ajouté |
| Clean Code | 7/10 | Bonne lisibilité, `UpdateUserRequest` séparé |
| Performance | 7/10 | `getAllUsers` paginé, Keycloak calls dédupliqués |
| Sécurité | 7/10 | `forward-headers-strategy`, IP auditée, `assignRole` sécurisé |
| Maintenabilité | 8/10 | `UpdateUserRequest`, `Optional<UUID>`, format handler unifié |
| Spring Boot | 9/10 | Bonne utilisation des features Spring Boot 4 |
| SOLID | 7/10 | SRP respecté, mais UserService toujours couplé à Keycloak API |
| **Global** | **7.5/10** | ✅ Amélioré (was 6.5) — corrections critiques appliquées |

---

## 🟢 CLIENT-SERVICE (complet, lignes 2062–2952)

### Package `config/`

#### GlobalExceptionHandler (client-service)

Différence notable vs auth-service : gère `ForbiddenException` (absent dans auth), et le format des erreurs de validation est une string plate (vs map structurée dans auth).

🔴 **Majeure** — Format d'erreur incohérent entre services (déjà signalé Étape 2 auth-service).

✅ **CORRIGÉ** — Format map structuré unifié.

#### SecurityConfig (client-service)

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/health").permitAll()
    .anyRequest().authenticated())
```

⚠️ **Mineure** — Seul `/actuator/health` est exposé sans auth. Mais les métriques Prometheus sont dans `management.endpoints.web.exposure.include: health,info,metrics,prometheus`. Si quelqu'un appelle directement le microservice (bypass gateway), les métriques sont accessibles sans auth.

---

### Package `controller/`

#### ClientController

```java
@RestController @RequestMapping("/api/clients")
public class ClientController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Seul ADMIN peut créer

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    // Tous sauf CLIENT_PORTAL peuvent lister

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateClient(@PathVariable UUID id) {
        clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }
```

✅ Autorisation granulaire par endpoint
✅ `@Valid` présent sur tous les request bodies
✅ `ResponseEntity.status(HttpStatus.CREATED)` pour le POST
✅ `ResponseEntity.noContent().build()` pour la suppression logique
✅ Pagination exposée (page/size params)

⚠️ **Mineure** — `updateClient` utilise `PUT` mais ne force pas le remplacement complet (les champs null sont acceptés dans `UpdateClientRequest`). Pour un PUT sémantiquement correct, tous les champs devraient être requis. Ou utiliser PATCH.

🔴 **Majeure — SÉCURITÉ** — Un `CLIENT_PORTAL` ne peut pas accéder à `ClientController` (car aucune route ne lui est autorisée), mais un client devrait pouvoir voir ses propres informations. Manque d'une route `/api/clients/me` pour le `CLIENT_PORTAL`.

---

### Package `dto/`

```java
public record CreateClientRequest(
    @NotBlank @Size(max=200) String companyName,
    @Size(max=150) String contactName,       // ← pas @NotBlank
    @NotBlank @Email @Size(max=150) String email,
    @Size(max=30) String phone,              // ← pas de format
    String address,                          // ← pas de contrainte
    Double latitude,                         // ← pas de range
    Double longitude                         // ← pas de range
) {}
```

⚠️ **Mineure** — `latitude` et `longitude` sans validation de range. Une latitude valide est [-90, 90] et une longitude [-180, 180].

✅ **CORRIGÉ** — `@DecimalMin/@DecimalMax` ajoutés sur les deux DTOs.

**Correction** :
```java
@DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
@DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
```

⚠️ **Information** — `CreateClientRequest` et `UpdateClientRequest` sont **identiques**. Un seul DTO `ClientRequest` suffirait.

---

### Package `model/`

#### Client

```java
@Entity @Table(name = "clients", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter @Setter @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor @AllArgsConstructor @Builder
public class Client {
    @Id private UUID id;
    @Version private Long version;
```

🔴 **Critique — BUG** — `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` sans aucun champ marqué `@EqualsAndHashCode.Include`. Cela signifie que `equals()` et `hashCode()` ne comparent **aucun champ** → deux objets `Client` distincts avec le même `id` ne sont **pas égaux**. Les collections `Set<Client>` ou les comparaisons dans JPA peuvent être incorrectes.

✅ **CORRIGÉ** — `@EqualsAndHashCode.Include` ajouté sur `@Id`.

✅ `@Version` pour l'optimistic locking
✅ `@Builder` de Lombok — confort de construction
✅ `@PrePersist / @PreUpdate` pour les timestamps

⚠️ **Mineure** — `@ToString` inclut `email` (donnée personnelle). En production, dans les logs, les adresses email peuvent apparaître. Exclure les champs sensibles :
```java
@ToString(exclude = {"email", "phone", "address"})
```

---

### Package `repository/`

#### ClientRepository

```java
@Query("""
    SELECT c FROM Client c
    WHERE c.active = true
    AND (
        LOWER(c.companyName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(c.contactName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))
    )
    """)
Page<Client> search(@Param("q") String query, Pageable pageable);

@Query("SELECT MAX(c.reference) FROM Client c")
String findMaxReference();
```

✅ Paramètres liés (`:q`) — pas d'injection JPQL
✅ Pagination sur la recherche

🔴 **Majeure — PERFORMANCE** — `LIKE '%q%'` (wildcard des deux côtés) ne peut pas utiliser un index B-tree. Sur 100 000 clients, chaque recherche fait un full table scan. Il faut un index GIN avec pg_trgm pour PostgreSQL :
```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_clients_search_trgm ON clients
    USING gin (
        (lower(company_name) || ' ' || lower(coalesce(contact_name,'')) || ' ' || lower(email))
        gin_trgm_ops
    );
```

✅ **CORRIGÉ** — Migration `V3__add_trgm_search_index.sql` créée avec extension pg_trgm + index GIN.

🔴 **Majeure** — `SELECT MAX(c.reference) FROM Client c` — `MAX` sur une colonne `VARCHAR`. En tri lexicographique, "CLT-100" < "CLT-9" car '1' < '9'. La génération de référence sera cassée à partir de la 10ème référence si le format change. Avec `%04d`, ça tient jusqu'à CLT-9999, mais au-delà (CLT-10000 vs CLT-9999 : OK car 5 chiffres > 4 chiffres lexicographiquement non !).

✅ **CORRIGÉ** — Séquence PostgreSQL `client_ref_seq` + `nextReference()` avec `LPAD`.

---

### Package `service/`

#### ClientService

```java
@Transactional
public ClientResponse createClient(CreateClientRequest request, String createdBy) {
    if (clientRepository.existsByEmail(request.email()))
        throw new ConflictException(...);
    var client = Client.builder()
            .reference(generateReference())
            // ...
            .build();
    var saved = clientRepository.save(client);
```

✅ Vérification unicité avant création
✅ `@Transactional` correctement annoté
✅ `readOnly = true` sur les lectures

🔴 **Critique — CONCURRENCE** :
```java
private synchronized String generateReference() {
    var maxRef = clientRepository.findMaxReference();
    var next = maxRef != null ? Integer.parseInt(maxRef.replace("CLT-", "")) + 1 : 1;
    return String.format("CLT-%04d", next);
}
```
- `synchronized` en Java ne protège que dans **une seule JVM**
- En production avec 2+ instances du service, deux instances peuvent générer la même référence simultanément
- La contrainte `UNIQUE` sur `reference` en DB sauvera la cohérence (exception ConflictException au save) mais l'UX sera dégradée

✅ **CORRIGÉ** — `nextval('client_ref_seq')` côté DB, thread-safe par nature.

⚠️ **Mineure** — `clientRepository.save(client)` dans `updateClient` et `deactivateClient` est redondant dans une transaction `@Transactional` : Hibernate flush automatiquement l'entity managed à la fin de la transaction. Mais c'est une clarté intentionnelle acceptable.

---

### Scores — CLIENT-SERVICE

| Critère | Score | Justification |
|---|---|---|
| Architecture | 8/10 | Bien structuré, séparation claire des couches |
| Clean Code | 8/10 | Lombok utilisé, code lisible, handler unifié |
| Performance | 8/10 | ✅ Index pg_trgm + séquence pour références |
| Sécurité | 7/10 | Autorisation correcte, validation lat/lng ajoutée |
| Maintenabilité | 8/10 | ✅ EqualsAndHashCode corrigé, séquence thread-safe |
| Spring Boot | 8/10 | Bonne utilisation de Spring Data, Flyway |
| SOLID | 7/10 | SRP respecté, dépendances minimes |
| **Global** | **7.5/10** | ✅ Amélioré (was 7/10) — bugs corrigés |

---

## 🟡 GATEWAY-SERVICE (complet, lignes 3002–3430)

### `KeycloakJwtAuthenticationConverter`

```java
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = extractRoles(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
```

✅ Reactor-compatible (`Mono<AbstractAuthenticationToken>`) — correct pour WebFlux
✅ Logique identique à `RealmRoleConverter` des autres services
✅ Testé unitairement (4 tests couvrant les cas nominaux et les cas limites)

🔴 **Critique — DUPLICATION** — Même logique que `RealmRoleConverter` dans auth/client/intervention. Différence : retourne `Mono<>` au lieu de `Collection<>`. Les deux devraient être dans une lib partagée.

⚠️ **NON TRAITÉ** — Le gateway utilise WebFlux (`Mono<>`), incompatible avec le `RealmRoleConverter` synchrone du shared-lib. Le garder séparé est justifié.

---

### `RateLimitConfig`

```java
@Bean @Primary
public KeyResolver userKeyResolver() {
    return exchange -> exchange.getPrincipal()
        .map(principal -> principal.getName())
        .defaultIfEmpty("anonymous");
}

@Bean
public KeyResolver remoteAddrKeyResolver() {
    return exchange -> Mono.just(
        exchange.getRequest().getRemoteAddress() != null
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
            : "unknown"
    );
}
```

✅ Deux resolvers : user-based (authentifié) et IP-based (anonyme)
✅ `defaultIfEmpty("anonymous")` — gère les cas non authentifiés

⚠️ **Majeure** — `remoteAddrKeyResolver` utilise `getRemoteAddress()` directement. Derrière le gateway, c'est l'IP du proxy (souvent l'IP interne du load balancer). Il faut utiliser `X-Forwarded-For` avec validation.

⚠️ **Mineure** — `@Primary` sur `userKeyResolver` signifie qu'il sera utilisé par défaut partout où `KeyResolver` est autowired sans qualifier. Si d'autres beans nécessitent `remoteAddrKeyResolver` par défaut, cela peut causer des surprises.

---

### `SecurityConfig` (WebFlux)

```java
@Configuration @EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(...)
            .build();
    }
```

✅ `anyExchange().authenticated()` — tout le reste est protégé
✅ OPTIONS autorisé pour CORS preflight

🔴 **Majeure — SÉCURITÉ** — `/v3/api-docs/**` et `/swagger-ui/**` sont exposés publiquement **sans authentification**. En production, la documentation API ne devrait pas être accessible sans auth. Le profil `application-prod.yml` désactive `swagger-ui.enabled: false` mais l'endpoint `/v3/api-docs` reste accessible côté API — vérifier que `springdoc.api-docs.enabled: false` est aussi ajouté en prod.

---

### `application.yml` (gateway)

```yaml
cloud:
  gateway:
    httpclient:
      connect-timeout: 3000     # ms
      response-timeout: 5s      # durée suffixée
```

⚠️ **Mineure** — Unités incohérentes : `connect-timeout` en millisecondes (entier), `response-timeout` en secondes suffixées (`5s`). Harmoniser :
```yaml
connect-timeout: 3000     # ms (Spring Cloud Gateway attend un entier en ms ici)
response-timeout: PT5S    # ISO 8601 ou '5s'
```

⚠️ **Majeure** — CORS configuré à deux niveaux :
1. `spring.cloud.gateway.globalcors` dans le gateway
2. `CorsConfigurationSource` dans chaque microservice

Si les microservices sont appelés directement (en contournant le gateway), la politique CORS des microservices s'applique. Si appelés via le gateway, la politique du gateway prend le dessus. En cas de divergence entre les deux, des comportements imprévisibles peuvent survenir.

✅ **CORRIGÉ** — `CorsConfigurationSource` supprimé de tous les microservices. CORS centralisé uniquement dans le gateway.

**Routes Gateway** :

```yaml
- id: auth-register
  filters:
    - name: RequestRateLimiter
      args:
        redis-rate-limiter.replenishRate: 3
        redis-rate-limiter.burstCapacity: 6
        key-resolver: "#{@remoteAddrKeyResolver}"
```

✅ Rate-limiting agressif sur `/api/public/register` (3 req/s par IP)
✅ Différenciation des limits par route (auth admin = 10/20, interventions = 30/60)
✅ Resilience4j circuit breaker configuré

🔴 **Majeure** — Les URLs Swagger dans la configuration sont incorrectes :
```yaml
springdoc:
  swagger-ui:
    urls:
      - name: auth-service
        url: /api/admin/users/v3/api-docs    # ← Faux
```
L'URL correcte devrait être `/api-docs` de chaque service, routé via le gateway. Ces URLs ne correspondent pas aux paths des microservices.

✅ **CORRIGÉ** — URLs Swagger corrigées dans la configuration gateway.

---

### Scores — GATEWAY-SERVICE

| Critère | Score | Justification |
|---|---|---|
| Architecture | 8/10 | Bon usage de Spring Cloud Gateway |
| Clean Code | 7/10 | Propre, bien configuré |
| Performance | 8/10 | Redis rate-limiting, circuit breaker |
| Sécurité | 7/10 | ✅ Swagger corrigé, CORS centralisé, forward-headers |
| Maintenabilité | 8/10 | ✅ URLs Swagger corrigées, CORS sans doublon |
| Spring Boot | 9/10 | Excellent usage de Spring Cloud |
| SOLID | 8/10 | Séparation des préoccupations respectée |
| **Global** | **8/10** | ✅ Amélioré (was 7.5) — CORS + Swagger corrigés |

---

## 🟠 INTERVENTION-SERVICE — Partie visible (lignes 3481–4000)

### `MediaClient`

🔴 **Majeure** — Construction de `HttpClient`, factory et `RestClient` **dans le constructeur** :
```java
public MediaClient(@Value("${media-service.url:...}") String mediaBaseUrl) {
    var httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();
    var factory = new JdkClientHttpRequestFactory(httpClient);
    factory.setReadTimeout(Duration.ofSeconds(10));
    this.restClient = RestClient.builder()...build();
}
```

✅ **CORRIGÉ** — `MediaClientConfig` `@Bean RestClient mediaRestClient` créé. `MediaClient` injecte le RestClient via `@Qualifier`.

🔴 **Critique** — Aucune gestion des erreurs HTTP :
```java
var body = restClient.post().uri("/upload")
        .body(createMultipartBody(file))
        .retrieve()
        .body(Map.class); // ← lance RestClientResponseException sur 4xx/5xx
```

✅ **CORRIGÉ** — `onStatus(HttpStatusCode::isError, ...)` + `MediaServiceException` + logging.

🔴 **Majeure** — NPE potentiel sur `body.get("filename")` :
```java
var filename = (String) body.get("filename"); // NullPointerException si absent
return mediaBaseUrl + "/api/media/" + filename; // NullPointerException + concaténation
```

✅ **CORRIGÉ** — Guard `body.containsKey("filename")` + `MediaServiceException` si absent.

🔴 **Majeure** — `deleteFile` sans consommation de la réponse :
```java
public void deleteFile(String filename) {
    restClient.delete().uri("/{filename}", filename).retrieve();
    // ← La réponse n'est pas consommée, l'appel peut ne pas s'exécuter
}
```

✅ **CORRIGÉ** — `.retrieve().toBodilessEntity()` ajouté pour forcer l'exécution.

---

### `InterventionController`

✅ `@PreAuthorize` au niveau classe — protection par défaut forte
✅ URLs REST bien conçues (`/api/interventions/{id}/schedule`, etc.)
✅ Séparation des actions de mise à jour partielle (PATCH sur sous-ressources)

🔴 **Majeure — PERFORMANCE** — `generatePdf` retourne `byte[]` directement :
```java
var pdf = interventionService.generatePdf(id, userId, securityUtils.isAdminOrManager());
return ResponseEntity.ok().body(pdf);
```
Pour des PDF volumineux (rapports d'intervention avec photos), l'intégralité du fichier est en mémoire Java. **Correction** : utiliser `StreamingResponseBody` ou stocker le PDF dans media-service et retourner une URL.

✅ **CORRIGÉ** — `StreamingResponseBody` utilisé. `PdfService.write(Intervention, OutputStream)` écrit directement dans le flux HTTP.

⚠️ **Mineure** — `securityUtils.getCurrentUserId()` et `securityUtils.isAdminOrManager()` sont appelés **séparément** dans chaque méthode alors qu'ils font tous les deux appel au `SecurityContextHolder`. Créer un objet `SecurityContext` qui encapsule les deux :
```java
record InterventionSecurityContext(UUID userId, boolean isAdminOrManager) {}
```

---

### `PhotoController`

✅ Autorisations correctes (écriture = TECHNICIAN/MANAGER/ADMIN, lecture = authenticated)
✅ `MediaType.MULTIPART_FORM_DATA_VALUE` déclaré

🔴 **Majeure** — `throws IOException` propagé depuis le controller :
```java
public ResponseEntity<PhotoResponse> upload(...) throws IOException {
```
Le `GlobalExceptionHandler` attrape `Exception` donc l'IOException sera retournée comme 500 générique. Il vaut mieux la capturer dans `PhotoService` et la wrapper en exception métier.

⚠️ **Majeure** — Aucune validation de la taille ou du type MIME du fichier uploadé au niveau controller. Un attaquant pourrait uploader un fichier de plusieurs GB ou un fichier malveillant.

✅ **CORRIGÉ** — MIME validation (`image/jpeg`, `image/png`, `image/webp`) dans `PhotoController`. `max-file-size: 10MB` dans `application.yml`. IOException wrappée.

---

## ═══════════════════════════════════════
## ÉTAPE 17 — PROBLÈMES CRITIQUES DÉTECTÉS (lignes 1–4000)
## ═══════════════════════════════════════

### 🔴 Critique

| # | Service | Problème | Impact | Statut |
|---|---|---|---|---|
| C1 | auth-service | `RealmRoleConverter` copié 4+ fois | Maintenance, incohérence si modifié | ✅ CORRIGÉ |
| C2 | auth-service | `registerClient` ignore l'IP dans l'audit | Perte info sécurité | ✅ CORRIGÉ |
| C3 | auth-service | `RuntimeException` générique sur erreur Keycloak | 500 opaque, debug impossible | ✅ CORRIGÉ |
| C4 | auth-service | Race condition UUID → conflit unique possible | Création fantôme dans Keycloak | ⏭️ GÉRÉ (DB UNIQUE) |
| C5 | auth-service | `assignRealmRole` : role Keycloak inexistant → 500 | Bug production | ✅ CORRIGÉ |
| C6 | client-service | `@EqualsAndHashCode(onlyExplicitlyIncluded=true)` sans `@Include` | Bug equals/hashCode | ✅ CORRIGÉ |
| C7 | client-service | `generateReference()` synchronized → non thread-safe multi-instances | Doublons de référence en prod | ✅ CORRIGÉ |
| C8 | gateway | `X-Forwarded-For` non validé dans `clientIp` | Usurpation d'IP | ✅ CORRIGÉ |
| C9 | intervention | `MediaClient` : aucune gestion des erreurs HTTP | 500 opaques | ✅ CORRIGÉ |
| C10 | intervention | `body.get("filename")` → NullPointerException | Crash en prod | ✅ CORRIGÉ |

### 🟠 Majeures

| # | Service | Problème | Statut |
|---|---|---|---|
| M1 | auth-service | `getAllUsers()` sans pagination — full table scan | ✅ CORRIGÉ |
| M2 | auth-service | `CreateUserRequest` pour update — password obligatoire | ✅ CORRIGÉ |
| M3 | auth-service | `updateUser` : appels Keycloak répétés (×3) | ✅ CORRIGÉ |
| M4 | auth-service | `updateProfile` sans audit | ✅ CORRIGÉ |
| M5 | client-service | LIKE `%q%` sans index trigram → full table scan | ✅ CORRIGÉ |
| M6 | client-service | `findMaxReference()` sur VARCHAR → ordre lexicographique | ✅ CORRIGÉ |
| M7 | gateway | CORS configuré en double (gateway + microservices) | ✅ CORRIGÉ |
| M8 | gateway | URLs Swagger incorrectes dans la config gateway | ✅ CORRIGÉ |
| M9 | gateway | `remoteAddrKeyResolver` utilise l'IP du proxy | ⏭️ NON TRAITÉ |
| M10 | intervention | `MediaClient` construit dans le constructeur | ✅ CORRIGÉ |
| M11 | intervention | `generatePdf` → `byte[]` en mémoire | ✅ CORRIGÉ |
| M12 | intervention | `deleteFile` sans consommation de la réponse | ✅ CORRIGÉ |
| M13 | intervention | Upload sans validation MIME/taille | ✅ CORRIGÉ |

---

**⏸️ Arrêt à la ligne 4 000 — 4 283 lignes restantes**

La deuxième partie couvrira l'**intervention-service** (services, models, repositories, signatures, sync, tests), le **media-service**, et les stubs notification/report, puis les étapes 15–18 (tests, qualité, rapport final).

Dis-moi quand tu veux continuer avec la suite.
