# Base ng_fields — API Spring Boot

**Base :** `ng_fields` (principale) + `ng_fields_test` (tests, même structure)
**Propriétaire :** `ng_fields_user`
**Schémas :** `auth`, `client`, `intervention`, `media`, `notification`, `report`

---

## 1. Vue d'ensemble

```
ng_fields
├── auth           ← Utilisateurs locaux (référence Keycloak) + audit_logs
├── client         ← Clients NG-STARs + contacts
├── intervention   ← Fiches terrain, pièces, photos, signatures
├── media          ← Fichiers uploadés (photos, signatures, PDF)
├── notification   ← Logs d'envoi email
└── report         ← Requêtes de rapports (CSV, PDF, analytics)
```

## 2. Dictionnaire des Tables

### 2.1 `client.clients` — Clients (entreprises / sites)

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant unique |
| `company_name` | `VARCHAR(150)` | `NOT NULL` | | Raison sociale |
| `email` | `VARCHAR(150)` | | | Email de contact |
| `phone` | `VARCHAR(20)` | | | Téléphone |
| `address` | `TEXT` | | | Adresse complète |
| `latitude` | `DOUBLE PRECISION` | | | GPS lat |
| `longitude` | `DOUBLE PRECISION` | | | GPS lng |
| `contact_name` | `VARCHAR(100)` | | | Nom du contact principal |
| `contact_phone` | `VARCHAR(20)` | | | Téléphone du contact |
| `active` | `BOOLEAN` | `NOT NULL` | `TRUE` | Client actif/désactivé |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date création |
| `updated_at` | `TIMESTAMPTZ` | | `NOW()` | Date modification |

**Index :** `idx_clients_active` sur `(active)`, `idx_clients_company_name` sur `(company_name)`
**Entité JPA :** `Client.java` — `@Table(name = "clients", schema = "client")` ✅
**API :** `POST/GET/PUT/DELETE /api/clients`

---

### 2.2 `intervention.interventions` — Fiches d'intervention (8 sections)

| Colonne | Type | Contrainte | Défaut | Section | Description |
|---|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | — | Identifiant unique |
| `local_id` | `VARCHAR(50)` | `UNIQUE` | | — | ID côté mobile (sync) |
| `date` | `TIMESTAMPTZ` | `NOT NULL` | | S1 | Date intervention |
| `status` | `VARCHAR(20)` | `NOT NULL` | `'PENDING'` | S1 | `PENDING / IN_PROGRESS / COMPLETED / CANCELLED` |
| `type` | `VARCHAR(50)` | | | S1 | Type (dépannage, maintenance, etc.) |
| `technician_id` | `VARCHAR(36)` | | | S1 | Keycloak user ID |
| `technician_name` | `VARCHAR(100)` | | | S1 | Nom du technicien |
| `client_id` | `UUID` | `FK → client.clients(id)` | | S1 | Client concerné |
| `departure_time` | `TIME` | | | S2 | Heure départ base |
| `arrival_time` | `TIME` | | | S2 | Heure arrivée site |
| `intervention_start_time` | `TIME` | | | S2 | Début intervention |
| `intervention_end_time` | `TIME` | | | S2 | Fin intervention |
| `return_time` | `TIME` | | | S2 | Heure retour base |
| `duration_minutes` | `INTEGER` | | | S2 | Durée calculée |
| `problem_desc` | `TEXT` | | | S3 | Description du problème |
| `diagnosis` | `TEXT` | | | S3 | Diagnostic technique |
| `work_done` | `TEXT` | | | S4 | Travaux effectués |
| `equipment_type` | `VARCHAR(100)` | | | S4 | Type d'équipement |
| `equipment_brand` | `VARCHAR(100)` | | | S4 | Marque |
| `equipment_model` | `VARCHAR(100)` | | | S4 | Modèle |
| `equipment_serial` | `VARCHAR(100)` | | | S4 | N° de série |
| `equipment_location` | `VARCHAR(200)` | | | S4 | Localisation |
| `result` | `VARCHAR(20)` | | | S6 | `RESOLVED / PARTIAL / UNRESOLVED` |
| `recommendations` | `TEXT` | | | S7 | Recommandations |
| `signature_client_url` | `TEXT` | | | S8 | URL signature client |
| `signature_technician_url` | `TEXT` | | | S8 | URL signature technicien |
| `signature_manager_url` | `TEXT` | | | S8 | URL signature manager |
| `pdf_url` | `TEXT` | | | — | URL PDF généré |
| `synced_at` | `TIMESTAMPTZ` | | | — | Dernière sync mobile |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | — | Date création |
| `updated_at` | `TIMESTAMPTZ` | | `NOW()` | — | Date modification |

