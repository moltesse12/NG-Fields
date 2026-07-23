# Database Schema

**Mis à jour :** 23/07/2026

NG-Fields uses PostgreSQL with multi-schema architecture. Each service owns its schema. Schema management is via **Flyway** (auth, client, intervention, report) with Hibernate in `ddl-auto: validate` mode.

## Schema Management

| Service | Strategy | Migration |
|---------|----------|-----------|
| auth-service | Flyway | `V1__init.sql` |
| client-service | Flyway | `V1__init.sql` |
| intervention-service | Flyway | `V1__init.sql` |
| report-service | Flyway | `V1__init.sql` |
| media-service | Hibernate validate | No tables (disk storage) |
| notification-service | Hibernate validate | `email_logs` |

Flyway runs automatically at startup (`spring.flyway.baseline-on-migrate: true`). Hibernate validates schema consistency but does not create/alter tables.

---

## Schema: `auth`

### `users`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| keycloak_id | UUID | NOT NULL UNIQUE |
| username | VARCHAR(50) | NOT NULL UNIQUE |
| email | VARCHAR(255) | NOT NULL UNIQUE |
| first_name | VARCHAR(255) | NOT NULL |
| last_name | VARCHAR(255) | NOT NULL |
| role | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(255) | |
| company_id | UUID | FK -> companies(id) (NULL pour ADMIN/MANAGER/TECHNICIAN) |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| must_change_password | BOOLEAN | NOT NULL DEFAULT FALSE |
| email_verified | BOOLEAN | NOT NULL DEFAULT FALSE |
| updated_by | UUID | |
| version | BIGINT | DEFAULT 0 |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_users_company_id`

### `audit_logs`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| user_id | UUID | |
| action | VARCHAR(255) | NOT NULL |
| resource | VARCHAR(255) | |
| resource_id | VARCHAR(255) | |
| details | TEXT | |
| ip_address | VARCHAR(255) | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_audit_logs_user_id`

### `companies`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| name | VARCHAR(200) | NOT NULL |
| email | VARCHAR(150) | |
| phone | VARCHAR(30) | |
| address | VARCHAR(255) | |
| contact_name | VARCHAR(150) | |
| contact_phone | VARCHAR(30) | |
| keycloak_organization_id | UUID | |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | DEFAULT 0 |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

### `company_users`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| company_id | UUID | NOT NULL FK -> companies(id) |
| keycloak_user_id | UUID | UNIQUE |
| email | VARCHAR(150) | |
| first_name | VARCHAR(100) | |
| last_name | VARCHAR(100) | |
| role | VARCHAR(50) | NOT NULL (CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER) |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | DEFAULT 0 |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_company_users_company_id`

### `company_access_log`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| company_id | UUID | NOT NULL FK -> companies(id) |
| user_id | UUID | FK -> company_users(id) |
| action | VARCHAR(100) | NOT NULL |
| resource | VARCHAR(100) | |
| resource_id | UUID | |
| ip_address | INET | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

### `failed_login_attempts`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| username | VARCHAR(255) | NOT NULL |
| ip_address | VARCHAR(255) | NOT NULL |
| successful | BOOLEAN | NOT NULL |
| attempted_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| locked_until | TIMESTAMPTZ | |

**Indexes:** `idx_failed_login_username`

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
| version | BIGINT | DEFAULT 0 |
| created_by | VARCHAR(100) | |
| updated_by | UUID | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_clients_company_name`

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
| created_by | UUID | |
| updated_by | UUID | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_contacts_client_id`

---

## Schema: `intervention`

### `interventions`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| reference | VARCHAR(255) | NOT NULL UNIQUE |
| client_id | UUID | NOT NULL |
| client_name | VARCHAR(255) | |
| client_email | VARCHAR(255) | |
| client_phone | VARCHAR(255) | |
| client_address | VARCHAR(255) | |
| equipment_type | VARCHAR(255) | |
| equipment_brand | VARCHAR(255) | |
| equipment_model | VARCHAR(255) | |
| equipment_serial | VARCHAR(255) | |
| equipment_location | VARCHAR(255) | |
| reported_issue | VARCHAR(255) | |
| diagnosis | VARCHAR(255) | |
| work_done | VARCHAR(255) | |
| status | VARCHAR(255) | NOT NULL DEFAULT 'PENDING' |
| intervention_date | TIMESTAMPTZ | |
| created_by | UUID | |
| assigned_to | UUID | |
| site_address | VARCHAR(255) | |
| site_city | VARCHAR(255) | |
| estimated_cost | NUMERIC(19, 2) | |
| gps_latitude | DOUBLE PRECISION | |
| gps_longitude | DOUBLE PRECISION | |
| total_cost | NUMERIC(19, 2) | |
| client_signature | VARCHAR(255) | |
| technician_signature | VARCHAR(255) | |
| manager_signature | VARCHAR(255) | |
| signed_at | TIMESTAMPTZ | |
| departure_time | TIMESTAMPTZ | |
| arrival_time | TIMESTAMPTZ | |
| start_time | TIMESTAMPTZ | |
| end_time | TIMESTAMPTZ | |
| duration_minutes | INTEGER | |
| result | VARCHAR(20) | |
| follow_up_recommended | BOOLEAN | DEFAULT FALSE |
| recommendations | TEXT | |
| local_id | VARCHAR(255) | UNIQUE |
| notes | VARCHAR(255) | |
| active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | DEFAULT 0 |
| updated_by | UUID | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_interventions_client_id`, `idx_interventions_status`, `idx_interventions_assigned_to`, `idx_interventions_created_at`

