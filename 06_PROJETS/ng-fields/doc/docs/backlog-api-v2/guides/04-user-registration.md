# 04 — API Auth (auth-service)

**Objectif :** Gestion des utilisateurs (création, profil, rôles) — synchronisation Keycloak + PostgreSQL
**Dépend de :** [03-spring-security.md](03-spring-security.md)

---

## Architecture

```
gateway:8080
  └── /api/admin/users/**    → auth-service:8081 (rate limited)
  └── /api/users/me           → auth-service:8081
  └── /api/public/**          → auth-service:8081 (no auth)
```

---

## Endpoints

| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| POST | `/api/admin/users` | ADMIN | Créer un user dans Keycloak + DB |
| GET | `/api/admin/users` | ADMIN | Lister tous les users |
| GET | `/api/admin/users/{id}` | ADMIN | Détail d'un user |
| PUT | `/api/admin/users/{id}` | ADMIN | Modifier un user |
| DELETE | `/api/admin/users/{id}` | ADMIN | Supprimer un user (Keycloak + DB) |
| PATCH | `/api/admin/users/{keycloakId}/roles` | ADMIN | Assigner un rôle |
| PATCH | `/api/admin/users/{keycloakId}/status` | ADMIN | Activer/désactiver |
| POST | `/api/admin/users/{keycloakId}/reset-password` | ADMIN | Envoyer email reset |
| GET | `/api/users/me` | Tous | Profil de l'utilisateur connecté |
| PUT | `/api/users/me` | Tous | Modifier son profil |
| POST | `/api/public/register` | Anonyme | Inscription self-service (CLIENT_USER par défaut) |

---

## Modèle User

```java
public class User {
    @Id UUID id;                    // PK auto-généré
    UUID keycloakId;                // ID Keycloak (unique)
    String username;
    String email;
    String firstName;
    String lastName;
    String phone;                   // optionnel
    Boolean active;                 // = enabled dans Keycloak
    String role;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}
```

### DTOs

```java
// CreateUserRequest
public record CreateUserRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phone,
    @NotBlank @Size(min = 8) String password,
    String role                     // null → CLIENT_USER par défaut
) {}

// UserResponse
public record UserResponse(
    UUID id,
    UUID keycloakId,
    String username,
    String email,
    String firstName,
    String lastName,
    String phone,
    Boolean active,
    String role,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
```

---

## Flux de création

```
POST /api/admin/users
  → UserController (vérifie @PreAuthorize)
    → UserService.createUser(request, adminKeycloakId)
      → keycloak-admin-client → Keycloak Admin API (création user + assign rôle)
      → UserRepository.save() (copie dans PostgreSQL)
    → Retourne 201 + UserResponse

POST /api/public/register
  → UserController (pas d'auth)
    → UserService.registerClient(request, clientIp)
      → keycloak-admin-client → Keycloak Admin API (rôle forcé CLIENT_USER)
      → UserRepository.save()
    → Retourne 201 + message + UserResponse
```

---

## Fonctionnalités avancées

### Assignation de rôle
```java
PATCH /api/admin/users/{keycloakId}/roles
Body: { "role": "MANAGER" }
```
- Supprime tous les rôles existants dans Keycloak
- Assigne le nouveau rôle
- Met à jour `User.role` en DB

### Gestion de statut
```java
PATCH /api/admin/users/{keycloakId}/status
Body: { "enabled": false }
```
- Désactive/active dans Keycloak
- Met à jour `User.active` en DB

### Reset de mot de passe
```java
POST /api/admin/users/{keycloakId}/reset-password
```
- Envoie un email via `keycloak-admin-client.executeActionsEmail()`

---

## Modèle AuditLog

```java
public class AuditLog {
    @Id UUID id;
    UUID userId;           // Qui a fait l'action
    String action;         // CREATE_USER, UPDATE_ROLE, etc.
    String resource;       // USER, CLIENT, etc.
    String resourceId;     // ID de la ressource ciblée
    String details;        // JSON ou texte libre
    String ipAddress;
    OffsetDateTime createdAt;
}
```

---

## Test avec Postman

1. Importer `Backend/postman/NG-Fields API.postman_collection.json` + `NG-Fields Dev.postman_environment.json`
2. Exécuter "1. Health check" → vérifier `200 {"status": "UP"}`
3. Exécuter "5. Login ADMIN" → token stocké automatiquement
4. Exécuter "12. ADMIN: Create user" → 201
5. Modifier le body de "7. Inscription publique" si besoin → 201
6. Exécuter "9. GET /users/me" → profil du user connecté

### Test manuel direct

```powershell
# Login ADMIN
$token = curl -X POST http://localhost:8088/realms/ng-fields/protocol/openid-connect/token `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "client_id=ng-fields-backend" `
  -d "client_secret=SECRET" `
  -d "username=admin" `
  -d "password=Admin123!" `
  -d "grant_type=password" | ConvertFrom-Json
$adminToken = $token.access_token

# Créer un user
curl -X POST http://localhost:8080/api/admin/users `
  -H "Authorization: Bearer $adminToken" `
  -H "Content-Type: application/json" `
  -d '{
    "username": "test_user",
    "email": "test@ng-fields.test",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+22890000000",
    "password": "StrongPass123!",
    "role": "TECHNICIAN"
  }'
```

---

## ✅ Critères de succès

| Critère | Test |
|---------|------|
| ADMIN crée un user | `POST /api/admin/users` → 201 avec id + keycloakId |
| User existe dans Keycloak | Admin Console → Users |
| Rôle correct dans le JWT | `realm_access.roles` contient le rôle |
| Inscription publique | `POST /api/public/register` → 201, rôle CLIENT_USER |
| Profil utilisateur | `GET /api/users/me` → 200 avec firstName, lastName, phone |
| Audit log créé | Table `auth.audit_logs` contient une entrée |

---

➡️ [05-client-crud.md](05-client-crud.md) — CRUD Clients
