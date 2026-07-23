# API Endpoints — NG-Fields Backend

**Mis à jour :** 23/07/2026 (Backend Complet)
**Total :** 90 endpoints

---

## Gateway (8080)

All external requests go through the gateway.

---

## Auth Service (8081)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/public/register | PUBLIC | Auto-inscription (force CLIENT_USER) |
| GET | /api/public/health | PUBLIC | Health check |
| GET | /api/users/me | ANY | Get profile |
| PUT | /api/users/me | ANY | Update profile |
| PUT | /api/client/change-password | ANY | Change password (first login) |
| POST | /api/admin/users | ADMIN | Create user |
| GET | /api/admin/users | ADMIN | List users (paginé) |
| GET | /api/admin/users/{id} | ADMIN | Get user |
| PUT | /api/admin/users/{id} | ADMIN | Update user |
| DELETE | /api/admin/users/{id} | ADMIN | Soft delete user |
| PATCH | /api/admin/users/{keycloakId}/roles | ADMIN | Assign role |
| PATCH | /api/admin/users/{keycloakId}/status | ADMIN | Enable/disable |
| POST | /api/admin/users/{keycloakId}/reset-password | ADMIN | Reset password |
| POST | /api/admin/companies | ADMIN/MANAGER | Register company |
| GET | /api/admin/companies | ADMIN/MANAGER | List companies |
| GET | /api/admin/companies/{id} | ADMIN/MANAGER | Get company |
| PUT | /api/admin/companies/{id} | ADMIN | Update company |
| DELETE | /api/admin/companies/{id} | ADMIN | Soft delete company |
| POST | /api/client/users | CLIENT_ADMIN | Create user in company |
| GET | /api/client/users | CLIENT_ADMIN | List company users |
| PUT | /api/client/users/{id} | CLIENT_ADMIN | Update company user |
| DELETE | /api/client/users/{id} | CLIENT_ADMIN | Soft delete company user |
| PUT | /api/client/users/{id}/role | CLIENT_ADMIN | Change user role |
| PUT | /api/client/users/{id}/password | CLIENT_ADMIN | Reset user password |
| GET | /api/client/interventions | CLIENT | List company interventions |
| GET | /api/client/dashboard | CLIENT_ADMIN | Company KPIs |

---

## Client Service (8082)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/clients | ADMIN/MANAGER | Create client |
| GET | /api/clients | ADMIN/MANAGER/TECHNICIAN | List clients (paginé) |
| GET | /api/clients/search?q= | ADMIN/MANAGER/TECHNICIAN | Search clients (ILIKE) |
| GET | /api/clients/{id} | ADMIN/MANAGER/TECHNICIAN | Get client |
| PUT | /api/clients/{id} | ADMIN | Update client |
| DELETE | /api/clients/{id} | ADMIN | Soft delete client |

---

## Intervention Service (8083)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/interventions | TECHNICIAN | Create intervention |
| GET | /api/interventions | ANY | List interventions (filtres: status, technicianId) |
| GET | /api/interventions/{id} | ANY | Get intervention |
| PUT | /api/interventions/{id} | ANY | Update intervention |
| DELETE | /api/interventions/{id} | ANY | Soft delete |
| POST | /api/interventions/{id}/close | ANY | Close with signatures |
| GET | /api/interventions/{id}/pdf | ANY | Generate PDF |
| POST | /api/interventions/{id}/send/email | ANY | Send report by email |
| GET | /api/interventions/stats | MANAGER | Dashboard stats (total, active, pending, completed, month, today) |
| GET | /api/interventions/stats/by-technician | MANAGER | Stats by technician |
| GET | /api/interventions/stats/by-client | MANAGER | Stats by client |
| POST | /api/sync/batch | ANY | Batch sync (offline→online, last-write-wins) |
| PATCH | /api/interventions/{id}/schedule | PROPRIETARY* | Update schedule |
| PATCH | /api/interventions/{id}/equipment | PROPRIETARY* | Update equipment |
| PATCH | /api/interventions/{id}/diagnosis | PROPRIETARY* | Update diagnosis |
| PATCH | /api/interventions/{id}/result | PROPRIETARY* | Update result |
| PATCH | /api/interventions/{id}/recommendations | PROPRIETARY* | Update recommendations |
| POST | /api/interventions/{id}/items | PROPRIETARY* | Add item |
| PUT | /api/interventions/{id}/items/{itemId} | PROPRIETARY* | Update item |
| DELETE | /api/interventions/{id}/items/{itemId} | PROPRIETARY* | Remove item |
| POST | /api/interventions/{id}/assign | MANAGER | Assign technician |
| GET | /api/manager/interventions/weekly-schedule | MANAGER | Weekly schedule for technician |
| POST | /api/manager/interventions | MANAGER | Create planned intervention |
| PUT | /api/manager/interventions/{id}/assign | MANAGER | Reassign intervention |
| GET | /api/manager/events | MANAGER | SSE real-time events |
| GET | /api/manager/export/csv | MANAGER | Export CSV (filtres: status, technicianId) |
| GET | /api/manager/export/excel | MANAGER | Export Excel (filtres: status, technicianId) |
| GET | /api/manager/export/html | MANAGER | Export HTML (filtres: status, technicianId) |
| GET | /api/client/interventions | CLIENT | List company interventions (filtré company_id) |
| GET | /api/client/dashboard | CLIENT_ADMIN | Company KPIs (filtré company_id) |

*\* Propriétaire = technicien assigné, sauf ADMIN/MANAGER qui peuvent modifier toute intervention.*

---

## Media Service (8084)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/media/upload | ANY | Upload file (multipart) |
| POST | /api/media/upload-base64 | ANY | Upload base64 |
| GET | /api/media/{filename} | ANY | Download file |
| DELETE | /api/media/{filename} | ANY | Delete file |

---

## Notification Service (8085)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/notifications/email | ANY | Send email |
| POST | /api/push/register-token | ANY | Register FCM push token |
| POST | /api/push/send | ANY | Send push notification |
| GET | /api/push/health | PUBLIC | Push service health |

---

## Report Service (8086)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | /api/reports/interventions/csv | MANAGER | Export CSV |
| GET | /api/reports/interventions/pdf | MANAGER | Export PDF |
| GET | /api/reports/analytics | MANAGER | Analytics |
| GET | /api/report/pdf-templates | ADMIN | List PDF templates |
| POST | /api/report/pdf-templates | ADMIN | Create PDF template |
| PUT | /api/report/pdf-templates/{id} | ADMIN | Update PDF template |
| DELETE | /api/report/pdf-templates/{id} | ADMIN | Delete PDF template |
| GET | /api/report/email-templates | ADMIN | List email templates |
| POST | /api/report/email-templates | ADMIN | Create email template |
| PUT | /api/report/email-templates/{id} | ADMIN | Update email template |
| DELETE | /api/report/email-templates/{id} | ADMIN | Delete email template |
