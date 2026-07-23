# 02 — Configurer le Realm Keycloak NG-Fields

**Objectif :** Realm `ng-fields` avec clients OIDC + rôles RBAC opérationnels
**Temps estimé :** 15 minutes
**Dépend de :** [01-setup-keycloak.md](01-setup-keycloak.md) (Keycloak tourne sur `localhost:8080`)

---

## Vue d'ensemble

Ce qu'on va créer dans Keycloak :

```
Realm: ng-fields
├── Clients OIDC
│   ├── ng-fields-web       (public, PKCE, Angular)
│   ├── ng-fields-mobile    (public, PKCE, Flutter)
│   └── ng-fields-backend   (confidential, service account)
├── Realm Roles
│   ├── ADMIN               (accès complet)
│   ├── MANAGER             (dashboard, exports)
│   ├── TECHNICIAN          (interventions terrain)
│   ├── CLIENT_ADMIN        (admin entreprise multi-utilisateurs)
│   ├── CLIENT_USER         (utilisateur entreprise, soumission demandes)
│   └── CLIENT_VIEWER       (consultation seule)
└── Utilisateurs de test
    ├── admin / Admin123!
    ├── tech1 / Tech123!
    ├── manager1 / Mgr123!
    ├── clientadmin1 / ClientAdmin123!
    └── clientuser1 / ClientUser123!
```

---

## Étape 1 : Créer le Realm

1. Aller sur **http://localhost:8080/admin**
2. Cliquer sur **Create Realm** dans le panneau de gauche
3. Remplir :

   | Champ | Valeur |
   |-------|--------|
   | Realm name | `ng-fields` |
   | Enabled | ON |
   | Display name | `NG-Fields` |
   | HTML Display name | `NG-Fields` |

4. Cliquer **Create**

## Étape 2 : Configurer la sécurité du Realm

Aller dans **Realm Settings → Security Defenses** :

### Brute Force Detection

| Champ                          | Valeur                            |
| ------------------------------ | --------------------------------- |
| Brute Force Detection          | <font color="#00b050">ON</font>   |
| Failure Factor                 | <font color="#00b050">5</font>    |
| Wait Increment Seconds         | `1800` (30 min)                   |
| Quick Login Check Milliseconds | <font color="#00b050">1000</font> |
| Max Delta Time Seconds         | `43200` (12 h)                    |
|                                |                                   |

### Politique de mot de passe

Aller dans **Authentication → Password Policy** → ajouter :

| Politique            | Valeur |
| -------------------- | ------ |
| Minimum Length       | `8`    |
| Uppercase Characters | `1`    |
| Digits               | `1`    |
| Special Characters   | `1`    |
| Not Username         | ON     |


### Durée des tokens

Aller dans **Realm Settings → Tokens** :

| Paramètre                               | Valeur       |
| --------------------------------------- | ------------ |
| Access Token Lifespan                   | `15 Minutes` |
| Access Token Lifespan for Implicit Flow | `15 Minutes` |
| Client Login Timeout                    | `5 Minutes`  |
| User Initiated Action Lifespan          | `5 Minutes`  |

### Durée des Sessions

Aller dans **Realm Settings → Sessions:

| Paramètre                   | Valeur      |
| --------------------------- | ----------- |
| SSO Session Idle            | `1 Day`     |
| SSO Session Max             | `7 Days`    |
| Client Session Idle         | `1 Day`     |
| Client Session Max          | `7 Days`    |
| Offline Session Idle        | `30 Days`   |
| Offline Session Max Limited | ON          |
| Offline Session Max         | `30 Days`   |
| Login timeout               | `5 Minutes` |
| Login action timeout        | `5 Minutes` |

## Étape 3 : Créer les clients OIDC

### Client 1 : `ng-fields-web` (Angular, public, PKCE)

Aller dans **Clients → Create client**.

**Onglet General :**

| Champ                                | Valeur                                                 |
| ------------------------------------ | ------------------------------------------------------ |
| Client ID                            | `ng-fields-web`                                        |
| Name                                 | `NG-Fields Web App`                                    |
| Description                          | `Application web Angular (portail client + dashboard)` |
| Client authentication                | OFF (public)                                           |
| Authorization                        | OFF                                                    |
| Standard flow                        | **ON**                                                 |
| Direct access grants                 | OFF                                                    |
| Implicit flow                        | OFF                                                    |
| Service account roles                | OFF                                                    |
| Standard Token Exchange              | OFF                                                    |
| JWT Authorization Grant              | OFF                                                    |
| OAuth 2.0 Device Authorization Grant | OFF                                                    |
| OIDC CIBA Grant                      | OFF                                                    |
| Require PKCE                         | **ON**                                                 |
| Require DPoP bound tokens            | OFF                                                    |
| Root URL                             | *(laisser vide)*                                       |
| Home URL                             | *(laisser vide)*                                       |
| Valid redirect URIs                  | `http://localhost:4200/*`                              |
| Valid post logout redirect URIs      | `http://localhost:4200/*`                              |
| Web origins                          | `http://localhost:4200`                                |


