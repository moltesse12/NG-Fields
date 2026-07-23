# Tests API NG-Fields (Postman)

**Mis à jour :** 23/07/2026 (Backend Complet)

## Prérequis

- **Keycloak** sur `http://localhost:8088` (realm `ng-fields`)
- **API Gateway** sur `http://localhost:8080` (routes vers tous les services)

## Installation

1. `File` → `Import`
2. Importer `Backend/postman/NG-Fields API.postman_collection.json` (90 endpoints)
3. Importer `Backend/postman/NG-Fields Dev.postman_environment.json`
4. Sélectionner l'environnement `NG-Fields Dev`

## Couverture par US

| US | Endpoints | Requêtes |
|----|-----------|----------|
| US-001 | Health check | GET /api/public/health |
| US-004 | Login (4 rôles) | POST /api/public/login |
| US-005 | Inscription publique | POST /api/public/register |
| US-006 | Profil (GET/PUT) | GET/PUT /api/users/me |
| US-007 | CRUD Users | POST, GET, PUT, DELETE /api/admin/users |
| US-009 | CRUD Clients | POST, GET, PUT, DELETE /api/clients |
| US-011 | CRUD Interventions | POST, GET, PUT, DELETE /api/interventions |
| US-012-017 | Sections intervention | PATCH schedule, equipment, diagnosis, result, recommendations |
| US-014 | Items (pièces) | POST, PUT, DELETE /api/interventions/{id}/items |
| US-015 | Photos | POST /api/media/upload |
| US-016 | Signatures | POST /api/interventions/{id}/signatures |
| US-018 | Stats + liste filtrée | GET /api/interventions/stats, GET /api/interventions?status=&technicianId= |
| US-019 | Batch sync | POST /api/sync/batch |
| US-021 | PDF | GET /api/interventions/{id}/pdf |
| US-022 | Email | POST /api/interventions/{id}/send/email |
| US-024/025 | Push notifications | POST /api/push/register-token, /api/push/send |
| US-028 | Dashboard Manager | GET /api/interventions/stats, /stats/by-technician, /stats/by-client |
| US-029 | Planning | GET /api/manager/interventions/weekly-schedule, POST assign |
| US-030 | Actuator | GET /actuator/health, /actuator/prometheus |
| US-033 | Swagger | GET /swagger-ui.html, /v3/api-docs |
| US-035 | SSE Events | GET /api/manager/events |
| US-036 | Export | GET /api/manager/export/csv, /excel, /html |
| US-037 | Companies | POST, GET, PUT, DELETE /api/admin/companies |
| US-039 | Client Users | POST, GET, PUT, DELETE /api/client/users |
| US-040 | Client Interventions | GET /api/client/interventions |
| US-043 | Client Dashboard | GET /api/client/dashboard |
| US-042 | Templates | CRUD /api/report/pdf-templates, /email-templates |

## Variables

| Variable | Valeur | Remplie par |
|----------|--------|-------------|
| `base_url` | `http://localhost:8080` | Manuel (gateway) |
| `kc_url` | `http://localhost:8088` | Manuel (Keycloak) |
| `client_secret` | *(à configurer)* | Manuel |
| `adminToken` | *(auto)* | Requête Login ADMIN |
| `managerToken` | *(auto)* | Requête Login MANAGER |
| `technicianToken` | *(auto)* | Requête Login TECHNICIEN |
| `clientAdminToken` | *(auto)* | Requête Login CLIENT_ADMIN |
| `testInterventionId` | *(auto)* | Requête création intervention |
| `plannedInterventionId` | *(auto)* | Requête création planifiée |