**Index :**

| Nom | Colonnes | Description |
|---|---|---|
| `idx_interventions_client` | `(client_id)` | Recherche par client |
| `idx_interventions_technician` | `(technician_id)` | Filtre technicien |
| `idx_interventions_date` | `(date)` | Tri chronologique |
| `idx_interventions_status` | `(status)` | Filtre statut |
| `idx_interventions_local_id` | `(local_id)` | Sync offline |

**Entité JPA :** `Intervention.java` — `@Table(name = "interventions", schema = "intervention")` ✅
**API :** `POST/GET/PATCH /api/interventions`

---

### 2.3 `intervention.intervention_items` — Pièces et consommables

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant |
| `name` | `VARCHAR(200)` | `NOT NULL` | | Désignation |
| `quantity` | `INTEGER` | `NOT NULL` | `1` | Quantité |
| `intervention_id` | `UUID` | `FK → intervention.interventions(id) ON DELETE CASCADE` | | Intervention parente |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date création |

**Entité JPA :** `InterventionItem.java` — `@Table(name = "intervention_items", schema = "intervention")` ✅
**API :** `POST/GET/PUT/DELETE /api/interventions/{id}/items`

---

### 2.4 `intervention.intervention_photos` — Photos avant/après

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant |
| `intervention_id` | `UUID` | `FK → intervention.interventions(id) ON DELETE CASCADE` | | Intervention parente |
| `file_url` | `TEXT` | `NOT NULL` | | URL du fichier |
| `file_type` | `VARCHAR(10)` | | `'BEFORE'` | `BEFORE / AFTER / OTHER` |
| `latitude` | `DOUBLE PRECISION` | | | GPS lat (EXIF) |
| `longitude` | `DOUBLE PRECISION` | | | GPS lng (EXIF) |
| `taken_at` | `TIMESTAMPTZ` | | | Date prise de vue |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date création |

**Index :** `idx_photos_intervention` sur `(intervention_id)`
**Entité JPA :** À créer ⏳
**API :** `POST/GET/DELETE /api/interventions/{id}/photos`

---

### 2.5 `auth.users` — Registre local des utilisateurs (optionnel)

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant |
| `keycloak_id` | `VARCHAR(36)` | `UNIQUE` | | ID Keycloak |
| `username` | `VARCHAR(50)` | `NOT NULL UNIQUE` | | Nom d'utilisateur |
| `email` | `VARCHAR(255)` | `NOT NULL` | | Email |
| `first_name` | `VARCHAR(100)` | | | Prénom |
| `last_name` | `VARCHAR(100)` | | | Nom |
| `role` | `VARCHAR(20)` | `NOT NULL` | | `ADMIN / MANAGER / TECHNICIAN / CLIENT_ADMIN / CLIENT_USER / CLIENT_VIEWER` |
| `department` | `VARCHAR(100)` | | | Département |
| `phone` | `VARCHAR(20)` | | | Téléphone |
| `active` | `BOOLEAN` | `NOT NULL` | `TRUE` | Compte actif |
| `last_login_at` | `TIMESTAMPTZ` | | | Dernière connexion |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date création |
| `updated_at` | `TIMESTAMPTZ` | | `NOW()` | Date modification |

> **Note :** Table optionnelle. L'identité est gérée par Keycloak. Cette table sert de cache local.

**Entité JPA :** À créer ⏳
**API :** `POST /api/admin/users`, `POST /api/public/register`

---

