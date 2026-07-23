# 06 — CRUD Interventions

**Objectif :** API REST pour créer et gérer les fiches d'intervention (8 sections)
**Dépend de :** [05-client-crud.md](05-client-crud.md)

---

## Endpoints

| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| POST | `/api/interventions` | TECHNICIAN, MANAGER | Créer une intervention |
| PATCH | `/api/interventions/{id}` | Propriétaire, MANAGER | Mettre à jour (sections 1-8) |
| GET | `/api/interventions` | Tous | Lister (filtré par rôle) |
| GET | `/api/interventions/{id}` | Tous | Détail complet |
| POST | `/api/interventions/{id}/items` | TECHNICIAN | Ajouter un consommable |
| GET | `/api/interventions/{id}/items` | Tous | Lister les consommables |
| PUT | `/api/interventions/{id}/items/{itemId}` | TECHNICIAN | Modifier un item |
| DELETE | `/api/interventions/{id}/items/{itemId}` | TECHNICIAN | Supprimer un item |

## Sections de l'intervention

| Section | Champs | Statut |
|---------|--------|--------|
| 1. Infos générales | `client_id`, `type`, `date`, `status` | |
| 2. Horaires | `departure_time`, `arrival_time`, `start_time`, `end_time`, `duration` | |
| 3. Diagnostic | `problem_desc`, `diagnosis` | |
| 4. Travaux | `work_done`, `equipment_*` | |
| 5. Pièces | Items (name, quantity) | |
| 6. Résultat | `result` (RESOLVED/PARTIAL/UNRESOLVED) | |
| 7. Recommandations | `recommendations` | |

## Guide détaillé

> **À rédiger après Sprint 2**