**Onglet Advanced :**

| Champ | Valeur |
|-------|--------|
| Proof Key for Code Exchange Code Challenge Method | `S256` |

### Client 2 : `ng-fields-backend` (Spring Boot, M2M, confidential)

Aller dans **Clients → Create client**.

**Onglet General :**

| Champ                                | Valeur                                                            |
| ------------------------------------ | ----------------------------------------------------------------- |
| Client ID                            | `ng-fields-backend`                                               |
| Name                                 | `NG-Fields Backend API`                                           |
| Description                          | `Backend Spring Boot (communication M2M avec client_credentials)` |
| Client authentication                | **ON** (confidential)                                             |
| Authorization                        | OFF                                                               |
| Standard flow                        | OFF                                                               |
| Direct access grants                 | OFF                                                               |
| Implicit flow                        | OFF                                                               |
| Service account roles                | **ON**                                                            |
| Standard Token Exchange              | OFF                                                               |
| JWT Authorization Grant              | OFF                                                               |
| OAuth 2.0 Device Authorization Grant | OFF                                                               |
| OIDC CIBA Grant                      | OFF                                                               |
| Require PKCE                         | OFF                                                               |
| Require DPoP bound tokens            | OFF                                                               |
| Root URL                             | *(laisser vide)*                                                  |
| Home URL                             | *(laisser vide)*                                                  |
| Valid redirect URIs                  | *(laisser vide)*                                                  |
| Valid post logout redirect URIs      | *(laisser vide)*                                                  |
| Web origins                          | *(laisser vide)*                                                  |

> **Note :** Pas d'URL de redirection — le backend n'a pas de navigateur. Il utilise le flow `client_credentials` (M2M).

Dans l'onglet **Credentials** :
- Copier le `Client secret` (on en aura besoin plus tard)

### Client 3 : `ng-fields-mobile` (Flutter, public, PKCE)

Aller dans **Clients → Create client**.

**Onglet General :**

| Champ                                | Valeur                                                           |
| ------------------------------------ | ---------------------------------------------------------------- |
| Client ID                            | `ng-fields-mobile`                                               |
| Name                                 | `NG-Fields Mobile App`                                           |
| Description                          | `Application mobile Flutter (interventions terrain techniciens)` |
| Client authentication                | OFF (public)                                                     |
| Authorization                        | OFF                                                              |
| Standard flow                        | **ON**                                                           |
| Direct access grants                 | OFF                                                              |
| Implicit flow                        | OFF                                                              |
| Service account roles                | OFF                                                              |
| Standard Token Exchange              | OFF                                                              |
| JWT Authorization Grant              | OFF                                                              |
| OAuth 2.0 Device Authorization Grant | OFF                                                              |
| OIDC CIBA Grant                      | OFF                                                              |
| Require PKCE                         | **ON**                                                           |
| Require DPoP bound tokens            | OFF                                                              |
| Root URL                             | *(laisser vide)*                                                 |
| Home URL                             | *(laisser vide)*                                                 |
| Valid redirect URIs                  | `http://127.0.0.1/*`                                             |
| Valid post logout redirect URIs      | `http://127.0.0.1/*`                                             |
| Web origins                          | `+`                                                              |

**Onglet Advanced :**

| Champ | Valeur |
|-------|--------|
| Proof Key for Code Exchange Code Challenge Method | `S256` |

## Étape 4 : Créer les Realm Roles

Aller dans **Realm Roles → Create role** :

### Rôle ADMIN

| Champ       | Valeur                                  |
| ----------- | --------------------------------------- |
| Role Name   | `ADMIN`                                 |
| Description | `Full access + Keycloak administration` |

### Rôle MANAGER

| Champ       | Valeur                                                  |
| ----------- | ------------------------------------------------------- |
| Role Name   | `MANAGER`                                               |
| Description | `Dashboard, notifications, exports, signature différée` |

### Rôle TECHNICIAN