### 2.6 `notification.notification_log` — Logs d'envoi

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant |
| `intervention_id` | `UUID` | `FK → intervention.interventions(id)` | | Intervention liée |
| `channel` | `VARCHAR(20)` | `NOT NULL` | | `PUSH / EMAIL / WHATSAPP` |
| `recipient` | `VARCHAR(255)` | `NOT NULL` | | Destinataire |
| `template` | `VARCHAR(100)` | | | Template utilisé |
| `status` | `VARCHAR(20)` | `NOT NULL` | `'PENDING'` | `PENDING / SENT / FAILED` |
| `error_message` | `TEXT` | | | Message d'erreur |
| `sent_at` | `TIMESTAMPTZ` | | | Date d'envoi |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date création |

**Index :** `idx_notif_intervention` sur `(intervention_id)`, `idx_notif_status` sur `(status)`
**Entité JPA :** À créer ⏳

---

### 2.7 `audit.audit_log` — Journal d'audit

| Colonne | Type | Contrainte | Défaut | Description |
|---|---|---|---|---|
| `id` | `UUID` | `PK` | `gen_random_uuid()` | Identifiant |
| `actor_id` | `VARCHAR(36)` | `NOT NULL` | | Keycloak user ID |
| `actor_username` | `VARCHAR(50)` | | | Nom d'utilisateur |
| `action` | `VARCHAR(50)` | `NOT NULL` | | `CREATE / UPDATE / DELETE / LOGIN / EXPORT` |
| `entity_type` | `VARCHAR(50)` | `NOT NULL` | | Table concernée |
| `entity_id` | `UUID` | | | ID de l'entité |
| `changes` | `JSONB` | | | Changements (old → new) |
| `ip_address` | `VARCHAR(45)` | | | Adresse IP |
| `created_at` | `TIMESTAMPTZ` | | `NOW()` | Date de l'action |

**Index :** `idx_audit_actor` sur `(actor_id)`, `idx_audit_entity` sur `(entity_type, entity_id)`, `idx_audit_created` sur `(created_at)`
**Entité JPA :** À créer ⏳

---

## 3. Diagramme des Relations

```
client.clients
┌──────────────────┐      1
│ id (PK)          │◄──────────┐
│ company_name     │           │
│ email            │           │
│ phone            │           │
│ address          │           │
│ latitude         │           │
│ longitude        │           │
│ contact_name     │           │
│ contact_phone    │           │
│ active           │           │
│ created_at       │           │
│ updated_at       │           │
└──────────────────┘           │
                               │
intervention.interventions     │   *
┌──────────────────┐           │
│ id (PK)          │──┐       │
│ local_id (UQ)    │  │       │
│ date             │  │       │
│ status           │  │       │
│ type             │  │       │
│ technician_id    │  │       │
│ client_id (FK)   │──┼───────┘
│ ... (8 sections) │  │
│ created_at       │  │
│ updated_at       │  │
└──────────────────┘  │
       │              │
       │  *           │  *
       │              │
       ▼              ▼
┌──────────────────┐  ┌─────────────────────────────┐
│ intervention.    │  │ intervention.               │
│ intervention_items│  │ intervention_photos          │
│                  │  │                              │
│ id (PK)          │  │ id (PK)                      │
│ name             │  │ file_url                     │
│ quantity         │  │ file_type (BEFORE/AFTER)     │
│ intervention_id  │  │ latitude                     │
│   (FK)           │  │ longitude                    │
│ created_at       │  │ taken_at                     │
└──────────────────┘  │ intervention_id (FK)          │
                      │ created_at                   │
                      └─────────────────────────────┘
```

---

## 4. Mapping Backlog → Tables

