# 05 — CRUD Clients (client-service)

**Objectif :** API REST pour gérer les clients (création, consultation paginée, modification, désactivation, recherche)
**Dépend de :** [04-user-registration.md](04-user-registration.md)

---

## Architecture

```
gateway:8080
  └── /api/clients/**          → client-service:8082 (rate limited)
```

---

## Endpoints

| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| POST | `/api/clients` | ADMIN | Créer un client |
| GET | `/api/clients?page=0&size=20` | ADMIN, MANAGER, TECHNICIAN | Lister (paginé) |
| GET | `/api/clients/search?q=&page=0&size=20` | ADMIN, MANAGER, TECHNICIAN | Rechercher |
| GET | `/api/clients/{id}` | ADMIN, MANAGER, TECHNICIAN | Détail |
| PUT | `/api/clients/{id}` | ADMIN | Modifier |
| DELETE | `/api/clients/{id}` | ADMIN | Désactiver (soft delete) |

---

## Modèle Client

```java
public class Client {
    @Id UUID id;
    String reference;           // CLT-XXXX (auto-généré)
    String companyName;
    String contactName;
    String email;
    String phone;
    String address;
    Double latitude;
    Double longitude;
    Boolean active;             // true par défaut
    String createdBy;           // keycloakId du créateur
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}
```

### DTOs

```java
// CreateClientRequest
public record CreateClientRequest(
    @NotBlank String companyName,
    @NotBlank @Email String email,
    @NotBlank String phone,
    String address,
    @NotBlank String contactName,
    String contactPhone,
    Double latitude,
    Double longitude
) {}

// UpdateClientRequest (mêmes champs, tous optionnels)
public record UpdateClientRequest(
    String companyName,
    String email,
    String phone,
    String address,
    String contactName,
    String contactPhone,
    Double latitude,
    Double longitude
) {}

// ClientResponse
public record ClientResponse(
    UUID id,
    String reference,
    String companyName,
    String contactName,
    String email,
    String phone,
    String address,
    Double latitude,
    Double longitude,
    Boolean active,
    String createdBy,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
```

---

## Flux

```
POST /api/clients (ADMIN)
  → ClientController.createClient(request, jwt)
    → ClientService.createClient(request, keycloakId)
      → Génération référence: "CLT-" + (count+1)
      → ClientRepository.save(client)
    → Retourne 201 + ClientResponse

GET /api/clients?page=0&size=20
  → ClientService.listClients(page, size)
    → ClientRepository.findAllActive(pageable)
    → Retourne Page<ClientResponse>

GET /api/clients/search?q=NG-STARs
  → ClientService.searchClients(q, page, size)
    → ClientRepository.search(q, pageable)
      → WHERE LOWER(c.companyName) LIKE %q%
         OR LOWER(c.contactName) LIKE %q%
         OR LOWER(c.email) LIKE %q%
    → Retourne Page<ClientResponse>

DELETE /api/clients/{id} (ADMIN)
  → ClientService.deactivateClient(id)
    → ClientRepository.findById(id) → ClientNotFoundException
    → client.setActive(false)
    → ClientRepository.save(client)
    → Retourne 204 No Content
```

---

## Référence client

Le format `CLT-XXXX` est généré côté serveur :

```java
var count = clientRepository.count();
var ref = String.format("CLT-%04d", count + 1);
```

> Ponytail: compteur thread-unsafe, suffisant en dev. Remplacer par séquence DB si nécessaire.

---

## Pagination

Le retour de `GET /api/clients` et `GET /api/clients/search` est une `Page<ClientResponse>` Spring Data :

```json
{
  "content": [ ... ],
  "totalElements": 42,
  "totalPages": 3,
  "number": 0,
  "size": 20,
  "first": true,
  "last": false
}
```

---

## RBAC

| Endpoint | ADMIN | MANAGER | TECHNICIAN |
|----------|-------|---------|------------|
| POST | ✅ | ❌ 403 | ❌ 403 |
| GET list/search | ✅ | ✅ | ✅ |
| GET detail | ✅ | ✅ | ✅ |
| PUT | ✅ | ❌ 403 | ❌ 403 |
| DELETE | ✅ | ❌ 403 | ❌ 403 |

Implémenté via `@PreAuthorize` sur chaque méthode du controller.

---

## Test avec Postman

1. Exécuter "5. Login ADMIN" dans Postman
2. Exécuter "20. Créer un client (ADMIN)" → 201 + référence CLT-XXXX
3. Exécuter "21. Lister les clients (paginé)" → page avec contenu
4. Exécuter "23. Détail d'un client" → 200
5. Exécuter "26. Créer client (CLIENT_USER — 403)" → 403

---

## ✅ Critères de succès

| Critère | Test |
|---------|------|
| ADMIN crée un client | `POST /api/clients` → 201 avec référence |
| Liste paginée | `GET /api/clients?page=0` → contient `content`, `totalElements` |
| Recherche | `GET /api/clients/search?q=term` → résultats filtrés |
| Détail | `GET /api/clients/{id}` → 200 |
| Modification | `PUT /api/clients/{id}` → champs mis à jour |
| Désactivation | `DELETE /api/clients/{id}` → 204, active = false |
| RBAC | CLIENT_USER → 403 sur POST/PUT/DELETE |

---

➡️ [06-intervention-crud.md](06-intervention-crud.md) — CRUD Interventions