| Champ       | Valeur                                                       |
| ----------- | ------------------------------------------------------------ |
| Role Name   | `TECHNICIAN`                                                 |
| Description | `Interventions terrain, photos, signatures, synchronisation` |

### Rôle CLIENT_ADMIN

| Champ       | Valeur                                            |
| ----------- | ------------------------------------------------- |
| Role Name   | `CLIENT_ADMIN`                                    |
| Description | `Admin entreprise — gère les utilisateurs de sa société` |

### Rôle CLIENT_USER

| Champ       | Valeur                                            |
| ----------- | ------------------------------------------------- |
| Role Name   | `CLIENT_USER`                                     |
| Description | `Utilisateur entreprise — soumet des demandes d'intervention` |

### Rôle CLIENT_VIEWER

| Champ       | Valeur                                            |
| ----------- | ------------------------------------------------- |
| Role Name   | `CLIENT_VIEWER`                                   |
| Description | `Consultation seule — voit les interventions et rapports` |

## Étape 5 : Créer les utilisateurs de test

Aller dans **Users → Add user**.

### Utilisateur admin

| Champ                 | Valeur                 |
| --------------------- | ---------------------- |
| Username              | `admin`                |
| Email                 | `admin@ng-fields.test` |
| First name            | `admin`                |
| Last name             | `NG-Fields`            |
| Email verified        | **ON**                 |
| Required User Actions |                        |
| Temporarily locked    | OFF                    |

Cliquer **Create**, puis aller dans l'onglet **Credentials** :

