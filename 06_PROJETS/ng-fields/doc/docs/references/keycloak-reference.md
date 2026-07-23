# Keycloak Reference — NG-Fields

**Version cible :** 26.6.4 (dernière stable, mai 2026)
**Documentation officielle :** https://www.keycloak.org/
**Image Docker :** `quay.io/keycloak/keycloak:26.6.4`

---

## Sommaire

1. [Docker — Démarrage rapide](#1-docker--démarrage-rapide)
2. [Production — Configuration obligatoire](#2-production--configuration-obligatoire)
3. [Container optimisé (multi-stage build)](#3-container-optimisé-multi-stage-build)
4. [Base de données PostgreSQL](#4-base-de-données-postgresql)
5. [Hostname et Reverse Proxy](#5-hostname-et-reverse-proxy)
6. [Endpoints OIDC / OAuth 2.0](#6-endpoints-oidc--oauth-20)
7. [Flux d'authentification supportés](#7-flux-dauthentification-supportés)
8. [Admin Client Java (keycloak-admin-client)](#8-admin-client-java-keycloak-admin-client)
9. [Import / Export des Realms](#9-import--export-des-realms)
10. [Interface de Management (Health, Metrics)](#10-interface-de-management-health-metrics)
11. [Logging](#11-logging)
12. [Sources de configuration](#12-sources-de-configuration)
13. [Rappels sécuritaires NG-Fields](#13-rappels-sécuritaires-ng-fields)

---

## 1. Docker — Démarrage rapide

### Mode développement (NE PAS utiliser en prod)

```bash
docker run -p 127.0.0.1:8080:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.6.4 start-dev
```

- Accès Admin Console : `http://localhost:8080/admin`
- Accès Account Console : `http://localhost:8080/realms/{realm}/account`

### Mode production

```bash
docker run --name keycloak -p 8443:8443 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=change_me \
  quay.io/keycloak/keycloak:26.6.4 start \
    --hostname=auth.ng-fields.ngs.tg \
    --https-certificate-file=/certs/tls.crt \
    --https-certificate-key-file=/certs/tls.key \
    --db=postgres \
    --db-url=jdbc:postgresql://db:5432/keycloak \
    --db-username=keycloak \
    --db-password=change_me
```

---

## 2. Production — Configuration obligatoire

| Option | Valeur recommandée | Description |
|--------|-------------------|-------------|
| `--hostname` | `auth.ng-fields.ngs.tg` | Nom DNS public. Obligatoire. Sécurité : empêche les manipulations de hostname. |
| `--https-certificate-file` | `/certs/tls.crt` | Certificat TLS Let's Encrypt |
| `--https-certificate-key-file` | `/certs/tls.key` | Clé privée du certificat |
| `--proxy-headers` | `xforwarded` | Obligatoire derrière Nginx |
| `--db` | `postgres` | Base de données production (pas `dev-file`) |
| `--features` | `preview` | Si nécessaire pour certaines fonctionnalités |

### Bonnes pratiques de production

1. **TLS obligatoire** — toutes les communications doivent être chiffrées
2. **Hostname explicite** — ne jamais dépendre du `Host` header pour la résolution
3. **Base de données dédiée** — PostgreSQL, pas H2/`dev-file`
4. **Reverse proxy** — Nginx devant Keycloak pour TLS termination
5. **Séparation Admin** — exposer l'Admin Console sur un hostname différent :
   ```bash
   --hostname https://auth.ng-fields.ngs.tg \
   --hostname-admin https://admin-auth.ng-fields.ngs.tg
   ```
6. **Limiter les requêtes en file** : `--http-max-queued-requests=100`

---

## 3. Container optimisé (multi-stage build)

Pour un démarrage rapide en production, pré-build l'image :

```dockerfile
FROM quay.io/keycloak/keycloak:26.6.4 AS builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_DB=postgres

WORKDIR /opt/keycloak

RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 \
  -dname "CN=server" -alias server \
  -ext "SAN:c=DNS:localhost,IP:127.0.0.1" \
  -keystore conf/server.keystore

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.6.4
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KC_DB=postgres
ENV KC_HOSTNAME=auth.ng-fields.ngs.tg

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
```

```bash
docker build -t ng-fields-keycloak:latest .
docker run --name keycloak -p 8443:8443 -p 9000:9000 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=change_me \
  ng-fields-keycloak:latest \
  start --optimized --hostname=auth.ng-fields.ngs.tg
```

### Import d'un realm au démarrage (dev uniquement)

```bash
docker run --name keycloak -p 8080:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v /path/to/realm-export.json:/opt/keycloak/data/import/realm.json \
  quay.io/keycloak/keycloak:26.6.4 \
  start-dev --import-realm
```

### Montée en charge (heap JVM)

```bash
docker run --name keycloak -p 8443:8443 -m 2g \
  -e JAVA_OPTS_KC_HEAP="-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=50" \
  quay.io/keycloak/keycloak:26.6.4 start --optimized
```

Toujours définir `-m` (memory limit). Recommandé : **2 Go minimum** pour la production.

---

## 4. Base de données PostgreSQL

### Configuration minimale

```bash
bin/kc.[sh|bat] start --db postgres \
  --db-url-host=keycloak-postgres \
  --db-username=keycloak \
  --db-password=change_me \
  --hostname=auth.ng-fields.ngs.tg
```

### JDBC URL personnalisée

```bash
bin/kc.[sh|bat] start --db postgres \
  --db-url=jdbc:postgresql://mypostgres:5432/keycloak \
  --db-username=keycloak \
  --db-password=change_me
```

### TLS vers la base de données

```bash
bin/kc.[sh|bat] start --db=postgres \
  --db-tls-mode=verify-server \
  --db-tls-trust-store-file=/path/to/cert.pem
```

### Pool de connexions

| Option | Défaut | Description |
|--------|--------|-------------|
| `--db-pool-initial-size` | — | Taille initiale du pool |
| `--db-pool-max-size` | 100 | Taille max du pool |
| `--db-pool-min-size` | — | Taille min du pool |
| `--db-pool-max-lifetime` | — | Durée de vie max d'une connexion |
| `--db-connect-timeout` | 10s | Timeout de connexion JDBC |

### Versions PostgreSQL testées

PostgreSQL 14, 15, 16, 17, 18 sont supportés. Pour NG-Fields, **PostgreSQL 18**.

---

## 5. Hostname et Reverse Proxy

### Configuration derrière Nginx

```bash
bin/kc.[sh|bat] start \
  --hostname=https://auth.ng-fields.ngs.tg \
  --proxy-headers=xforwarded \
  --http-enabled=true \
  --hostname-strict=true
```

Explications :
- `--hostname` : URL publique complète (inclut scheme, pas de port si 443)
- `--proxy-headers=xforwarded` : lit les headers `X-Forwarded-*` de Nginx
- `--http-enabled=true` : nécessaire car Nginx fait la terminaison TLS
- `--hostname-strict=true` : refuse les hostnames non configurés

### Chemins à exposer sur Nginx

| Chemin Keycloak | Chemin proxy | Exposé ? |
|----------------|--------------|----------|
| `/` | — | NON |
| `/admin/` | — | NON |
| `/realms/` | `/realms/` | OUI |
| `/resources/` | `/resources/` | OUI |
| `/.well-known/` | `/.well-known/` | OUI |
| `/metrics` | — | NON (port 9000) |
| `/health` | — | NON (port 9000) |

### Backchannel dynamique (réseau interne)

```bash
--hostname=https://auth.ng-fields.ngs.tg \
--hostname-backchannel-dynamic=true
```

Les applications peuvent alors joindre Keycloak via le réseau interne Docker.

---

## 6. Endpoints OIDC / OAuth 2.0

Base : `https://auth.ng-fields.ngs.tg/realms/ng-fields`

### Configuration OpenID Discovery

```
GET /realms/{realm}/.well-known/openid-configuration
```

### Endpoints principaux

| Endpoint | Usage |
|----------|-------|
| `GET /protocol/openid-connect/auth` | Authorization endpoint (connexion) |
| `POST /protocol/openid-connect/token` | Token endpoint (échange code, refresh) |
| `GET /protocol/openid-connect/userinfo` | Userinfo endpoint (claims utilisateur) |
| `GET /protocol/openid-connect/logout` | Logout (RP-Initiated) |
| `GET /protocol/openid-connect/certs` | JWKS (clés publiques pour validation JWT) |
| `POST /protocol/openid-connect/token/introspect` | Introspection (validation token) |
| `POST /protocol/openid-connect/revoke` | Revocation de token |

---

## 7. Flux d'authentification supportés

### Authorization Code + PKCE (Angular Web)

Flux recommandé pour l'application Angular :

1. Redirection vers : `GET /realms/ng-fields/protocol/openid-connect/auth?client_id=ng-fields-web&response_type=code&code_challenge=...&code_challenge_method=S256`
2. Login utilisateur sur la page Keycloak
3. Callback avec `code` → échange via `POST /realms/ng-fields/protocol/openid-connect/token`
4. Obtention : `access_token` (JWT, 15 min) + `refresh_token` (7 jours) + `id_token`

### Authorization Code + PKCE (Flutter Mobile)

Même flux, mais :
- `client_id=ng-fields-mobile`
- Redirect URI : `http://127.0.0.1` (port aléatoire, ASWebAuthenticationSession/Chrome Custom Tab)
- Utilise `flutter_appauth` pour gérer le flux

### Client Credentials (M2M — backend → services intermes)

```bash
curl -X POST https://auth.ng-fields.ngs.tg/realms/ng-fields/protocol/openid-connect/token \
  -d "client_id=ng-fields-backend" \
  -d "client_secret=..." \
  -d "grant_type=client_credentials"
```

### Recommandations de sécurité

| Paramètre | Valeur recommandée |
|-----------|-------------------|
| Access Token Lifespan | 15 minutes |
| Refresh Token Lifespan | 7 jours (SSO Session Max) |
| PKCE | Obligatoire pour les clients publics |
| Implicit flow | NE PAS UTILISER (déprécié, supprimé dans OAuth 2.1) |
| Resource Owner Password Credentials | NE PAS UTILISER (déprécié) |

---

## 8. Admin Client Java (keycloak-admin-client)

### Dépendance Maven

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-admin-client</artifactId>
    <version>26.6.4</version>
</dependency>
```

### Utilisation (exemple connexion et récupération realm)

```java
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;

Keycloak keycloak = Keycloak.getInstance(
    "https://auth.ng-fields.ngs.tg",
    "master",
    "admin",
    "password",
    "admin-cli");

RealmRepresentation realm = keycloak.realm("ng-fields").toRepresentation();
```

### Opérations prévues pour NG-Fields

| Opération | Méthode | Endpoint Admin API |
|-----------|---------|-------------------|
| Créer un utilisateur | POST | `/admin/realms/ng-fields/users` |
| Désactiver un compte | PUT | `/admin/realms/ng-fields/users/{id}` |
| Attribuer un rôle | POST | `/admin/realms/ng-fields/users/{id}/role-mappings/realm` |
| Réinitialiser mot de passe | PUT | `/admin/realms/ng-fields/users/{id}/reset-password` |

### Compatibilité

Le client `keycloak-admin-client:26.6.4` est compatible avec le serveur `26.6.4`. Pour éviter les problèmes de compatibilité JSON, initialiser `ObjectMapper` :

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```

---

## 9. Import / Export des Realms

### Export CLI (server arrêté)

```bash
# Export de tous les realms vers un répertoire
bin/kc.[sh|bat] export --dir /tmp/export

# Export d'un realm spécifique
bin/kc.[sh|bat] export --dir /tmp/export --realm ng-fields

# Export avec utilisateurs
bin/kc.[sh|bat] export --dir /tmp/export --users different_files --users-per-file 100

# Export vers un fichier unique
bin/kc.[sh|bat] export --file /tmp/ng-fields-realm.json --realm ng-fields
```

### Import CLI (server arrêté)

```bash
# Import depuis un répertoire
bin/kc.[sh|bat] import --dir /tmp/export

# Import depuis un fichier
bin/kc.[sh|bat] import --file /tmp/ng-fields-realm.json

# Import sans écraser les realms existants
bin/kc.[sh|bat] import --dir /tmp/export --override false
```

### Noms de fichiers pour l'import

- Realm : `<realm-name>-realm.json` (ex: `ng-fields-realm.json`)
- Utilisateurs : `<realm-name>-users-<num>.json`
- Utilisateurs fédérés : `<realm-name>-federated-users-<num>.json`

### Import au démarrage (dev)

```bash
bin/kc.[sh|bat] start --import-realm
```

Les fichiers JSON dans `data/import/` sont importés automatiquement.

### Environnement et placeholders

```json
{
  "realm": "${MY_REALM_NAME}",
  "enabled": true
}
```

---

## 10. Interface de Management (Health, Metrics)

Par défaut sur le port **9000**, séparé du trafic utilisateur.

### Activation

```bash
bin/kc.[sh|bat] start --health-enabled=true --metrics-enabled=true
```

### Endpoints

| Endpoint | Port | Description |
|----------|------|-------------|
| `/health` | 9000 | Health check global |
| `/health/ready` | 9000 | Ready (initialisation terminée) |
| `/health/live` | 9000 | Live (processus en vie) |
| `/metrics` | 9000 | Métriques Prometheus |

### Configuration avancée

```bash
# Changer le port
--http-management-port=9000

# Changer le chemin
--http-management-relative-path=/management

# Forcer HTTP (pas de TLS) sur le management
--http-management-scheme=http
```

### Logging structuré JSON (ECS)

```bash
bin/kc.[sh|bat] start --log-console-output=json --log-console-json-format=ecs
```

Exemple de sortie JSON ECS :

```json
{
  "@timestamp": "2026-06-01T10:32:15.123Z",
  "log.level": "INFO",
  "log.logger": "org.keycloak.events",
  "message": "Login successful",
  "service.name": "keycloak",
  "service.environment": "prod",
  "mdc": {
    "kc.realmName": "ng-fields",
    "kc.clientId": "ng-fields-web"
  }
}
```

---

## 11. Logging

### Handlers disponibles

| Handler | Commande | Usage |
|---------|----------|-------|
| Console | `--log=console` | Défaut, stdout |
| File | `--log=console,file` | Rotation sur disque |
| Syslog | `--log=console,syslog` | Envoi à un serveur Syslog |

### Niveaux de log

```bash
# Root level
--log-level=info

# Par catégorie
--log-level="info,org.hibernate:debug,org.keycloak.events:trace"
```

### JSON pour l'audit trail

```bash
# Activer le MDC (realm, client, userId dans les logs)
--features=log-mdc --log-mdc-enabled=true

# JSON format ECS
--log-console-output=json --log-console-json-format=ecs

# Personnaliser le service name
--log-service-name=ng-fields-keycloak \
--log-service-environment=production
```

### HTTP Access Log

```bash
--http-access-log-enabled=true \
--http-access-log-pattern=combined \
--http-access-log-file-enabled=true \
--http-access-log-file-name=keycloak-http-access \
--http-access-log-file-suffix=.log
```

---

## 12. Sources de configuration

Priorité (de la plus haute à la plus basse) :

1. **Ligne de commande** : `--db-url-host=myhost`
2. **Variables d'environnement** : `KC_DB_URL_HOST=myhost`
3. **Fichier config** : `conf/keycloak.conf` → `db-url-host=myhost`
4. **Java KeyStore** : `kc.db-password=secret`

### Mapping des formats

| Paramètre | CLI | Env var | keycloak.conf | KeyStore |
|-----------|-----|---------|---------------|----------|
| db-url-host | `--db-url-host` | `KC_DB_URL_HOST` | `db-url-host=` | `kc.db-url-host` |
| hostname | `--hostname` | `KC_HOSTNAME` | `hostname=` | `kc.hostname` |
| proxy-headers | `--proxy-headers` | `KC_PROXY_HEADERS` | `proxy-headers=` | `kc.proxy-headers` |

### Variables avec caractères spéciaux (passwords)

Utiliser le préfixe `KCRAW_` pour préserver les `$` :

```bash
export KCRAW_DB_PASSWORD='my$$pa$\{vault}word'
```

### Build vs Runtime

```bash
# Étape 1 : Build (options persistées dans l'image)
bin/kc.[sh|bat] build --db=postgres

# Étape 2 : Start optimisé (runtime uniquement)
bin/kc.[sh|bat] start --optimized --hostname=...
```

Ne jamais stocker de secrets dans les options de build.

---

## 13. Rappels sécuritaires NG-Fields

### Configuration du Realm `ng-fields`

| Client ID | Type | Flux | Usage |
|-----------|------|------|-------|
| `ng-fields-web` | Public | Authorization Code + PKCE | Angular |
| `ng-fields-mobile` | Public | Authorization Code + PKCE | Flutter |
| `ng-fields-backend` | Confidential | Client Credentials | M2M (services intermes) |

### Rôles

| Rôle | Accès |
|------|-------|
| `ADMIN` | Tout |
| `MANAGER` | Dashboard + Notifications + Signature différée + Exports |
| `TECHNICIAN` | Interventions + Photos + Signatures + Sync |
| `CLIENT_ADMIN` | Admin entreprise — gère les utilisateurs de sa société |
| `CLIENT_USER` | Utilisateur entreprise — soumet des demandes d'intervention |
| `CLIENT_VIEWER` | Consultation seule — voit les interventions et rapports |

### Paramètres de sécurité

| Paramètre | Valeur |
|-----------|--------|
| Access Token Lifespan | 15 minutes |
| Refresh Token Lifespan | 7 jours |
| Brute Force Detection | Activé (5 tentatives → 30 min) |
| Require SSL | ALL (production) |
| Login avec PKCE | Obligatoire |
| Politique mot de passe | 10+ char, 1 maj, 1 chiffre, 1 spécial |
| Email vérification | Obligatoire |
| MFA (TOTP) | Obligatoire pour ADMIN et MANAGER |

### Docker Compose NG-Fields (production)

```yaml
keycloak:
  image: quay.io/keycloak/keycloak:26.6.4
  command: start --optimized --import-realm
  environment:
    KC_HOSTNAME: auth.ng-fields.ngs.tg
    KC_HTTPS_CERTIFICATE_FILE: /opt/keycloak/conf/tls.crt
    KC_HTTPS_CERTIFICATE_KEY_FILE: /opt/keycloak/conf/tls.key
    KC_DB: postgres
    KC_DB_URL: jdbc:postgresql://db:5432/keycloak
    KC_DB_USERNAME: ${KC_DB_USER}
    KC_DB_PASSWORD: ${KC_DB_PASSWORD}
    KC_BOOTSTRAP_ADMIN_USERNAME: ${KC_ADMIN}
    KC_BOOTSTRAP_ADMIN_PASSWORD: ${KC_ADMIN_PASSWORD}
    KC_PROXY: edge
    KC_HEALTH_ENABLED: "true"
    KC_METRICS_ENABLED: "true"
    KC_LOG_LEVEL: info
    KC_LOG_CONSOLE_OUTPUT: json
    KC_LOG_CONSOLE_JSON_FORMAT: ecs
    KC_FEATURES: log-mdc
  volumes:
    - ./keycloak/realm-ng-fields.json:/opt/keycloak/data/import/realm.json
    - ./certs:/opt/keycloak/conf/tls.crt
  ports:
    - "8443:8443"
  deploy:
    resources:
      limits:
        memory: 2g
```

---

*Document généré le 01/06/2026 à partir de la documentation officielle Keycloak 26.6.4*
*Sources : https://www.keycloak.org/documentation*
