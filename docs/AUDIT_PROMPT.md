# PROMPT D'AUDIT COMPLET — Stack Keycloak + Spring Boot + Angular + Flutter

**Contexte :** PC sans virtualisation fiable (pas de VM, Docker son blocker)
**Objectif :** Audit professionnel niveau entreprise — sécurité, standards, performance, maintenabilité, design ui/ux , maintenabiliters ...

---

## CONTEXTE TECHNIQUE

- **Projet** : NG-Fields — application de gestion d'interventions terrain
- **IAM** : Keycloak **26.6.2** — hébergé sur :
  - Dev : `http://localhost:9090`
  - ~~Prod : `https://auth.ng-fields.ngs.tg`~~
  - ~~Staging : `https://auth-staging.ng-fields.ngs.tg`~~
- **Backend** : Spring Boot microservices (5 services)
  - Java **25** / Spring Boot **4.0.6** / Spring Security **6.x**
  - Communication : REST synchrone (Feign/WebClient — à confirmer)
  - Base de données : **PostgreSQL 16** via Supabase (schémas : auth, client, intervention, notification, audit)
  - Cache : **Redis 7-alpine**
- **Microservices :**
  | Service | Port | Rôle |
  |---|---|---|
  | `gateway-service` | 8080 | API Gateway — point d'entrée unique |
  | `auth-service` | 8081 | Authentification, users, admin Keycloak |
  | `client-service` | 8082 | CRUD clients & historique |
  | `intervention-service` | 8083 | Interventions, photos, signatures, PDF, GPS |
  | `notification-service` | 8084 | Emails centralisés via Resend |
  | `shared-auth-lib` | — | Librairie partagée JWT/OAuth2 |
- **Frontend Web** : **Angular 21.2.x** (TypeScript 5.9, vitest) — `apps/ng-fields-web/`
- **Mobile** : **Flutter ^3.5.0** (Dart) — `apps/mobile/` — non encore développé
- **CI/CD** : GitHub Actions (backend.yml, mobile.yml)
- **Environnement** : Pas de virtualisation disponible (audit via fichiers de config, CLI, curl, logs directs)

---

## CONTRAINTE PRINCIPALE

Aucune VM ni Docker pour les tests d'intégration. Tous les checks doivent être réalisables via :

- Lecture/analyse de fichiers de configuration
- Commandes CLI (curl, jq, openssl, keytool, mvn, ng, dart/flutter)
- Appels directs aux endpoints déployés
- Analyse statique du code source

---

## MISSION : AUDIT COMPLET EN 6 AXES

### AXE 1 — KEYCLOAK (IAM & Sécurité des identités)

Génère un checklist exhaustif avec commandes curl/CLI pour vérifier :

**Configuration Realm**

- [ ] Realm dédié créé (pas de realm "master" en production)
- [ ] Brute-force protection activée (max tentatives, délai de verrouillage)
- [ ] Politique de mots de passe (longueur min, complexité, historique)
- [ ] SSL/TLS requis (mode "all" ou "external")
- [ ] Timeout de session (SSO, idle, access token lifespan)

**Clients OAuth2**

- [ ] Type de grant correct par client (authorization_code + PKCE pour SPA/mobile, client_credentials pour M2M)
- [ ] PKCE activé (code_challenge_method=S256) pour Angular et Flutter
- [ ] Redirect URIs restrictives (pas de wildcards `*`)
- [ ] Web origins configurés correctement (CORS)
- [ ] Client secrets non exposés côté frontend
- [ ] Refresh token rotation activée
- [ ] Scopes minimaux (principe du least privilege)

**Rôles & Autorisations**

- [ ] Hiérarchie de rôles cohérente (realm roles vs client roles)
- [ ] Mappers de tokens configurés (claims personnalisés)
- [ ] Groupes utilisateurs organisés logiquement
- [ ] Service accounts sécurisés pour les microservices

**Tokens**

- [ ] Access token lifespan ≤ 5 min (recommandé : 1-2 min)
- [ ] Refresh token lifespan cohérent avec l'UX
- [ ] Algorithme de signature RS256 ou ES256 (jamais HS256 en production)
- [ ] JWKS endpoint accessible et certifié

Fournis les commandes curl exactes pour vérifier chaque point via l'Admin REST API de Keycloak.