### `intervention_items`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| intervention_id | UUID | NOT NULL FK -> interventions(id) ON DELETE CASCADE |
| type | VARCHAR(255) | NOT NULL |
| description | VARCHAR(255) | NOT NULL |
| quantity | INTEGER | NOT NULL DEFAULT 1 |
| unit_price | NUMERIC(19, 2) | NOT NULL DEFAULT 0 |
| total | NUMERIC(19, 2) | NOT NULL DEFAULT 0 |
| version | BIGINT | DEFAULT 0 |
| created_by | UUID | |
| updated_by | UUID | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_intervention_items_intervention_id`

### `intervention_photos`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| intervention_id | UUID | NOT NULL FK -> interventions(id) ON DELETE CASCADE |
| url | TEXT | NOT NULL |
| type | VARCHAR(10) | NOT NULL |
| latitude | DOUBLE PRECISION | |
| longitude | DOUBLE PRECISION | |
| taken_at | TIMESTAMPTZ | |
| original_filename | VARCHAR(200) | |
| version | BIGINT | DEFAULT 0 |
| created_by | UUID | |
| updated_by | UUID | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_intervention_photos_intervention_id`

---

## Schema: `report`

### `pdf_templates`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| name | VARCHAR(255) | NOT NULL |
| description | VARCHAR(255) | |
| template_type | VARCHAR(255) | NOT NULL DEFAULT 'INTERVENTION_REPORT' |
| config | JSONB | NOT NULL DEFAULT '{}' |
| is_default | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_by | VARCHAR(255) | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

**Indexes:** `idx_pdf_templates_template_type`

### `email_templates`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| name | VARCHAR(255) | NOT NULL |
| description | VARCHAR(255) | |
| template_key | VARCHAR(255) | NOT NULL UNIQUE |
| subject | VARCHAR(255) | NOT NULL |
| body_html | TEXT | NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_by | VARCHAR(255) | |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

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

## Migration Files

| File | Service | Content |
|------|---------|---------|
| `auth-service/.../db/migration/V1__init.sql` | auth | users, audit_logs, companies, company_users, company_access_log, failed_login_attempts |
| `client-service/.../db/migration/V1__init.sql` | client | clients, contacts |
| `intervention-service/.../db/migration/V1__init.sql` | intervention | interventions, intervention_items, intervention_photos |
| `report-service/.../db/migration/V1__init.sql` | report | pdf_templates, email_templates |

All migrations use `CREATE EXTENSION IF NOT EXISTS "uuid-ossp"` for UUID generation.
