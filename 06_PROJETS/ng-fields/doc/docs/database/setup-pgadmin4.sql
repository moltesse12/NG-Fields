-- ============================================================
-- SCRIPT COMPLET — Configuration PostgreSQL NG-Fields
-- À exécuter DANS pgAdmin 4 (Query Tool, connecté en postgres)
-- OU en ligne de commande :
--   psql -U postgres -h 127.0.0.1 -f setup-pgadmin4.sql
-- ============================================================

-- ============================================================
-- ÉTAPE 1 : CRÉER LES RÔLES
-- ============================================================

CREATE ROLE keycloak_user
    WITH LOGIN
         PASSWORD '${KC_DB_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 20;

COMMENT ON ROLE keycloak_user IS 'Keycloak IAM';

CREATE ROLE ng_fields_user
    WITH LOGIN
         PASSWORD '${DB_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 50;

COMMENT ON ROLE ng_fields_user IS 'API Spring Boot';

CREATE ROLE ng_fields_test_user
    WITH LOGIN
         PASSWORD '${DB_TEST_PASSWORD}'
         NOSUPERUSER NOCREATEDB NOCREATEROLE
         CONNECTION LIMIT 10;

COMMENT ON ROLE ng_fields_test_user IS 'Tests intégration';

-- ============================================================
-- ÉTAPE 2 : CRÉER LES BASES
-- REMARQUE : CREATE DATABASE ne peut PAS être dans une transaction
--            Si erreur locale, remplacer par French_France.1252
-- ============================================================

CREATE DATABASE keycloak
    WITH OWNER = keycloak_user
         ENCODING = 'UTF8'
         LC_COLLATE = 'French_France.1252'
         LC_CTYPE = 'French_France.1252'
         TEMPLATE = template0
         CONNECTION LIMIT = 30;

\c keycloak postgres
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON SEQUENCES TO keycloak_user;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- NOTE : Les \c (changement de base) ne marchent pas dans pgAdmin.
-- Exécuter les sections ci-dessous MANUELLEMENT dans la Query Tool
-- après avoir cliqué sur chaque base dans le panneau gauche.
-- ============================================================
-- SUITE : Ouvrir la base ng_fields → Query Tool → exécuter ceci
-- ============================================================

-- REVOKE ALL ON SCHEMA public FROM PUBLIC;
-- REVOKE ALL ON SCHEMA public FROM ng_fields_user;

CREATE SCHEMA IF NOT EXISTS auth         AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS client       AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS intervention AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS notification AUTHORIZATION ng_fields_user;
CREATE SCHEMA IF NOT EXISTS audit        AUTHORIZATION ng_fields_user;

GRANT USAGE, CREATE ON SCHEMA auth         TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA client       TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA intervention TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA notification TO ng_fields_user;
GRANT USAGE, CREATE ON SCHEMA audit        TO ng_fields_user;

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

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";

-- ============================================================
-- SUITE : Ouvrir la base keycloak → Query Tool → exécuter ceci
-- ============================================================

GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON SEQUENCES TO keycloak_user;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- SUITE : Ouvrir la base ng_fields_test → Query Tool → exécuter ceci
-- ============================================================

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