| Sprint | Guide | Tables créées / modifiées | Statut |
|---|---|---|---|
| Sprint 1b | 04-user-registration | `auth.users` | ✅ Implémenté |
| Sprint 2 | 05-client-crud | `client.clients`, `client.contacts` | ✅ Implémenté |
| Sprint 2 | 06-intervention-crud | `intervention.interventions`, `intervention.intervention_items` | ✅ Implémenté |
| Sprint 3 | 07-photos-storage | `intervention.intervention_photos` | ✅ Implémenté |
| Sprint 3 | 08-signatures | *(colonnes `signature_*_url` dans `interventions`)* | ✅ Implémenté |
| Sprint 3 | 09-pdf-generation | *(colonne `pdf_url` dans `interventions`)* | ✅ Implémenté |
| Sprint 4 | 10-hors-ligne | *(colonnes `local_id`, `synced_at` dans `interventions`)* | ✅ Implémenté |
| Sprint 4 | 11-openproject | *(colonne `openproject_ticket_id` dans `interventions`)* | ✅ Implémenté |
| Sprint 4 | 12-dashboard | *(requêtes d'agrégation)* | ✅ Implémenté |
| — | Notification | `notification.email_logs` | ✅ Implémenté |
| — | Media | `media.media_files` | ✅ Implémenté |
| — | Report | `report.report_requests` | ✅ Implémenté |
| — | Audit | `auth.audit_logs` | ✅ Implémenté |

---

## 5. Annotations `@Table(schema = ...)` à ajouter

| Entité | Fichier | Annotation actuelle | Nouvelle annotation |
|---|---|---|---|
| `Client` | `Client.java:10` | `@Table(name = "clients")` | `@Table(name = "clients", schema = "client")` |
| `Intervention` | `Intervention.java:13` | `@Table(name = "interventions")` | `@Table(name = "interventions", schema = "intervention")` |
| `InterventionItem` | `InterventionItem.java:10` | `@Table(name = "intervention_items")` | `@Table(name = "intervention_items", schema = "intervention")` |

---

## 6. État d'avancement

### ✅ Existant (entités JPA)

| Table | Schéma | Entité JPA | Colonnes | Statut |
|---|---|---|---|---|
| `clients` | `client` | `Client.java` | 12 | ✅ Implémenté |
| `contacts` | `client` | `Contact.java` | 8 | ✅ Implémenté |
| `interventions` | `intervention` | `Intervention.java` | 37+ | ✅ Implémenté |
| `intervention_items` | `intervention` | `InterventionItem.java` | 7 | ✅ Implémenté |
| `intervention_photos` | `intervention` | `InterventionPhoto.java` | 8 | ✅ Implémenté |
| `users` | `auth` | `User.java` | 12 | ✅ Implémenté |
| `audit_logs` | `auth` | `AuditLog.java` | 8 | ✅ Implémenté |
| `media_files` | `media` | — | 7 | ✅ Implémenté |
| `email_logs` | `notification` | — | 7 | ✅ Implémenté |
| `report_requests` | `report` | — | 9 | ✅ Implémenté |

### ⏳ À créer

| Table | Schéma | Priorité | Statut |
|---|---|---|---|
| — | — | — | Toutes les tables critiques sont implémentées |

---

## 7. Extensions PostgreSQL

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";    -- chiffrement
CREATE EXTENSION IF NOT EXISTS "unaccent";    -- recherche sans accents
```

---

## 8. DDL Complet

```sql
-- ============================================================
-- DDL NG-Fields — Base ng_fields
-- Schémas : auth, client, intervention, notification, audit
-- ============================================================

-- Schémas
CREATE SCHEMA IF NOT EXISTS auth         AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS client       AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS intervention AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS notification AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS audit        AUTHORIZATION ng_fields_user;

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";

-- Tables

CREATE TABLE client.clients (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name    VARCHAR(150) NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(20),
    address         TEXT,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    contact_name    VARCHAR(100),
    contact_phone   VARCHAR(20),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE intervention.interventions (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id                VARCHAR(50) UNIQUE,
    date                    TIMESTAMPTZ NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    type                    VARCHAR(50),
    technician_id           VARCHAR(36),
    technician_name         VARCHAR(100),
    client_id               UUID REFERENCES client.clients(id),
    departure_time          TIME,
    arrival_time            TIME,
    intervention_start_time TIME,
    intervention_end_time   TIME,
    return_time             TIME,
    duration_minutes        INTEGER,
    problem_desc            TEXT,
    openproject_ticket_id   VARCHAR(50),
    diagnosis               TEXT,
    work_done               TEXT,
    equipment_type          VARCHAR(100),
    equipment_brand         VARCHAR(100),
    equipment_model         VARCHAR(100),
    equipment_serial        VARCHAR(100),
    equipment_location      VARCHAR(200),
    result                  VARCHAR(20),
    recommendations         TEXT,
    signature_client_url    TEXT,
    signature_technician_url TEXT,
    signature_manager_url   TEXT,
    pdf_url                 TEXT,
    synced_at               TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE intervention.intervention_items (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             VARCHAR(200) NOT NULL,
    quantity         INTEGER NOT NULL DEFAULT 1,
    intervention_id  UUID NOT NULL REFERENCES intervention.interventions(id) ON DELETE CASCADE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE intervention.intervention_photos (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id  UUID NOT NULL REFERENCES intervention.interventions(id) ON DELETE CASCADE,
    file_url         TEXT NOT NULL,
    file_type        VARCHAR(10) DEFAULT 'BEFORE',
    latitude         DOUBLE PRECISION,
    longitude        DOUBLE PRECISION,
    taken_at         TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE auth.users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id     VARCHAR(36) UNIQUE,
    username        VARCHAR(50) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    role            VARCHAR(20) NOT NULL,
    department      VARCHAR(100),
    phone           VARCHAR(20),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notification.notification_log (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id  UUID REFERENCES intervention.interventions(id),
    channel          VARCHAR(20) NOT NULL,
    recipient        VARCHAR(255) NOT NULL,
    template         VARCHAR(100),
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message    TEXT,
    sent_at          TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit.audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id        VARCHAR(36) NOT NULL,
    actor_username  VARCHAR(50),
    action          VARCHAR(50) NOT NULL,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID,
    changes         JSONB,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes

CREATE INDEX idx_clients_active         ON client.clients(active);
CREATE INDEX idx_clients_company_name   ON client.clients(company_name);

CREATE INDEX idx_interventions_client    ON intervention.interventions(client_id);
CREATE INDEX idx_interventions_technician ON intervention.interventions(technician_id);
CREATE INDEX idx_interventions_date      ON intervention.interventions(date);
CREATE INDEX idx_interventions_status    ON intervention.interventions(status);
CREATE INDEX idx_interventions_local_id  ON intervention.interventions(local_id);

CREATE INDEX idx_items_intervention     ON intervention.intervention_items(intervention_id);

CREATE INDEX idx_photos_intervention    ON intervention.intervention_photos(intervention_id);

CREATE INDEX idx_notif_intervention     ON notification.notification_log(intervention_id);
CREATE INDEX idx_notif_status           ON notification.notification_log(status);

CREATE INDEX idx_audit_actor            ON audit.audit_log(actor_id);
CREATE INDEX idx_audit_entity           ON audit.audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_created          ON audit.audit_log(created_at);

-- Trigger updated_at automatique

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_clients_updated_at
    BEFORE UPDATE ON client.clients
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_interventions_updated_at
    BEFORE UPDATE ON intervention.interventions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON auth.users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- Droits

GRANT USAGE ON SCHEMA auth, client, intervention, notification, audit TO ng_fields_user;
```

---

## 9. Rôles et privilèges (ALTER DEFAULT PRIVILEGES)

Exécuter UNE FOIS après création des schémas, connecté en tant que `postgres` :

```sql
ALTER DEFAULT PRIVILEGES IN SCHEMA auth
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ng_fields_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth
    GRANT USAGE, SELECT ON SEQUENCES TO ng_fields_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA client
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ng_fields_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA client
    GRANT USAGE, SELECT ON SEQUENCES TO ng_fields_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA intervention
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ng_fields_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA intervention
    GRANT USAGE, SELECT ON SEQUENCES TO ng_fields_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA notification
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ng_fields_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA notification
    GRANT USAGE, SELECT ON SEQUENCES TO ng_fields_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA audit
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ng_fields_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
    GRANT USAGE, SELECT ON SEQUENCES TO ng_fields_user;

-- Pour la base de tests (TRUNCATE autorisé pour reset entre les tests)
ALTER DEFAULT PRIVILEGES IN SCHEMA auth
    GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLES TO ng_fields_test_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA client
    GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLES TO ng_fields_test_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA intervention
    GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLES TO ng_fields_test_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA notification
    GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLES TO ng_fields_test_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
    GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLES TO ng_fields_test_user;
```