| Champ     | Valeur                                             |
| --------- | -------------------------------------------------- |
| Password  | `Admin123!`                                        |
| Temporary | OFF (l'utilisateur devra changer au premier login) |

Aller dans l'onglet **Role mapping → Assign role** :

| Champ                       | Valeur  |
| --------------------------- | ------- |
| Assign Realm roles to admin | `ADMIN` |

### Utilisateur tech1

**General :**

| Champ          | Valeur                 |
| -------------- | ---------------------- |
| Username       | `tech1`                |
| Email          | `tech1@ng-fields.test` |
| First name     | `Toussain`             |
| Last name      | `KODAH`                |
| Email verified | **ON**                 |

**Credentials :** `Tech123!` (temporaire : OFF)
**Role mapping :** `TECHNICIAN`

### Utilisateur manager1

**General :**

| Champ          | Valeur                    |
| -------------- | ------------------------- |
| Username       | `manager1`                |
| Email          | `manager1@ng-fields.test` |
| First name     | `Manager`                 |
| Last name      | `NG-Fields`               |
| Email verified | **ON**                    |

**Credentials :** `Mngr123!` (temporaire : ON)
**Role mapping :** `MANAGER`

### Utilisateur clientadmin1

**General :**

| Champ          | Valeur                         |
| -------------- | ------------------------------ |
| Username       | `clientadmin1`                 |
| Email          | `clientadmin1@test.com`        |
| First name     | `Admin`                        |
| Last name      | `Entreprise`                   |
| Email verified | **ON**                         |

**Credentials :** `ClientAdmin123!` (temporaire : ON)
**Role mapping :** `CLIENT_ADMIN`

### Utilisateur clientuser1

**General :**

| Champ          | Valeur                         |
| -------------- | ------------------------------ |
| Username       | `clientuser1`                  |
| Email          | `clientuser1@test.com`         |
| First name     | `User`                         |
| Last name      | `Entreprise`                   |
| Email verified | **ON**                         |

**Credentials :** `ClientUser123!` (temporaire : ON)
**Role mapping :** `CLIENT_USER`

## Étape 6 : Vérifier que tout fonctionne

### 6.1 Récupérer le secret du client backend

1. Aller dans **Admin Console → Clients → `ng-fields-backend` → Credentials**
2. Cliquer **Regenerate Secret**
3. Copier la valeur (elle servira pour tous les tests)

> Garder ce secret pour plus tard. Il sera aussi nécessaire dans la config Spring Boot (guide 03).
> Les tests ci-dessous s'exécutent dans **n'importe quel terminal PowerShell** (pas besoin d'être dans un dossier précis).
### 6.2 Test 1 : Flow client_credentials (M2M)

Dans Postman, créer une nouvelle requête :

```
POST http://localhost:8080/realms/ng-fields/protocol/openid-connect/token
```

**Body (x-www-form-urlencoded) :**

| Key             | Value                |
| --------------- | -------------------- |
| `client_id`     | `ng-fields-backend`  |
| `client_secret` | `**********`         |
| `grant_type`    | `client_credentials` |

**Résultat attendu :** `200 OK` avec une réponse JSON contenant :
```json
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxVHg5RmQ5RFNiMzFyMjBzV3dsVXQ0bExMU3poVUl6Z1ZqR2NTZm5IMlVJIn0.eyJleHAiOjE3ODA0OTE3MTcsImlhdCI6MTc4MDQ5MDgxNywianRpIjoidHJydGNjOmM0OTc3YjQwLTYwMTgtOTdiYi1jZmI1LTg1ZTMyNTRlYzA2NyIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvbmctZmllbGRzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6Ijc2Y2U5NmNjLWMyZWUtNDg3Yy1hNWIxLWM1ODFkMDZhMDA4NCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im5nLWZpZWxkcy1iYWNrZW5kIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtbmctZmllbGRzIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiY2xpZW50SG9zdCI6IjEyNy4wLjAuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LW5nLWZpZWxkcy1iYWNrZW5kIiwiY2xpZW50QWRkcmVzcyI6IjEyNy4wLjAuMSIsImNsaWVudF9pZCI6Im5nLWZpZWxkcy1iYWNrZW5kIn0.ixorYmcGpyCyfDODoeDjO8GjDi5T4zzJBHCyZBSc_Wt78-dsgUcQyZQ8fBLNOBlYkMB2H4Wij6dFjCkLmdRB9iFnEcF2cFfLnO029c5S3d81S8MgO940G7Q8elVfxQD2wY9kfZbNNE2uw6zTGjDwW7NNy96mUEpU0-QDDg_kmf6Ki5QDFlaxMRaDr6j-1e_rhl6RBCmfmexOrj_bRY8UMsl8UpnDdIdreZtB0HdIBjLER3JjUQy6qNIWtRpIwZY_B-9774xWibgKzaaP-K9gMBUOnNNCKpaVgTSe5_5Ei8AGtqpTwfhttGIGjqR-C2nwCWNyMBWk75LxtTVyUg8-5w","expires_in":900,"refresh_expires_in":0,"token_type":"Bearer","not-before-policy":0,"scope":"email profile"}
```

### 6.3 Test 2 : Flow password (utilisateur réel)

> **Note :** On utilise le client `ng-fields-backend` (pas `ng-fields-web`) car Angular a `Direct access grants: OFF` pour des raisons de sécurité. Seul le backend peut utiliser le flow `password` avec un `client_secret`.

Nouvelle requête Postman :

```
POST http://localhost:8080/realms/ng-fields/protocol/openid-connect/token
```

**Body (x-www-form-urlencoded) :**

| Key             | Value               |
| --------------- | ------------------- |
| `client_id`     | `ng-fields-backend` |
| `client_secret` | `**********`        |
| `grant_type`    | `password`          |
| `username`      | `admin`             |
| `password`      | `Admin123!`         |

**Résultat attendu :** `200 OK` avec un `access_token` valide.
```json
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxVHg5RmQ5RFNiMzFyMjBzV3dsVXQ0bExMU3poVUl6Z1ZqR2NTZm5IMlVJIn0.eyJleHAiOjE3ODA0OTE3NTcsImlhdCI6MTc4MDQ5MDg1NywianRpIjoib25ydHJvOmVjOTk0MTNmLWMxYTEtZGZjMi02ZGQ3LTI0ZTUwOTk4NGRmNyIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvbmctZmllbGRzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjZmOGZiNWZmLTMwNGQtNDkyZC1hZDhmLTA0MmFlNTk3M2Y1ZCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im5nLWZpZWxkcy1iYWNrZW5kIiwic2lkIjoiU1FXOTh1ZEFjTFotZ2hqYmxnQjJGLWgwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIkFETUlOIiwiZGVmYXVsdC1yb2xlcy1uZy1maWVsZHMiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6ImFkbWluIE5HLUZpZWxkcyIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIiwiZ2l2ZW5fbmFtZSI6ImFkbWluIiwiZmFtaWx5X25hbWUiOiJORy1GaWVsZHMiLCJlbWFpbCI6ImFkbWluQG5nLWZpZWxkcy50ZXN0In0.iDixV8DteMy4VpMwfiaeG6XFABcvezZdEyJw_0TBmiMajGxxtctLgZ30NmMBKEoGiHW-GreU3dTN5ELt8HYdR3zHN-Smk4ueQDrynmzDs5kaeyl--RskTO24vL5p1dzSQN8mom6AUrKwJUWlb9RSfoxfau-s9jxaWDYlhh3Ofl3j_hx_8PUda-eWkBdvTtpeIc2osDeQVljyvCOaZXIpbyTjlF13pog7nUbfbAW4GEW7e7fMyG6Qpift-1qC_ohOxO1fKdp1SuIaNy-yoTHzOlTTjSN5JgyM68zd_bPWR8p-0-EknEMRZOHK7o9dW6h-fXCS9EUBl4yakfzaMrNzHw","expires_in":900,"refresh_expires_in":86400,"refresh_token":"eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiNmE2M2Y5OS01NTBmLTQxODItOTRhZi1lMDM0MzdkOTE2YTAifQ.eyJleHAiOjE3ODA1NzcyNTcsImlhdCI6MTc4MDQ5MDg1NywianRpIjoiNDkzNDA5MzUtMThlZi1jNGI1LTM3NGMtODM4MTBmN2JmNGE2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9uZy1maWVsZHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL25nLWZpZWxkcyIsInN1YiI6IjZmOGZiNWZmLTMwNGQtNDkyZC1hZDhmLTA0MmFlNTk3M2Y1ZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJuZy1maWVsZHMtYmFja2VuZCIsInNpZCI6IlNRVzk4dWRBY0xaLWdoamJsZ0IyRi1oMCIsInNjb3BlIjoiZW1haWwgd2ViLW9yaWdpbnMgcHJvZmlsZSByb2xlcyBzZXJ2aWNlX2FjY291bnQgYWNyIGJhc2ljIiwiYXVkX3giOiJhY2NvdW50In0.LFrNjKWZViW5Ofs86xK_yDmYsoAqHKGAK2yYQyc0QWZDOtYhlbmnTw2raJ1Xul9XRRCBfaSnmEiUcg8cg0j6hQ","token_type":"Bearer","not-before-policy":0,"session_state":"SQW98udAcLZ-ghjblgB2F-h0","scope":"email profile"}
```
### 6.4 Décoder le JWT pour vérifier les rôles

Dans Postman, cliquer sur l'onglet **Cookies** → coller le token dans [jwt.io](https://jwt.io) ou utiliser le script **Tests** :

```javascript
// Onglet Tests de Postman
var token = pm.response.json().access_token;
var payload = JSON.parse(atob(token.split('.')[1]));
console.log(payload);
pm.collectionVariables.set("access_token", token);
```

**Vérifier que le payload contient :**
```json
{
  "iss": "http://localhost:8080/realms/ng-fields",
  "realm_access": { "roles": ["ADMIN"] },
  "preferred_username": "admin"
}
```

### 6.5 Appeler l'API protégée avec le token

Nouvelle requête Postman :

```
GET http://localhost:8081/api/public/health
```

**Headers :**

| Key           | Value                              |
|---------------|------------------------------------|
| Authorization | `Bearer {{access_token}}`          |

> Le token est stocké dans une variable collection via le script Tests plus haut.

### 6.6 Vérifier les utilisateurs dans l'Admin Console

1. Aller dans **Admin Console → Realm `ng-fields` → Users → View all users**
2. Vérifier que `admin`, `tech1`, `manager1`, `clientadmin1`, `clientuser1` apparaissent
3. Cliquer sur chaque utilisateur → **Role mapping** → vérifier que le bon rôle est assigné

## Étape 7 : Exporter le Realm (pour Git)

```powershell
cd F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\apps\keycloak-26.6.4\bin

# Arrêter Keycloak (Ctrl+C dans la console), puis :
.\kc.bat export --dir F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\Projet_NG-Fields\docs\keycloak --realm ng-fields
```

> **Note :** Si Keycloak ne s'arrête pas proprement, tuer le processus puis relancer l'export.

---

## Résumé des credentials

| Composant | URL | Identifiants |
|-----------|-----|-------------|
| Admin Console | `http://localhost:8080/admin` | `admin` / `Admin123!` |
| Token endpoint | `http://localhost:8080/realms/ng-fields/protocol/openid-connect/token` | — |
| JWKS endpoint | `http://localhost:8080/realms/ng-fields/protocol/openid-connect/certs` | — |
| Backend secret | Dans Clients → ng-fields-backend → Credentials | — |

## Prochaine étape

Le realm est prêt avec clients, rôles et utilisateurs. Passer à la sécurisation de Spring Boot :

➡️ [03-spring-security.md](03-spring-security.md)
