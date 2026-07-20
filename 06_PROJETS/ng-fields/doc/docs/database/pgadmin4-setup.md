# Guide pgAdmin 4 — Configuration PostgreSQL NG-Fields

**Objectif :** 1 serveur PostgreSQL avec 3 bases, rôles dédiés, schémas par domaine
**Outil :** pgAdmin 4 (déjà installé)
**Temps estimé :** 20 min
**Dépend de :** PostgreSQL 18 démarré (service `postgresql-x64-18`)

---

## Architecture cible

```
PostgreSQL 18 (localhost:5432)
├── Base : keycloak              ← Keycloak IAM (tables internes)
├── Base : ng_fields             ← API Spring Boot
│   ├── Schéma : auth            ← Utilisateurs locaux
│   ├── Schéma : client          ← Clients et équipements
│   ├── Schéma : intervention    ← Fiches, photos, signatures, items
│   ├── Schéma : notification    ← Logs d'envoi (push, email, WhatsApp)
│   └── Schéma : audit           ← Journal d'audit horodaté
└── Base : ng_fields_test        ← Tests d'intégration (isolée)
```

> **Règle :** Jamais le superuser `postgres` dans les applications. Chaque app a son rôle dédié.

---

## Étape 1 : Connexion à pgAdmin 4

1. Ouvrir **pgAdmin 4** (Démarrer → pgAdmin 4)
2. Panneau gauche → cliquer sur le serveur `PostgreSQL 18`
3. Saisir le mot de passe master de pgAdmin (celui défini à l'installation)
4. **Tools → Query Tool** (ou `Alt+Shift+Q`)

> Toutes les commandes SQL ci-dessous s'exécutent dans le Query Tool.
> Exécuter avec **F5** ou le bouton ▶ Run.

---

## Étape 2 : Créer les rôles applicatifs

Copier-coller le bloc, puis **F5** :

```sql
-- Rôle Keycloak
CREATE ROLE keycloak_user
    WITH LOGIN
         PASSWORD '${KC_DB_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 20;

COMMENT ON ROLE keycloak_user IS 'Keycloak IAM — accès exclusif à la base keycloak';

-- Rôle API Spring Boot
CREATE ROLE ng_fields_user
    WITH LOGIN
         PASSWORD '${DB_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 50;

COMMENT ON ROLE ng_fields_user IS 'API Spring Boot — accès exclusif à ng_fields';

-- Rôle tests
CREATE ROLE ng_fields_test_user
    WITH LOGIN
         PASSWORD '${DB_TEST_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 10;

COMMENT ON ROLE ng_fields_test_user IS 'Tests intégration — accès exclusif à ng_fields_test';
```

**Résultat :** `CREATE ROLE` × 3

---

## Étape 3 : Créer les bases de données

```sql
-- Base Keycloak
CREATE DATABASE keycloak
    WITH OWNER = keycloak_user
         ENCODING = 'UTF8'
         LC_COLLATE = 'French_France.1252'
         LC_CTYPE = 'French_France.1252'
         TEMPLATE = template0
         CONNECTION LIMIT = 30;

COMMENT ON DATABASE keycloak IS 'Base dédiée à Keycloak IAM';

-- Base API
CREATE DATABASE ng_fields
    WITH OWNER = ng_fields_user
         ENCODING = 'UTF8'
         LC_COLLATE = 'French_France.1252'
         LC_CTYPE = 'French_France.1252'
         TEMPLATE = template0
         CONNECTION LIMIT = 100;

COMMENT ON DATABASE ng_fields IS 'Base principale API Spring Boot NG-Fields';

-- Base tests
CREATE DATABASE ng_fields_test
    WITH OWNER = ng_fields_test_user
         ENCODING = 'UTF8'
         LC_COLLATE = 'French_France.1252'
         LC_CTYPE = 'French_France.1252'
         TEMPLATE = template0
         CONNECTION LIMIT = 20;

COMMENT ON DATABASE ng_fields_test IS 'Base de tests intégration Spring Boot';
```

**Résultat :** `CREATE DATABASE` × 3

---

## Étape 4 : Configurer `ng_fields` — schémas + droits

Dans pgAdmin, cliquer sur la base `ng_fields` dans le panneau gauche, **Tools → Query Tool** :

```sql
-- Retirer le schéma public par défaut
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM ng_fields_user;

-- Créer les 5 schémas métier
CREATE SCHEMA IF NOT EXISTS auth         AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS client       AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS intervention AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS notification AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS audit        AUTHORIZATION ng_fields_user;

COMMENT ON SCHEMA auth         IS 'Utilisateurs locaux, référence keycloak_id';
COMMENT ON SCHEMA client       IS 'Clients NG-STARs et équipements';
COMMENT ON SCHEMA intervention IS 'Fiches intervention, photos, signatures, items';
COMMENT ON SCHEMA notification IS 'Logs envoi push, email, WhatsApp';
COMMENT ON SCHEMA audit        IS 'Journal d''audit horodaté';

-- Droits sur les schémas
GRANT USAGE, CREATE ON SCHEMA auth         TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA client       TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA intervention TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA notification TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA audit        TO ng_fields_user;

-- Droits par défaut sur les futures tables
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

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";
```

**Résultat :** `CREATE SCHEMA` × 5, `GRANT` × ~20, `CREATE EXTENSION` × 3

---

## Étape 5 : Configurer `keycloak` — droits

Cliquer sur `keycloak` → **Tools → Query Tool** :

```sql
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON SEQUENCES TO keycloak_user;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

**Résultat :** `GRANT` × 3, `CREATE EXTENSION`

---

## Étape 6 : Configurer `ng_fields_test` — schémas + droits

Cliquer sur `ng_fields_test` → **Tools → Query Tool** :

```sql
CREATE SCHEMA IF NOT EXISTS auth         AUTHORIZATION ng_fields_test_user;
CREATE SCHEMA IF NOT EXISTS client       AUTHORIZATION ng_fields_test_user;
CREATE SCHEMA IF NOT EXISTS intervention AUTHORIZATION ng_fields_test_user;
CREATE SCHEMA IF NOT EXISTS notification AUTHORIZATION ng_fields_test_user;
CREATE SCHEMA IF NOT EXISTS audit        AUTHORIZATION ng_fields_test_user;

GRANT USAGE, CREATE ON SCHEMA auth         TO ng_fields_test_user;
GRANT USAGE, CREATE ON SCHEMA client       TO ng_fields_test_user;
GRANT USAGE, CREATE ON SCHEMA intervention TO ng_fields_test_user;
GRANT USAGE, CREATE ON SCHEMA notification TO ng_fields_test_user;
GRANT USAGE, CREATE ON SCHEMA audit        TO ng_fields_test_user;

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

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
```

---

## Étape 7 : Vérifier

Dans n'importe quelle Query Tool (connecté en `postgres`) :

```sql
-- Rôles
SELECT rolname, rolcanlogin, rolconnlimit
FROM pg_roles
WHERE rolname IN ('keycloak_user', 'ng_fields_user', 'ng_fields_test_user')
ORDER BY rolname;

-- Bases
SELECT datname, pg_catalog.pg_get_userbyid(datdba) AS owner, pg_encoding_to_char(encoding)
FROM pg_database
WHERE datname IN ('keycloak', 'ng_fields', 'ng_fields_test')
ORDER BY datname;

-- Schémas (connecté à ng_fields)
SELECT schema_name, schema_owner
FROM information_schema.schemata
WHERE catalog_name = 'ng_fields'
  AND schema_name IN ('auth', 'client', 'intervention', 'notification', 'audit')
ORDER BY schema_name;
```

**Résultat attendu :** 3 rôles, 3 bases, 5 schémas — tous corrects.

---

## Récapitulatif

| Composant | Base | Hôte | Port | Utilisateur | Mot de passe |
|---|---|---|---|---|---|
| API Spring Boot | `ng_fields` | `localhost` | `5432` | `ng_fields_user` | `${DB_PASSWORD}` |
| Keycloak | `keycloak` | `localhost` | `5432` | `keycloak_user` | `${KC_DB_PASSWORD}` |
| Tests Spring Boot | `ng_fields_test` | `localhost` | `5432` | `ng_fields_test_user` | `${DB_TEST_PASSWORD}` |

---

## Dépannage

| Symptôme | Cause | Solution |
|---|---|---|
| `locale "fr_FR.UTF-8" does not exist` | Locale Windows incompatible | Utiliser `French_France.1252` |
| `role "xyz" already exists` | Rôle existant | Ajouter `IF NOT EXISTS` ou ignorer |
| `database "xyz" already exists` | Base existante | Ignorer ou `DROP DATABASE xyz` |
| `password authentication failed` | Mauvais mot de passe | Vérifier le hash dans pg_hba.conf (scram-sha-256) |
| `Connection refused (port 5432)` | PostgreSQL arrêté | `services.msc` → démarrer `postgresql-x64-18` |
| `permission denied for schema` | Droits manquants | Vérifier `GRANT USAGE ON SCHEMA` |

---

## Prochaine étape

La base est prête. Voir :

- [database-model.md](06_PROJETS/ng-fields/Doc/docs/database/database-model.md) — Architecture globale
- [ng-fields-application.md](ng-fields-application.md) — DDL et tables détaillées
- [keycloak.md](keycloak.md) — Connexion Keycloak → PostgreSQL
