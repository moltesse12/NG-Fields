# Base keycloak — Configuration PostgreSQL

## 1. Informations générales

| Propriété      | Valeur                                                |
| -------------- | ----------------------------------------------------- |
| Nom de la base | `keycloak`                                            |
| Propriétaire   | `keycloak_user`                                       |
| Type           | Base dédiée — Keycloak gère ses propres tables (~80)  |
| Driver JDBC    | Déjà inclus : `org.postgresql.postgresql-42.7.11.jar` |
| Schéma utilisé | `public` (par défaut Keycloak)                        |

## 2. Rôle dédié

```sql
CREATE ROLE keycloak_user
    WITH LOGIN
         PASSWORD '${KC_DB_PASSWORD}'
         NOSUPERUSER
         NOCREATEDB
         NOCREATEROLE
         CONNECTION LIMIT 20;

COMMENT ON ROLE keycloak_user IS 'Rôle dédié à Keycloak — accès exclusif à la base keycloak';
```

## 3. Création de la base

```sql
CREATE DATABASE keycloak
    WITH OWNER      = keycloak_user
         ENCODING   = 'UTF8'
         LC_COLLATE = 'French_France.1252'
         LC_CTYPE   = 'French_France.1252'
         TEMPLATE   = template0
         CONNECTION LIMIT = 30;

COMMENT ON DATABASE keycloak IS 'Base dédiée à Keycloak IAM — ne pas modifier manuellement';
```

## 4. Droits

```sql
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT ALL PRIVILEGES ON SEQUENCES TO keycloak_user;
```

## 5. Connexion Keycloak → PostgreSQL

Keycloak 26.6.2 (distribution Quarkus) se configure via variables d'environnement :

```powershell
# Dans PowerShell, AVANT de lancer Keycloak
$env:KC_DB          = "postgres"
$env:KC_DB_URL      = "jdbc:postgresql://localhost:5432/keycloak"
$env:KC_DB_USERNAME = "keycloak_user"
$env:KC_DB_PASSWORD = "${KC_DB_PASSWORD}"
```

Ou via le fichier `conf/keycloak.conf` (équivalent YAML) :

```properties
db=postgres
db-url=jdbc:postgresql://localhost:5432/keycloak
db-username=keycloak_user
db-password=${KC_DB_PASSWORD}
```

## 6. Démarrage

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps\keycloak-26.6.2\bin
.\kc.bat start-dev --http-port=8080
```

> **Important :** Au premier démarrage, Keycloak exécute son schéma Liquibase.
> ~80 tables sont créées automatiquement. Ne pas interrompre (2-3 minutes).

## 7. Vérification

```powershell
# Connexion directe à la base keycloak
psql -U keycloak_user -h localhost -d keycloak -c "\dt"
# Résultat attendu : ~80 tables (EVENTS, USER_ENTITY, REALM, etc.)
```

## 8. Fichiers d'export du realm

Les fichiers d'export du realm `ng-fields` se trouvent dans :

```
docs/keycloak/
├── ng-fields-realm.json       ← Realm + clients + rôles
└── ng-fields-users-0.json     ← Utilisateurs de test
```

## 9. Nettoyage

Après migration vers PostgreSQL, l'ancienne base H2 peut être supprimée :

```powershell
Remove-Item -Recurse -Force "F:\...\keycloak-26.6.2\data\h2"
```
