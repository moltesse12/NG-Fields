# Database Schema

NG-Fields uses PostgreSQL with multi-schema architecture. Each service owns its schema.

## Schema: `auth`

### `users`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| keycloak_id | UUID | NOT NULL UNIQUE |
| username | VARCHAR(50) | NOT NULL UNIQUE |
| email | VARCHAR(150) | NOT NULL UNIQUE |
| first_name | VARCHAR(100) | |
| last_name | VARCHAR(100) | |
| role | VARCHAR(20) | NOT NULL |
| phone | VARCHAR(30) | |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | NOT NULL DEFAULT 0 |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

### `audit_logs`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| user_id | UUID | |
| action | VARCHAR(50) | NOT NULL |
| resource | VARCHAR(50) | |
| resource_id | VARCHAR(100) | |
| details | TEXT | |
| ip_address | VARCHAR(45) | |
| created_at | TIMESTAMPTZ | NOT NULL |

**Indexes:** `idx_audit_user_id`, `idx_audit_created_at`

---

## Schema: `client`

### `clients`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| reference | VARCHAR(20) | NOT NULL UNIQUE |
| company_name | VARCHAR(200) | NOT NULL |
| contact_name | VARCHAR(150) | |
| email | VARCHAR(150) | NOT NULL UNIQUE |
| phone | VARCHAR(30) | |
| address | TEXT | |
| latitude | DOUBLE PRECISION | |
| longitude | DOUBLE PRECISION | |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | NOT NULL DEFAULT 0 |
| created_by | VARCHAR(100) | |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

**Indexes:** `idx_clients_email`, `idx_clients_active`, `idx_clients_company`, `idx_clients_search_trgm` (trigram GIN)

### `contacts`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| client_id | UUID | NOT NULL FK -> clients(id) ON DELETE CASCADE |
| full_name | VARCHAR(150) | NOT NULL |
| email | VARCHAR(150) | |
| phone | VARCHAR(30) | |
| role | VARCHAR(50) | |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMPTZ | NOT NULL |

**Indexes:** `idx_contacts_client_id`, `idx_contacts_active`

---

## Schema: `intervention`

### `interventions`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| reference | VARCHAR(20) | NOT NULL UNIQUE |
| client_id | UUID | |
| client_name | VARCHAR(200) | |
| client_email | VARCHAR(150) | |
| client_phone | VARCHAR(30) | |
| client_address | TEXT | |
| latitude | DOUBLE PRECISION | |
| longitude | DOUBLE PRECISION | |
| technician_id | UUID | |
| technician_name | VARCHAR(150) | |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' |
| priority | VARCHAR(20) | NOT NULL DEFAULT 'MEDIUM' |
| title | VARCHAR(200) | |
| description | TEXT | |
| intervention_type | VARCHAR(50) | |
| scheduled_date | TIMESTAMPTZ | |
| started_at | TIMESTAMPTZ | |
| completed_at | TIMESTAMPTZ | |
| cancelled_at | TIMESTAMPTZ | |
| equipment_serial | VARCHAR(100) | |
| equipment_model | VARCHAR(100) | |
| equipment_brand | VARCHAR(100) | |
| diagnosis | TEXT | |
| resolution | TEXT | |
| version | BIGINT | NOT NULL DEFAULT 0 |
| created_by | VARCHAR(100) | |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

### `intervention_items`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| intervention_id | UUID | NOT NULL FK -> interventions(id) |
| description | VARCHAR(500) | NOT NULL |
| quantity | INTEGER | NOT NULL DEFAULT 1 |
| unit_price | DOUBLE PRECISION | |
| item_type | VARCHAR(30) | |
| created_at | TIMESTAMPTZ | NOT NULL |

---

## Schema: `media`

### `media_files`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| filename | VARCHAR(255) | NOT NULL UNIQUE |
| original_filename | VARCHAR(255) | NOT NULL |
| content_type | VARCHAR(100) | |
| size | BIGINT | NOT NULL |
| intervention_id | UUID | |
| uploaded_by | VARCHAR(100) | |
| created_at | TIMESTAMPTZ | NOT NULL |

---

## Schema: `notification`

### `email_logs`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| to | VARCHAR(150) | NOT NULL |
| subject | VARCHAR(200) | NOT NULL |
| template | VARCHAR(50) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |
| error_message | TEXT | |
| created_at | TIMESTAMPTZ | NOT NULL |

---

## Schema: `report`

### `report_requests`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| type | VARCHAR(20) | NOT NULL |
| format | VARCHAR(10) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |
| file_path | VARCHAR(500) | |
| error_message | TEXT | |
| created_by | VARCHAR(100) | |
| created_at | TIMESTAMPTZ | NOT NULL |
| completed_at | TIMESTAMPTZ | |

---

## Flyway Migrations

Each service uses Flyway for schema management. Migrations are in `src/main/resources/db/migration/`.

| Service | Versions | Description |
|---------|----------|-------------|
| auth-service | V1-V3 | Users, audit logs, optimistic locking |
| client-service | V1-V5 | Clients, reference sequence, trigram index, version, contacts |
| intervention-service | V1-V5+ | Interventions, items, indexes |
| media-service | V1-V3 | Media files |
| notification-service | V1-V2 | Email logs |
| report-service | V1-V2 | Report requests |
