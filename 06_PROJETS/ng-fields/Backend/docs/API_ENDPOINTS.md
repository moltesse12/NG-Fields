# API Endpoints

## Gateway (8080)
All external requests go through the gateway.

## Auth Service (8081)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/admin/users | ADMIN | Create user |
| GET | /api/admin/users | ADMIN | List users |
| GET | /api/admin/users/{id} | ADMIN | Get user |
| PUT | /api/admin/users/{id} | ADMIN | Update user |
| DELETE | /api/admin/users/{id} | ADMIN | Delete user |
| PATCH | /api/admin/users/{keycloakId}/roles | ADMIN | Assign role |
| PATCH | /api/admin/users/{keycloakId}/status | ADMIN | Enable/disable |
| POST | /api/admin/users/{keycloakId}/reset-password | ADMIN | Reset password |
| GET | /api/users/me | ANY | Get profile |
| PUT | /api/users/me | ANY | Update profile |
| POST | /api/public/register | PUBLIC | Register client |

## Client Service (8082)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/clients | MANAGER | Create client |
| GET | /api/clients | MANAGER | List clients |
| GET | /api/clients/search?q= | MANAGER | Search clients |
| GET | /api/clients/{id} | MANAGER | Get client |
| PUT | /api/clients/{id} | MANAGER | Update client |
| DELETE | /api/clients/{id} | MANAGER | Delete client |

## Intervention Service (8083)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/interventions | TECHNICIAN | Create intervention |
| GET | /api/interventions | ANY | List interventions |
| GET | /api/interventions/{id} | ANY | Get intervention |
| PUT | /api/interventions/{id} | ANY | Update intervention |
| DELETE | /api/interventions/{id} | ANY | Delete (soft) |
| POST | /api/interventions/{id}/close | ANY | Close with signatures |
| POST | /api/interventions/{id}/assign | MANAGER | Assign technician |
| POST | /api/interventions/{id}/start | ANY | Start intervention |
| POST | /api/interventions/{id}/cancel | MANAGER | Cancel intervention |
| PATCH | /api/interventions/{id}/schedule | ANY | Update schedule |
| PATCH | /api/interventions/{id}/equipment | ANY | Update equipment |
| PATCH | /api/interventions/{id}/diagnosis | ANY | Update diagnosis |
| POST | /api/interventions/{id}/items | ANY | Add item |
| PUT | /api/interventions/{id}/items/{itemId} | ANY | Update item |
| DELETE | /api/interventions/{id}/items/{itemId} | ANY | Remove item |
| GET | /api/interventions/{id}/pdf | ANY | Generate PDF |
| POST | /api/sync/interventions | ANY | Mobile sync |

## Media Service (8084)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/media/upload | ANY | Upload file |
| POST | /api/media/upload-base64 | ANY | Upload base64 |
| GET | /api/media/{filename} | ANY | Download file |
| DELETE | /api/media/{filename} | ANY | Delete file |

## Notification Service (8085)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/notifications/email | ANY | Send email |

## Report Service (8086)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | /api/reports/interventions/csv | MANAGER | Export CSV |
| GET | /api/reports/interventions/pdf | MANAGER | Export PDF |
| GET | /api/reports/analytics | MANAGER | Get analytics |