---

### AXE 2 — SPRING BOOT MICROSERVICES (Sécurité & Standards)

Pour chaque microservice, vérifier :

**Sécurité JWT**

- [ ] Validation de la signature JWT (spring-security-oauth2-resource-server)
- [ ] Validation de l'audience (aud claim) et de l'issuer (iss)
- [ ] Validation de l'expiration (exp)
- [ ] Extraction correcte des rôles depuis les claims Keycloak
- [ ] Endpoints protégés vs publics — matrice d'autorisation documentée

**API Design & Standards REST**

- [ ] Codes HTTP corrects (201 vs 200, 404 vs 500, 400 vs 422)
- [ ] Gestion globale des exceptions (GlobalExceptionHandler / @ControllerAdvice)
- [ ] Format d'erreur standardisé (RFC 7807 Problem Details)
- [ ] Validation des inputs (@Valid, @NotNull, contraintes personnalisées)
- [ ] DTOs séparés des entités (pas d'exposition directe des entités JPA)
- [ ] Pagination sur tous les endpoints liste

**API Gateway**

- [ ] Routing correct vers chaque microservice
- [ ] Rate limiting configuré (par IP, par user, par client)
- [ ] Propagation du token JWT vers les services downstream
- [ ] Gestion des headers de sécurité (X-Frame-Options, CSP, HSTS)
- [ ] Circuit breaker configuré (Resilience4j)

**Observabilité**

- [ ] Spring Boot Actuator sécurisé (endpoints sensibles protégés)
- [ ] Logs structurés (JSON) avec ID de corrélation (MDC)
- [ ] Health checks (/health) corrects pour chaque service
- [ ] Métriques exposées pour Prometheus (ou équivalent)

**Dépendances & Build**

- [ ] Aucune dépendance avec CVE critique (mvn dependency-check ou équivalent)
- [ ] Pas de credentials dans le code source (git grep)
- [ ] Variables d'environnement / Vault pour les secrets
- [ ] Profils Spring correctement séparés (dev/staging/prod)

Fournis les commandes exactes (curl, grep, mvn) pour chaque vérification.

---

### AXE 3 — ANGULAR (Sécurité & Standards Frontend)

**Authentification OAuth2/OIDC**

- [ ] Bibliothèque OIDC correcte (angular-oauth2-oidc ou auth0/angular)
- [ ] PKCE implémenté (code_verifier / code_challenge côté client)
- [ ] Tokens stockés en mémoire (pas dans localStorage pour l'access token)
- [ ] Refresh token géré correctement (rotation, stockage sécurisé)
- [ ] Déconnexion propre (révocation du token + clear state)

**Guards & Intercepteurs**

- [ ] AuthGuard sur toutes les routes protégées
- [ ] Intercepteur HTTP qui injecte le Bearer token
- [ ] Gestion des 401 (redirect vers login) et 403 (page d'erreur)
- [ ] Intercepteur de refresh token transparent

**Sécurité XSS / CSRF**

- [ ] Pas de innerHTML / bypassSecurityTrust* non justifié
- [ ] HttpClient utilisé (pas de fetch natif sans sanitisation)
- [ ] CSP headers configurés côté serveur (vérifier les headers de réponse)

**Performance & Maintenabilité**

- [ ] Lazy loading des modules
- [ ] OnPush change detection sur les composants critiques
- [ ] Pas de subscriptions non unsubscribed (memory leaks)
- [ ] Bundle size analysé (ng build --stats-json + webpack-bundle-analyzer)
- [ ] Environnements séparés (environment.ts vs environment.prod.ts)
- [ ] Version Angular à jour (ou plan de migration documenté)

---

### ~~AXE 4 — FLUTTER (Audit préventif — avant développement)~~

~~Établit les standards à respecter dès le début :~~

~~**Architecture recommandée**~~

- [ ] ~~Pattern d'état global défini (Bloc, Riverpod, ou Provider)~~
- [ ] ~~Séparation couches : presentation / domain / data~~
- [ ] ~~Repository pattern pour les appels API~~

~~**Sécurité mobile**~~

- [ ] ~~flutter_secure_storage pour les refresh tokens (pas SharedPreferences)~~
- [ ] ~~Dio + intercepteur pour JWT (injection + refresh automatique)~~
- [ ] ~~Certificate pinning planifié pour la production~~
- [ ] ~~Obfuscation activée pour le build release (--obfuscate --split-debug-info)~~
- [ ] ~~Pas de logs sensibles en production (debugPrint désactivé)~~

~~**OAuth2/OIDC Mobile**~~

- [ ] ~~PKCE obligatoire (flutter_appauth)~~
- [ ] ~~Deep linking sécurisé pour le callback OAuth2~~
- [ ] ~~Vérification de l'état (state param) dans le flow OAuth2~~

---

### AXE 5 — SÉCURITÉ TRANSVERSALE

**Transport & Certificats**

- [ ] TLS 1.2+ partout (vérifier via : `openssl s_client -connect host:port`)
- [ ] Certificats valides et non expirés
- [ ] HSTS activé
- [ ] Headers de sécurité HTTP présents sur tous les services

**Secrets & Configuration**

- [ ] Aucun secret en dur dans le code (git log --all -S 'password' ou trufflehog)
- [ ] .gitignore couvre les fichiers de config sensibles
- [ ] Variables d'environnement documentées (fichier .env.example)

**Logging & Audit**

- [ ] Logs des connexions / déconnexions (Keycloak events)
- [ ] Logs des erreurs 4xx/5xx avec ID de corrélation
- [ ] Pas de données personnelles dans les logs (RGPD)

---

### AXE 6 — PLAN DE RAPPORT D'AUDIT

Pour chaque point vérifié, génère un tableau avec :

| Composant | Point vérifié | Statut | Sévérité | Recommandation | Commande de vérification |
|---|---|---|---|---|---|

Puis génère :

1. **Résumé exécutif**  — niveau de maturité global
2. **Top 5 risques critiques** à corriger immédiatement
3. **Roadmap de remédiation** sur 3 sprints (court / moyen / long terme)
4. **Score de conformité** par couche (sur 10)

---

## FORMAT DE RÉPONSE ATTENDU

- Commence par l'AXE le plus critique selon le contexte
- Pour chaque check : donne la commande CLI/curl **EXACTE**, prête à copier-coller
- Indique explicitement si le check est faisable **SANS VM/Docker**
- Si un check nécessite un accès déployé, fournis l'URL template à adapter
- Génère le rapport final en Markdown structuré, exportable en `.md`

---

## INFORMATIONS COMPLÉMENTAIRES (pré-remplies)

| Champ                             | Valeur                                                         |
| --------------------------------- | -------------------------------------------------------------- |
| **Projet**                        | NG-Fields (gestion d'interventions terrain)                    |
| **URL Keycloak (dev)**            | `http://localhost:9090`                                        |
| ~~**URL Keycloak (prod)**~~       | ~~`https://auth.ng-fields.ngs.tg`~~                            |
| ~~**URL Keycloak (staging)**~~    | ~~`https://auth-staging.ng-fields.ngs.tg`~~                    |
| **URL API Gateway (dev)**         | `http://localhost:8080`                                        |
| ~~**URL API Gateway (prod)**~~    | ~~`https://api.ng-fields.ngs.tg`~~                             |
| ~~**URL API Gateway (staging)**~~ | ~~`https://api-staging.ng-fields.ngs.tg`~~                     |
| ~~**URL Web App (prod)**~~            | ~~`https://app.ng-fields.ngs.tg`~~                                 |
| **Realm Keycloak**                | `ng-fields`                                                    |
| **Client ID — Angular (web)**     | `ng-fields-web` (public, PKCE S256)                            |
| **Client ID — Flutter (mobile)**  | `ng-fields-mobile` (public, PKCE S256)                         |
| **Client ID — Backend (M2M)**     | `ng-fields-backend` (confidentiel, client_credentials)         |
| **Accès au code source**          | Oui — accès complet (vault Obsidian)                           |
| **Services déployés en**          | Cloud (ngs.tg) + localhost pour le dev                         |
| **Langue du rapport final**       | Français                                                       |
| **Dossier du code source**        | `06_PROJETS/Projet_NG-Fields/apps/`                            |
| **Fichier Keycloak realm export** | `06_PROJETS/Projet_NG-Fields/infra/keycloak/realm-export.json` |
