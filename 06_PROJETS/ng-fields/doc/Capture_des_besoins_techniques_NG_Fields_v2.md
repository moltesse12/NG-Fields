# Capture des Besoins Techniques — NG-Fields

**Projet :** NG-Fields – Digitalisation de la gestion des interventions terrain  
**Référence CdC :** NG-STARs Cahier des Charges V4-Final  
**Rédigé par :** FOLLY Nelson Emmanuel (Stagiaire)  
**Validateur :** David KATOH (Responsable IT)  
**Version :** 2.0  
**Date :** 01/06/2026  

> **⚠️ Note Post-Cadrage (21/07/2026) :** Ce document contient des references obsolètes aux fonctionnalites supprimees du perimetre : WhatsApp, OpenProject, facturation. Les sections concernees necessitent une revision. Se referer au Backlog V6.0 pour la version actualisee.

---

## Sommaire

1. [Introduction](#1-introduction)
2. [Architecture technique du système](#2-architecture-technique-du-système)
3. [Contraintes techniques](#3-contraintes-techniques)
4. [Technologies retenues](#4-technologies-retenues)
5. [Déploiement](#5-déploiement)
6. [Interopérabilité et systèmes externes](#6-interopérabilité-et-systèmes-externes)
7. [Plan de supervision et de journalisation](#7-plan-de-supervision-et-de-journalisation)
8. [Sécurité système](#8-sécurité-système)
9. [Diagramme de déploiement](#9-diagramme-de-déploiement)
10. [Conclusion](#10-conclusion)

---

## 1. Introduction

### 1.1 Objet du document

Ce document constitue la capture des besoins techniques du projet NG-Fields (version 2.0). Il complète la capture des besoins fonctionnels en précisant les exigences d'architecture, les technologies définitivement retenues, les contraintes de déploiement, les mécanismes de sécurité et les modalités de supervision nécessaires à la réalisation du système. Il sert de référence technique commune pour l'équipe de développement senior de NG-STARs tout au long du cycle de vie du projet.

La version 2.0 de ce document intègre les choix technologiques définitifs arrêtés lors du Sprint 0 :

- **Backend :** Spring Boot (Java / Kotlin)
- **Application web :** Angular (TypeScript)
- **Application mobile :** Flutter (Dart)
- **Authentification centralisée :** Keycloak (IAM — Identity and Access Management)

### 1.2 Périmètre

Le système NG-Fields se compose de cinq composantes complémentaires :

- une **application mobile Flutter** (iOS et Android) dédiée aux techniciens terrain ;
- une **application web Angular** (tableau de bord) accessible aux managers et administrateurs ;
- un **portail client Angular** simplifié pour la soumission de demandes d'intervention ;
- un **backend Spring Boot** (API REST) assurant la logique métier, la persistance, la génération des PDF, la synchronisation hors-ligne et les notifications ;
- un **serveur Keycloak** assurant l'authentification centralisée, la gestion des rôles (RBAC) et la fédération d'identité pour toutes les composantes.

### 1.3 Références

| Document | Version | Date |
|---|---|---|
| Cahier des charges SDCGI | V4-Final | 10/05/2026 |
| Capture des besoins fonctionnels | 1.0 | 01/06/2026 |
| Capture des besoins techniques | 1.0 | 01/06/2026 |
| Document UML par la pratique (P. Roques) | 5e édition | 2006 |
| Keycloak Documentation | 24.x | 2026 |
| Spring Boot Documentation | 3.x | 2026 |

---

## 2. Architecture technique du système

### 2.1 Vue d'ensemble

L'architecture de NG-Fields est organisée selon le modèle **client-serveur multi-tiers**, avec un découplage clair entre la couche présentation (Flutter mobile et Angular web), la couche identité et accès (Keycloak), la couche métier (Spring Boot API REST) et la couche de persistance (PostgreSQL, Redis, stockage objets).

Keycloak constitue le **point d'entrée unique pour toute authentification**. Aucun client (mobile ou web) n'accède au backend sans un jeton d'accès (Access Token) OIDC valide, émis et signé par Keycloak. Le backend Spring Boot valide chaque jeton entrant en vérifiant sa signature auprès du JWKS (JSON Web Key Set) de Keycloak.

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                             COUCHE PRÉSENTATION                                │
│                                                                                │
│  ┌──────────────────────────────────┐  ┌──────────────────────────────────────┐│
│  │   Application Mobile             │  │  Application Web / Portail Client    ││
│  │   Flutter (Dart)                 │  │  Angular (TypeScript)                ││
│  │   iOS 14+ / Android 10+          │  │  ng-fields.ngs.tg                    ││
│  │   ┌───────────────────────────┐  │  │  ┌──────────────────────────────┐    ││
│  │   │ SQLite chiffré (Drift +   │  │  │  │ Keycloak JS Adapter / OIDC   │    ││
│  │   │ SQLCipher)                │  │  │  │ angular-oauth2-oidc          │    ││
│  │   │ flutter_secure_storage    │  │  │  └──────────────────────────────┘    ││
│  │   │ flutter_appauth (OIDC)    │  │  └───────────────────┬──────────────────┘│
│  │   └───────────────────────────┘  │                      │                   │
│  └────────────────┬─────────────────┘                      │                   │ └───────────────────┼────────────────────────────────────────┼───────────────────┘
OIDC Authorization Code + PKCE                 OIDC Authorization Code
                    ▼                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                     COUCHE IDENTITÉ ET ACCÈS — KEYCLOAK                         │
│                                                                                 │
│┌───────────────────────────────────────────────────────────────────────────────┐│
││  Keycloak 24.x (Docker container)                                             ││
││  Realm : ng-fields                                                            ││
││  ├── Clients OIDC : ng-fields-mobile | ng-fields-web | ng-fields-backend      ││
││  ├── Rôles : ADMIN | MANAGER | TECHNICIAN | CLIENT_ADMIN | CLIENT_USER | CLIENT_VIEWER ││
││  ├── Flux : Authorization Code + PKCE (mobile/web) | Client Credentials(M2M)  ││
││  ├── Tokens : Access Token JWT (15 min) | Refresh Token (7 jours)             ││
││  └── JWKS endpoint : /realms/ng-fields/protocol/openid-connect/certs          ││
│└───────────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │  Bearer Token JWT (validation JWKS)
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                       COUCHE MÉTIER — API REST (SPRING BOOT)                    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────────┐│
│  │  Spring Boot 3.x — Java 21 / Kotlin (Docker container)                      ││
│  │  ├── spring-security (Resource Server — validation JWT Keycloak)            ││
│  │  ├── InterventionController / Service / Repository                          ││
│  │  ├── ClientController / Service / Repository                                ││
│  │  ├── PdfService (iText / Flying Saucer)                                     ││
│  │  ├── NotificationService (FCM + APNs + Email)                                ││
│  │  ├── SyncService (résolution conflits hors-ligne)                           ││
│  │  └── StorageService (MinIO / Supabase Storage)                              ││
│  └─────────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────────┘                                        │
┌───────────────────────────────────────▼─────────────────────────────────────────┐
│                            COUCHE PERSISTANCE                                   │
│                                                                                 │┌──────────────────┐  ┌─────────────────────┐  ┌────────────────────────────────┐
││  PostgreSQL 15   │  │  Redis 7            │  │  MinIO / Supabase Storage      │
││  (Supabase)      │  │  ├── Cache API      │  │  ├── Photos (JPEG/PNG)         ││
││  Spring Data JPA │  │  └── Queues async   │  │  └── Rapports PDF              ││
││  + Hibernate     │  │     (pdf/email/push/│  │  Accès S3-compatible           ││
││  Flyway (schema  │  │      sync)          │  │  URLs signées (72 h)           ││
││  migrations)     │  └─────────────────────┘  └────────────────────────────────┘ │└──────────────────┘                                                             │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Principes d'architecture

**Offline-First.** L'application Flutter est conçue selon une logique offline-first. Toutes les données saisies (formulaire, photos, signatures, coordonnées GPS) sont d'abord persistées localement dans une base Drift (SQLite) chiffrée via SQLCipher, puis synchronisées automatiquement avec le backend Spring Boot dès le retour de la connectivité, dans un délai maximum de 60 secondes.

**Authentification centralisée par Keycloak.** Keycloak est le seul émetteur de jetons pour l'ensemble du système. Le backend Spring Boot est configuré en tant que Resource Server OIDC et délègue intégralement la validation des tokens à Keycloak via la vérification JWKS. Aucune gestion locale d'identité n'existe dans le backend.

**Séparation des responsabilités.** Le backend suit une architecture en trois couches (Controller → Service → Repository), chaque module fonctionnel possédant ses propres classes de traitement, conformément aux bonnes pratiques de conception orientée objet.

**Stateless.** L'API REST Spring Boot est sans état. Chaque requête HTTP est authentifiée indépendamment via le Bearer Token JWT, sans maintien de session côté serveur.

**Modularité et évolutivité.** Chaque domaine fonctionnel est encapsulé dans un package Spring indépendant, facilitant la maintenance et les évolutions futures.

### 2.3 Flux d'authentification OIDC

#### 2.3.1 Application Angular (web) — Authorization Code Flow + PKCE

```
Utilisateur
    │
    ▼
Angular App  ──── Redirection vers Keycloak ──────────────▶ Keycloak
                  (Authorization Code + PKCE)               │
                                                            │ Page de login
                                                            │ (Keycloak UI)
Angular App  ◀─── Retour avec Authorization Code ───────────┘
    │
    │  Échange du code contre les tokens
    ▼
Keycloak ────── Access Token JWT + Refresh Token ──────────▶ Angular App
    │
    │  Bearer Token dans chaque requête HTTP
    ▼
Spring Boot API ──── Validation JWKS ──────────────────────▶ Keycloak
                                                           (JWKS endpoint)
```

#### 2.3.2 Application Flutter (mobile) — Authorization Code Flow + PKCE (flutter_appauth)

```
Utilisateur (technicien)
    │
    ▼
Flutter App  ──── flutter_appauth ──── Keycloak (OIDC)
                  (Authorization Code + PKCE)
                  Redirection vers navigateur sécurisé in-app
                                        │
Flutter App  ◀─── Access Token JWT + Refresh Token ──────┘
    │
    │  Tokens stockés dans flutter_secure_storage (Keychain iOS / Keystore Android)
    │
    │  Bearer Token dans chaque appel HTTP vers Spring Boot
    ▼
Spring Boot API ──── Validation JWKS ──────────────────────▶ Keycloak
```

#### 2.3.3 Communication Machine-to-Machine (M2M)

Pour les appels internes (backend vers services externes), le flux **Client Credentials** de Keycloak est utilisé, sans interaction utilisateur.

### 2.4 Architecture de l'application mobile Flutter

| Couche | Technologie / Bibliothèque | Rôle |
|---|---|---|
| **UI Layer** | Flutter Widgets, Material 3 | Écrans et composants visuels |
| **State Management** | Riverpod 2.x (ou BLoC) | Gestion de l'état global et réactif |
| **Auth Layer** | flutter_appauth + flutter_secure_storage | OIDC Authorization Code + PKCE, stockage sécurisé des tokens |
| **Service Layer** | Dart classes | Logique métier locale (calcul durée, validation, compression photo) |
| **Sync Service** | Dart isolates + connectivity_plus | File d'attente hors-ligne, détection réseau, envoi différé |
| **Local DB** | Drift (SQLite) + SQLCipher | Base de données locale chiffrée (offline-first) |
| **API Client** | Dio + intercepteurs JWT | Appels HTTP vers Spring Boot, rafraîchissement automatique des tokens |
| **GPS** | geolocator | Capture automatique des coordonnées à l'arrivée sur site |
| **Camera** | camera + image_picker | Capture photo avec métadonnées EXIF/GPS |
| **Signature** | signature (canvas tactile) | Zones de signature électronique |
| **PDF** | flutter_pdfview | Visualisation des rapports PDF générés |
| **Notifications** | firebase_messaging + flutter_local_notifications | Réception des push notifications |

### 2.5 Architecture de l'application web Angular

| Couche | Technologie / Bibliothèque | Rôle |
|---|---|---|
| **UI Layer** | Angular 17+ (Standalone Components) | Composants, pages, routing |
| **Design System** | Angular Material + Tailwind CSS | Charte graphique NG-STARs |
| **Auth Layer** | angular-oauth2-oidc | Flux OIDC Authorization Code + PKCE, gestion des tokens Keycloak |
| **HTTP Layer** | HttpClient + AuthInterceptor | Injection automatique du Bearer Token dans chaque requête |
| **State Management** | NgRx (Store + Effects) ou Signals | Gestion de l'état global réactif |
| **Formulaires** | Reactive Forms + Angular Validators | Validation des formulaires portail client |
| **Charts / KPI** | Chart.js via ng2-charts | Visualisation des indicateurs du tableau de bord |
| **Tables** | AG Grid Community | Tableaux filtrables avec export CSV/Excel |
| **PDF Viewer** | ng2-pdf-viewer | Prévisualisation des rapports PDF |
| **i18n** | @angular/localize | Internationalisation (français prioritaire) |

### 2.6 Architecture du backend Spring Boot

| Package / Module | Responsabilité principale                                                                                      |
| ---------------- | -------------------------------------------------------------------------------------------------------------- |
| `security`       | Configuration Spring Security Resource Server, validation JWT Keycloak, filtres CORS                           |
| `intervention`   | CRUD fiches d'intervention, calcul durée, gestion statut, synchronisation offline                              |
| `client`         | CRUD fiches client, historique des interventions                                                               |
| `pdf`            | Génération automatique des rapports PDF (iText/Flying Saucer), assemblage logo + photos + signatures + QR code |
| `notification`   | Push (FCM/APNs via Firebase Admin SDK), Email (Resend)                                                  |
| `sync`           | Réception et résolution des conflits des fiches hors-ligne (stratégie last-write-wins)                         |
| `storage`        | Gestion du stockage des photos et PDFs (MinIO / Supabase Storage, S3-compatible)                               |
| `audit`          | Journal d'audit horodaté de toutes les actions sensibles                                                       |
| `health`         | Actuator endpoints (healthcheck, métriques Prometheus/Micrometer)                                              |

---

## 3. Contraintes techniques

### 3.1 Contraintes de connectivité

L'analyse des conditions réelles d'intervention NG-STARs met en évidence les niveaux de connectivité suivants :

| Zone | Qualité réseau | Fréquence estimée | Mode appliqué |
|---|---|---|---|
| Siège et bureaux | Excellente, stable | Continue | Synchronisation temps réel |
| Zones urbaines centre-ville | Bonne — interruptions ponctuelles | ~70 % des interventions | Mode connecté principal |
| Zones périphériques | Moyenne à faible | ~30 % des interventions | Mode offline régulier |
| Zones rurales | Faible à nulle | ~20 % des interventions | Mode offline exclusif |

Le mode hors-ligne est une **exigence fondamentale et non optionnelle**, couvrant 100 % des scénarios d'intervention. Le token Keycloak est stocké localement dans `flutter_secure_storage` pour permettre l'accès offline aux données locales sans nécessiter une nouvelle authentification à chaque démarrage.

> **Contrainte spécifique Keycloak :** L'authentification initiale (obtention du premier Access Token) nécessite une connexion réseau. Une fois le Refresh Token stocké, l'application peut renouveler les Access Tokens en arrière-plan dès que la connectivité est disponible. Les opérations métier locales (saisie hors-ligne) ne sont pas bloquées par l'expiration du token.

### 3.2 Contraintes de performance

| Indicateur                                                        | Valeur cible              |
| ----------------------------------------------------------------- | ------------------------- |
| Génération du rapport PDF                                         | ≤ 10 secondes             |
| Synchronisation hors-ligne au retour réseau                       | ≤ 60 secondes             |
| Délai de réception des notifications push                         | ≤ 1 minute                |
| Délai d'envoi email du rapport                                    | ≤ 2 minutes               |
| Création d'une demande d'intervention depuis le portail client    | ≤ 30 secondes             |
| Temps de réponse de l'API REST Spring Boot (opérations courantes) | ≤ 2 secondes              |
| Compression photo (préservation métadonnées GPS/EXIF)             | Cible ≤ 500 Ko par image  |
| Démarrage de l'application Flutter (cold start)                   | ≤ 3 secondes              |
| Temps de chargement initial Angular (bundle)                      | ≤ 4 secondes (3G simulée) |

### 3.3 Contraintes de compatibilité

- Application Flutter : **iOS 14+** et **Android 10+** (API ).
- Application Angular : navigateurs modernes sans installation — Chrome, Firefox, Edge et Safari (versions N et N-1), en mode responsive (desktop + tablette).
- L'API Spring Boot respecte le standard **OpenAPI 3.0** (documentation générée via SpringDoc / Swagger UI).
- Keycloak : version **24.x** minimum, compatible avec le protocole **OpenID Connect 1.0** et **OAuth 2.1**.

### 3.4 Contraintes de capacité

- Minimum **15 techniciens** utilisateurs simultanés de l'application Flutter.
- Minimum **10 managers** utilisateurs simultanés de l'application Angular.
- Minimum **10 administrateurs IT** pour la gestion du système et de Keycloak.
- Jusqu'à **10 photos par intervention** (5 avant + 5 après), stockées avec leurs métadonnées.
- Sauvegardes automatiques quotidiennes avec rétention de **30 jours minimum**.
- Keycloak doit supporter au moins **35 utilisateurs actifs simultanément** avec un temps de réponse au login ≤ 3 secondes.

### 3.5 Contraintes budgétaires

| Poste | Coût estimé / mois |
|---|---|
| VPS Hetzner CX21 (backend Spring Boot + Keycloak + Redis) | 10 – 15 € |
| Supabase Pro (PostgreSQL managé + Storage) | 25 € |
| Abonnement Claude Pro (assistance IA développement) | 20 € |
| **Total estimé** | **55 – 60 € / mois** |

> Keycloak Community Edition est **open-source et gratuit**. Son hébergement est inclus dans le VPS mutualisé, avec un container Docker dédié.

---

## 4. Technologies retenues

### 4.1 Récapitulatif des choix définitifs

| Composante | Technologie | Version | Justification principale |
|---|---|---|---|
| **Mobile** | Flutter | 3.x (Dart 3.x) | Performances natives, UI cohérente iOS/Android, excellent support offline |
| **Web** | Angular | 17+ (TypeScript 5.x) | Framework entreprise robuste, intégration native OIDC, maîtrisé par l'équipe |
| **Backend** | Spring Boot | 3.x (Java 21) | Écosystème Java mature, Spring Security Resource Server natif pour Keycloak |
| **Authentification** | Keycloak | 24.x | Solution IAM open-source de référence, OIDC/OAuth2, RBAC avancé |
| **Base de données** | PostgreSQL 15 | via Supabase | Robustesse ACID, open-source, extensions JSON |
| **Cache / Queues** | Redis 7 | Alpine | Ultra-rapide, Redisson / Spring Data Redis |
| **Stockage objets** | MinIO / Supabase Storage | — | S3-compatible, open-source, URLs signées |
| **CI/CD** | GitHub Actions | — | Intégration native, pipelines Docker |
| **Containerisation** | Docker + Docker Compose | — | Reproductibilité dev/staging/prod |
| **Reverse Proxy** | Nginx | Alpine | TLS Let's Encrypt, routage vers les services |

### 4.2 Application mobile Flutter

#### Dépendances pubspec.yaml principales

```yaml
dependencies:
  # Authentication OIDC (Keycloak)
  flutter_appauth: ^6.0.0          # Authorization Code + PKCE
  flutter_secure_storage: ^9.0.0   # Stockage sécurisé tokens (Keychain/Keystore)

  # HTTP & API
  dio: ^5.4.0                      # Client HTTP avec intercepteurs JWT
  retrofit: ^4.1.0                 # Génération client API REST typé

  # Base de données locale offline-first
  drift: ^2.14.0                   # ORM SQLite async (ex Moor)
  drift_sqflite: ^2.1.0
  sqflite_sqlcipher: ^2.2.0        # Chiffrement AES-256

  # State Management
  flutter_riverpod: ^2.5.0
  riverpod_annotation: ^2.3.0

  # Géolocalisation
  geolocator: ^11.0.0
  permission_handler: ^11.3.0

  # Photographie
  camera: ^0.10.5
  image_picker: ^1.1.0
  image: ^4.1.0                    # Compression + métadonnées EXIF

  # Signature électronique
  signature: ^5.4.0

  # Synchronisation réseau
  connectivity_plus: ^6.0.0

  # Notifications push
  firebase_messaging: ^14.8.0
  flutter_local_notifications: ^16.3.0

  # PDF
  flutter_pdfview: ^1.3.0

  # QR Code
  qr_flutter: ^4.1.0

  # UI
  google_fonts: ^6.2.0
```

#### Architecture interne Flutter (Clean Architecture)

```
lib/
├── core/
│   ├── auth/            # Keycloak OIDC (flutter_appauth)
│   ├── network/         # Dio client + JWT interceptor
│   ├── database/        # Drift database + migrations
│   └── sync/            # SyncService (queue offline → backend)
├── features/
│   ├── intervention/    # domain / data / presentation
│   ├── client/
│   ├── photo/
│   ├── signature/
│   ├── pdf/
│   └── dashboard/
└── shared/              # Widgets communs, constantes, thème
```

### 4.3 Application web Angular

#### Dépendances package.json principales

```json
{
  "dependencies": {
    "@angular/core": "^17.x",
    "@angular/material": "^17.x",
    "@angular/router": "^17.x",
    "@angular/forms": "^17.x",
    "@ngrx/store": "^17.x",
    "@ngrx/effects": "^17.x",
    "angular-oauth2-oidc": "^17.x",
    "ag-grid-community": "^31.x",
    "ag-grid-angular": "^31.x",
    "chart.js": "^4.x",
    "ng2-charts": "^6.x",
    "ng2-pdf-viewer": "^10.x",
    "xlsx": "^0.18.x",
    "tailwindcss": "^3.x"
  }
}
```

#### Configuration de l'authentification Keycloak dans Angular

```typescript
// app.config.ts
import { provideAuth } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'https://auth.ng-fields.ngs.tg/realms/ng-fields',
  redirectUri: window.location.origin + '/callback',
  clientId: 'ng-fields-web',
  responseType: 'code',
  scope: 'openid profile email roles offline_access',
  useSilentRefresh: true,
  silentRefreshTimeout: 5000,
  showDebugInformation: false,
  requireHttps: true,
  pkce: true,
};
```

#### Architecture modulaire Angular

```
src/app/
├── core/
│   ├── auth/              # Keycloak OIDC (angular-oauth2-oidc)
│   ├── interceptors/      # AuthInterceptor (injection Bearer Token)
│   ├── guards/            # AuthGuard, RoleGuard
│   └── services/          # API services (HttpClient)
├── features/
│   ├── dashboard/         # Tableau de bord manager
│   ├── interventions/     # Liste, détail, historique
│   ├── clients/           # Fiches client
│   ├── planning/          # Planning et agenda
│   ├── notifications/     # Centre de notifications
│   └── client-portal/     # Portail de soumission demandes
├── shared/
│   ├── components/        # Composants réutilisables
│   ├── models/            # Interfaces TypeScript (DTOs)
│   └── pipes/             # Pipes de formatage
└── layouts/               # Shell, navigation, header
```

### 4.4 Backend Spring Boot

#### Dépendances pom.xml principales

```xml
<!-- Spring Boot Starter Web (API REST) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Security + OAuth2 Resource Server (Keycloak JWT) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Data JPA + PostgreSQL -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
</dependency>

<!-- Flyway (migrations schéma) -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>

<!-- Spring Data Redis -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Spring Actuator (healthcheck + métriques) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Prometheus (supervision) -->
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- iText / OpenPDF (génération PDF) -->
<dependency>
  <groupId>com.github.librepdf</groupId>
  <artifactId>openpdf</artifactId>
  <version>1.4.1</version>
</dependency>

<!-- Firebase Admin SDK (push notifications) -->
<dependency>
  <groupId>com.google.firebase</groupId>
  <artifactId>firebase-admin</artifactId>
  <version>9.3.0</version>
</dependency>

<!-- Spring Mail (email) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- SpringDoc OpenAPI (documentation Swagger) -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.3.0</version>
</dependency>

<!-- Validation -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- AWS S3 SDK (MinIO / Supabase Storage) -->
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>s3</artifactId>
</dependency>
```

#### Configuration Spring Security pour Keycloak (Resource Server)

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/client-portal/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(keycloakJwtConverter())
                )
            );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter keycloakJwtConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("realm_access.roles");
        converter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
```

#### Configuration application.yml (extrait)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.ng-fields.ngs.tg/realms/ng-fields
          jwk-set-uri: https://auth.ng-fields.ngs.tg/realms/ng-fields/protocol/openid-connect/certs

  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  flyway:
    enabled: true
    locations: classpath:db/migration

  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379
      password: ${REDIS_PASSWORD}

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
```

### 4.5 Keycloak — Configuration du Realm ng-fields

#### Clients OIDC configurés

| Client ID | Type | Flux | Usage |
|---|---|---|---|
| `ng-fields-web` | Public | Authorization Code + PKCE | Application Angular (navigateur) |
| `ng-fields-mobile` | Public | Authorization Code + PKCE | Application Flutter (mobile) |
| `ng-fields-backend` | Confidential | Client Credentials | Appels M2M (backend → services externes) |

#### Rôles du Realm

| Rôle | Périmètre d'accès |
|---|---|
| `ADMIN` | Accès complet à toutes les fonctionnalités + administration Keycloak |
| `MANAGER` | Tableau de bord, notifications, signature différée, exports, affectation techniciens |
| `TECHNICIAN` | Création/modification de ses interventions, photos, signatures, synchronisation |
| `CLIENT_ADMIN` | Admin entreprise — gère les utilisateurs de sa société |
| `CLIENT_USER` | Utilisateur entreprise — soumet des demandes d'intervention |
| `CLIENT_VIEWER` | Consultation seule — voit les interventions et rapports |

#### Paramètres de sécurité Keycloak recommandés

| Paramètre | Valeur recommandée |
|---|---|
| Access Token Lifespan | 15 minutes |
| Refresh Token Lifespan | 7 jours (SSO Session Max) |
| Brute Force Detection | Activé (5 tentatives → 30 min verrouillage) |
| Require SSL | ALL (production) |
| Login avec PKCE obligatoire | Activé pour les clients publics |
| Politique de mot de passe | Minimum 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial |
| Email vérification obligatoire | Activé |

### 4.6 Base de données et persistance

#### Schéma des entités principales (PostgreSQL)

```sql
-- Gestion des utilisateurs (délégué à Keycloak, référence par keycloak_id)
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id UUID UNIQUE NOT NULL,   -- sub du token JWT Keycloak
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    role        VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','MANAGER','TECHNICIAN')),
    department  VARCHAR(100),
    phone       VARCHAR(20),
    active      BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

-- Clients
CREATE TABLE clients (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(150) NOT NULL,
    email        VARCHAR(150),
    phone        VARCHAR(20),
    address      TEXT,
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    active       BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMPTZ DEFAULT NOW(),
    updated_at   TIMESTAMPTZ DEFAULT NOW()
);

-- Fiches d'intervention
CREATE TABLE interventions (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id                VARCHAR(50) UNIQUE,          -- ID généré offline côté Flutter
    date                    TIMESTAMPTZ NOT NULL,
    status                  VARCHAR(20) NOT NULL CHECK (status IN
                             ('PENDING','IN_PROGRESS','COMPLETED','CANCELLED')),
    type                    VARCHAR(50),
    technician_id           UUID REFERENCES users(id),
    client_id               UUID REFERENCES clients(id),
    departure_time          TIME,
    arrival_time            TIME,
    intervention_start_time TIME,
    intervention_end_time   TIME,
    return_time             TIME,
    duration_minutes        INTEGER,                     -- calculé automatiquement
    problem_desc            TEXT,
    diagnosis               TEXT,
    work_done               TEXT,
    result                  VARCHAR(30) CHECK (result IN ('RESOLVED','PARTIAL','UNRESOLVED')),
    recommendations         TEXT,
    equipment_type          VARCHAR(100),
    equipment_brand         VARCHAR(100),
    equipment_model         VARCHAR(100),
    equipment_serial        VARCHAR(100),
    equipment_location      VARCHAR(200),
    signature_client_url    TEXT,
    signature_technician_url TEXT,
    signature_manager_url   TEXT,
    pdf_url                 TEXT,
    synced_at               TIMESTAMPTZ,
    created_at              TIMESTAMPTZ DEFAULT NOW(),
    updated_at              TIMESTAMPTZ DEFAULT NOW()
);

-- Photos
CREATE TABLE intervention_photos (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id UUID REFERENCES interventions(id) ON DELETE CASCADE,
    url             TEXT NOT NULL,
    type            VARCHAR(10) CHECK (type IN ('BEFORE','AFTER')),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    taken_at        TIMESTAMPTZ,
    local_path      TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Pièces et consommables
CREATE TABLE intervention_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id UUID REFERENCES interventions(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    quantity        INTEGER DEFAULT 1,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Journal d'audit
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    resource    VARCHAR(100),
    resource_id UUID,
    ip_address  INET,
    user_agent  TEXT,
    details     JSONB,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);
```

> **Flyway** gère les migrations de schéma de manière versionnée (`V1__init.sql`, `V2__add_audit.sql`, etc.), garantissant la cohérence entre les environnements dev, staging et production.

### 4.7 Infrastructure et hébergement

| Composant                  | Solution                                 | Justification                                                |
| -------------------------- | ---------------------------------------- | ------------------------------------------------------------ |
| Base de données PostgreSQL | Supabase Pro                             | PostgreSQL managé, backup automatique, Storage S3-compatible |
| Serveur d'application      | VPS Hetzner CX31 (4 vCPU, 8 Go RAM)      | Charge Spring Boot + Keycloak + Redis simultanément          |
| Reverse Proxy / TLS        | Nginx + Let's Encrypt (Certbot)          | Certificats gratuits, renouvellement automatique             |
| Containerisation           | Docker + Docker Compose                  | Environnements reproductibles                                |
| CI/CD                      | GitHub Actions                           | Pipeline automatisé build → test → deploy                    |
| Stockage objets            | MinIO (auto-hébergé) ou Supabase Storage | S3-compatible, open-source                                   |

> Spring Boot et Keycloak sont des applications Java exigeantes en mémoire. Le VPS CX31 (8 Go RAM) est recommandé pour héberger les deux services simultanément en production.

---

## 5. Déploiement

### 5.1 Stratégie de déploiement

Le déploiement suit une méthodologie **Agile Scrum** avec sprints hebdomadaires. Le pipeline CI/CD est mis en place dès le Sprint 1 et permet des livraisons continues sur les environnements staging et production.

```
Commit GitHub (main / develop)
         │
         ▼
GitHub Actions — CI Pipeline
  ├── Lint (Checkstyle Java, ESLint Angular, Dart Analyze Flutter)
  ├── Tests unitaires (JUnit 5 + JaCoCo, Karma Angular, Flutter test)
  ├── Tests d'intégration (Testcontainers PostgreSQL + Keycloak)
  ├── Build artefacts
  │   ├── Spring Boot : ./mvnw package -DskipTests → JAR
  │   ├── Angular : ng build --configuration production → dist/
  │   └── Flutter : flutter build apk --release + flutter build ipa
  └── Build Docker images → push vers GitHub Container Registry (ghcr.io)
         │
         ▼
  Déploiement staging (automatique sur push develop)
         │
         ▼
  Validation (David KATOH)
         │
         ▼
  Déploiement production (manuel sur tag de release)
```

### 5.2 Configuration Docker Compose (production)

```yaml
version: '3.9'

services:

  nginx:
    image: nginx:alpine
    ports: ["80:80", "443:443"]
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - /etc/letsencrypt:/etc/letsencrypt:ro
    depends_on: [api, keycloak, web]

  keycloak:
    image: quay.io/keycloak/keycloak:24.0
    command: start
    environment:
      KC_HOSTNAME: auth.ng-fields.ngs.tg
      KC_HTTPS_CERTIFICATE_FILE: /opt/keycloak/conf/tls.crt
      KC_HTTPS_CERTIFICATE_KEY_FILE: /opt/keycloak/conf/tls.key
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://db:5432/keycloak
      KC_DB_USERNAME: ${KC_DB_USER}
      KC_DB_PASSWORD: ${KC_DB_PASSWORD}
      KEYCLOAK_ADMIN: ${KC_ADMIN}
      KEYCLOAK_ADMIN_PASSWORD: ${KC_ADMIN_PASSWORD}
      KC_PROXY: edge
    volumes:
      - ./keycloak/realm-ng-fields.json:/opt/keycloak/data/import/realm.json
    deploy:
      resources:
        limits:
          memory: 2g

  api:
    image: ghcr.io/ng-stars/ng-fields-api:latest
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: ${SUPABASE_DB_HOST}
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD}
      KEYCLOAK_ISSUER_URI: https://auth.ng-fields.ngs.tg/realms/ng-fields
    depends_on: [redis]
    deploy:
      resources:
        limits:
          memory: 1g

  web:
    image: ghcr.io/ng-stars/ng-fields-web:latest
    # Sert les fichiers Angular statiques via Nginx interne

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio:latest
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_PASSWORD}
    volumes:
      - minio_data:/data

volumes:
  redis_data:
  minio_data:
```

### 5.3 Environnements et sous-domaines

| Environnement | Backend | Web | Keycloak |
|---|---|---|---|
| Development | `localhost:8080` | `localhost:4200` | `localhost:8180` |
| Staging | `api-staging.ng-fields.ngs.tg` | `staging.ng-fields.ngs.tg` | `auth-staging.ng-fields.ngs.tg` |
| Production | `api.ng-fields.ngs.tg` | `app.ng-fields.ngs.tg` | `auth.ng-fields.ngs.tg` |

### 5.4 Planning de déploiement par sprint

| Sprint | Période | Objectif de déploiement |
|---|---|---|
| S-01 | Semaine 1 | CI/CD opérationnel, Keycloak configuré (Realm + clients + rôles), Spring Boot Resource Server, Flutter AppAuth |
| S-02 | Semaine 2 | Module clients, gestion utilisateurs, Angular login OIDC fonctionnel |
| S-03 | Semaine 3 | Formulaire intervention sections 1–4 (Flutter) |
| S-04 | Semaine 4 | Formulaire complet sections 5–8 (photos, signatures) |
| S-05 | Semaine 5 | Géolocalisation GPS, calcul durée, intégration Drift offline |
| S-06 | Semaine 6 | SyncService hors-ligne complet, résolution conflits |
| S-07 | Semaine 7 | Génération PDF (OpenPDF), envoi email |
| S-08 | Semaine 8 | Notifications push FCM/APNs |
| S-09 | Semaine 9 | Dashboard Angular (NgRx, AG Grid, Charts) |
| S-10 | Semaine 10 | Tests UAT avec techniciens et managers, corrections |
| S-11 | Semaine 11 | **Mise en production V1.0**, publication App Store et Google Play |

**Jalon de mise en production officielle : 30/06/2026**

### 5.5 Publication mobile Flutter

**Google Play Store (Android) :**
- Signature de l'APK avec le keystore NG-STARs (stocké dans GitHub Secrets).
- `flutter build apk --release` ou `flutter build appbundle --release` (recommandé).
- Soumission depuis la Google Play Console.

**Apple App Store (iOS) :**
- Adhésion au programme Apple Developer (99 $/an).
- `flutter build ipa --release` avec certificat de distribution valide.
- Distribution via TestFlight pour les tests UAT, puis soumission App Review.

### 5.6 Plan de reprise d'activité (PRA)

| Scénario | RTO cible | Procédure |
|---|---|---|
| Panne VPS | ≤ 4 heures | Redéploiement Docker Compose depuis GitHub Container Registry sur un nouveau VPS |
| Corruption PostgreSQL | ≤ 2 heures | Restauration depuis la sauvegarde automatique Supabase (rétention 30 jours) |
| Indisponibilité Keycloak | ≤ 30 minutes | Redémarrage du container ; les tokens existants restent valides pendant leur durée de vie (15 min) |

---

## 6. Interopérabilité et systèmes externes

### 6.1 Keycloak — API d'administration

Le backend Spring Boot communique avec l'API d'administration Keycloak pour certaines opérations (création de compte utilisateur, désactivation) en utilisant le flux Client Credentials.

| Opération | Méthode | Endpoint Keycloak Admin API |
|---|---|---|
| Créer un utilisateur | POST | `/admin/realms/ng-fields/users` |
| Désactiver un compte | PUT | `/admin/realms/ng-fields/users/{id}` |
| Attribuer un rôle | POST | `/admin/realms/ng-fields/users/{id}/role-mappings/realm` |
| Réinitialiser mot de passe | PUT | `/admin/realms/ng-fields/users/{id}/reset-password` |

### ~~6.2 OpenProject API REST v3~~ ❌ SUPPRIME

> **Decision cadrage 21/07/2026 :** L'integration OpenProject est supprimee du perimetre.

### 6.3 Service Email — Resend

| Parametre | Valeur |
|---|---|
| Fournisseur | Resend (API REST HTTPS) |
| Authentification | API Key (variable d'environnement `RESEND_API_KEY`) |
| Format | HTML + piece jointe PDF |
| Delai d'envoi cible | ≤ 2 minutes |
| Mecanisme de relance | Redis queue + Spring @Async avec retry exponentiel |

### ~~6.4 WhatsApp Business API (Meta)~~ ❌ SUPPRIME

> **Decision cadrage 21/07/2026 :** L'envoi WhatsApp est supprime du perimetre. Seul l'envoi par email reste.

### 6.5 Firebase Cloud Messaging (FCM) + APNs

| Paramètre | Valeur |
|---|---|
| SDK serveur | Firebase Admin SDK (Java) — `firebase-admin:9.3.0` |
| Authentification FCM | Service Account JSON (variable d'environnement) |
| Authentification APNs | Clé `.p8` ou certificat `.p12` |
| Délai de livraison cible | ≤ 1 minute |
| Canal de secours | Notification par email si le push échoue |

### 6.6 MinIO / Supabase Storage

| Paramètre | Valeur |
|---|---|
| Compatibilité | S3 (AWS SDK v2 Java) |
| Formats stockés | JPEG/PNG (photos), PDF (rapports) |
| Taille max par photo | 500 Ko après compression Flutter |
| Accès | URLs pré-signées (durée : 72 h pour partage, indéfini pour usage interne) |
| Chiffrement au repos | Activé (AES-256 côté serveur) |

---

## 7. Plan de supervision et de journalisation

### 7.1 Spring Boot Actuator et Micrometer

Le backend Spring Boot expose les endpoints de supervision suivants (sécurisés en lecture seule pour le rôle `ADMIN`) :

| Endpoint Actuator | Usage |
|---|---|
| `GET /actuator/health` | État de santé global (DB, Redis, Keycloak, Storage) |
| `GET /actuator/info` | Version de l'application et environnement |
| `GET /actuator/prometheus` | Métriques au format Prometheus/OpenMetrics |
| `GET /actuator/metrics` | Métriques détaillées (JVM, HTTP, DB pool) |
| `GET /actuator/loggers` | Consultation et modification dynamique des niveaux de log |

**Réponse type `/actuator/health` :**

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "details": { "database": "PostgreSQL", "validationQuery": "isValid()" } },
    "redis": { "status": "UP" },
    "keycloak": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### 7.2 Niveaux de journalisation

| Niveau | Environnement | Usage |
|---|---|---|
| `ERROR` | Tous | Erreurs critiques bloquantes (échec PDF, erreur DB) |
| `WARN` | Tous | Situations anormales non bloquantes (retry synchronisation, basculement canal secours) |
| `INFO` | Staging + Production | Événements métier significatifs (création intervention, envoi rapport, connexion) |
| `DEBUG` | Development uniquement | Traces détaillées — désactivé en staging/production |

**Format de log structuré (JSON — Logback) :**

```json
{
  "timestamp": "2026-06-01T10:32:15.123Z",
  "level": "INFO",
  "logger": "ng.stars.fields.intervention.InterventionService",
  "userId": "3f2e1a...",
  "userRole": "TECHNICIAN",
  "action": "CREATE_INTERVENTION",
  "resourceId": "7c8d9e...",
  "clientId": "1a2b3c...",
  "ip": "196.10.x.x",
  "duration_ms": 142,
  "message": "Intervention créée avec succès — statut PENDING"
}
```

### 7.3 Audit trail

Le journal d'audit est stocké dans la table `audit_logs` (PostgreSQL) et couvre toutes les actions sensibles :

| Action auditée |
|---|
| Connexion / Déconnexion (via événements Keycloak Event Listener) |
| Création / Modification / Suppression d'une intervention |
| Apposition d'une signature (client, technicien, manager) |
| Génération et envoi d'un rapport PDF |
| Création / Modification / Désactivation d'un compte utilisateur |
| Exportation de données (CSV/Excel) |
| Modification de la configuration système (seuils de notification) |

> Keycloak peut être configuré avec un **Event Listener** personnalisé pour envoyer les événements de connexion/déconnexion directement à l'API Spring Boot, qui les persiste dans `audit_logs`.

### 7.4 Métriques Prometheus (Micrometer)

| Métrique | Description |
|---|---|
| `http_server_requests_seconds` | Temps de réponse HTTP par endpoint et code de statut |
| `jvm_memory_used_bytes` | Consommation mémoire JVM (heap + non-heap) |
| `hikaricp_connections_active` | Connexions actives au pool PostgreSQL (HikariCP) |
| `pdf_generation_duration_seconds` | Durée de génération des rapports PDF |
| `sync_queue_pending_total` | Nombre de fiches en attente de synchronisation |
| `notification_delivery_total` (succès/échec) | Taux de succès des notifications push |
| `redis_connected_clients` | Nombre de clients connectés à Redis |

### 7.5 Alertes automatiques

| Seuil d'alerte | Niveau | Canal |
|---|---|---|
| Temps de réponse API P95 > 5 s pendant 5 min | WARNING | Email ADMIN |
| Taux d'erreur HTTP 5xx > 5 % sur 10 min | CRITICAL | Email + SMS ADMIN |
| Génération PDF > 30 s | WARNING | Email ADMIN |
| File sync > 50 fiches en attente | WARNING | Email ADMIN |
| Mémoire JVM heap > 85 % | WARNING | Email ADMIN |
| Espace disque VPS > 80 % | CRITICAL | Email ADMIN |
| Keycloak health DOWN | CRITICAL | Email + SMS ADMIN |
| Backend health DOWN | CRITICAL | Email + SMS ADMIN |

---

## 8. Sécurité système

### 8.1 Authentification et gestion des identités — Keycloak

Keycloak est le **seul système d'authentification** du projet NG-Fields. Aucune gestion locale d'identité n'existe dans le backend Spring Boot ou dans les applications clientes.

**Responsabilités de Keycloak :**

| Responsabilité | Mécanisme Keycloak |
|---|---|
| Authentification des utilisateurs | Login Form Keycloak (Username + Password) |
| Gestion des sessions | SSO Session avec Refresh Token |
| Émission des tokens | Access Token JWT RS256, Refresh Token opaque |
| Gestion des rôles (RBAC) | Realm Roles mappés dans les claims JWT |
| Réinitialisation de mot de passe | Email de réinitialisation automatique |
| Verrouillage de compte | Brute Force Detection (5 tentatives → 30 min) |
| MFA (authentification multi-facteurs) | TOTP (Google Authenticator) obligatoire pour ADMIN et MANAGER |
| Expiration et révocation de session | Logout côté Keycloak + invalidation Refresh Token |

**Responsabilités du backend Spring Boot :**

| Responsabilité | Mécanisme Spring Security |
|---|---|
| Validation des Access Tokens | Vérification signature RS256 via JWKS Keycloak |
| Extraction des rôles | Mapping `realm_access.roles` → `ROLE_` Spring |
| Contrôle d'accès fin | `@PreAuthorize`, `@Secured`, règles HttpSecurity |
| Audit des accès | `AuditEventRepository` + table `audit_logs` |

### 8.2 Chiffrement des communications

| Connexion | Protocole | Certificat |
|---|---|---|
| Client → Nginx | HTTPS TLS 1.3 | Let's Encrypt (renouvellement auto Certbot) |
| Nginx → Spring Boot | HTTP (réseau Docker interne privé) | — |
| Nginx → Keycloak | HTTPS (ou HTTP réseau interne) | Let's Encrypt ou auto-signé interne |
| Spring Boot → PostgreSQL | SSL/TLS | Certificat Supabase |
| Spring Boot → Redis | TLS (option) + authentification password | — |
| Spring Boot → Services externes | HTTPS TLS 1.3 | Certificats serveurs vérifiés |
| Flutter → Backend | HTTPS TLS 1.3 + Certificate Pinning (production) | — |

**Headers de sécurité HTTP (Nginx) :**

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-Frame-Options "DENY" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Content-Security-Policy "default-src 'self'; ..." always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
```

### 8.3 Chiffrement des données au repos

| Données | Mécanisme |
|---|---|
| Base de données locale Flutter (Drift/SQLite) | SQLCipher (AES-256-CBC) |
| Tokens Flutter | flutter_secure_storage (Keychain iOS / Keystore Android) |
| PostgreSQL Supabase | Chiffrement AES-256 au niveau disque (Supabase) |
| Stockage objets (photos, PDFs) | AES-256 côté serveur (MinIO / Supabase Storage) |
| Secrets et variables d'environnement | GitHub Secrets + variables d'environnement VPS (non commitées) |

### 8.4 Protection contre les attaques courantes

| Vecteur | Contre-mesure |
|---|---|
| Injection SQL | Spring Data JPA / Hibernate (requêtes paramétrées exclusivement) |
| XSS | Angular escape natif + Content-Security-Policy |
| CSRF | Désactivé pour l'API REST stateless (JWT) ; activé sur les formulaires Keycloak |
| Brute-force login | Keycloak Brute Force Detection (5 tentatives, 30 min verrouillage) |
| Rate limiting API | Spring rate-limiter via Redis (100 req/min par IP) |
| DDoS | Nginx `limit_req_zone` + `limit_conn` |
| Interception HTTPS | Certificate Pinning Flutter en production |
| Exposition secrets | Scan automatique GitHub avec `git-secrets` + GitHub Advanced Security |
| Dépendances vulnérables | Dependabot (GitHub) + OWASP Dependency-Check dans la pipeline CI |

### 8.5 Conformité RGPD

| Exigence RGPD | Implémentation |
|---|---|
| Minimisation des données | Seules les données nécessaires sont collectées et persistées |
| Consentement documenté | Formulaire d'acceptation lors de la première connexion Keycloak |
| Droit à l'effacement | API Spring Boot `/api/admin/users/{id}/anonymize` (anonymisation RGPD) |
| Droit d'accès | Export JSON des données personnelles disponible depuis l'interface ADMIN |
| Registre des traitements | Documentation maintenue et accessible depuis le back-office ADMIN |
| Sauvegardes chiffrées | Backup quotidien PostgreSQL Supabase, rétention 30 jours, AES-256 |
| Portabilité des données | Export CSV/JSON disponible sur demande (rôle ADMIN) |
| Journalisation des accès | Table `audit_logs` avec toutes les actions sensibles (section 7.3) |

### 8.6 Gestion des secrets

- Aucun secret ne doit jamais être commis dans le dépôt Git (`.gitignore` obligatoire pour `.env`).
- En CI/CD : secrets injectés via **GitHub Secrets** (tokens, passwords, clés API).
- En production : variables d'environnement VPS + **Docker Secrets** pour les valeurs critiques.
- Rotation des clés de signature Keycloak : automatique (Keycloak gère la rotation des clés RS256 via le JWKS).
- Audit trimestriel des accès aux ressources cloud (Supabase, VPS Hetzner, services tiers).

---

## 9. Diagramme de déploiement

Le diagramme ci-dessous représente la configuration physique et logique de l'infrastructure NG-Fields en production, conformément aux conventions UML 2 du diagramme de déploiement (nœuds, artefacts, connexions).

```plaintext
╔═════════════════════════════════════════════════════════════════════════════════╗║                              ENVIRONNEMENT PRODUCTION — NG-FIELDS               ║
╠═════════════════════════════════════════════════════════════════════════════════╣
║                                                                               ║  ║  ┌────────────────────────────┐     ┌─────────────────────────────────────────  ║
║  │  Appareil Mobile Technicien│     │  Poste Manager / Directeur              │ ║
║  │  (iOS 14+ / Android 10+)   │     │  (Navigateur Chrome / Firefox / Edge)    │║
║  │                            │     │                                         │  
║  │  «artifact»                 │         │  «artifact»                       │  ║
║  │  ng-fields-mobile.apk/.ipa  │         │  app.ng-fields.ngs.tg             │  ║
║  │  Flutter 3.x                │         │  Angular 17+ (SSR ou SPA)         │  ║
║  │  ┌─────────────────────┐    │         │  angular-oauth2-oidc               │ ║
║  │  │ Drift DB (SQLCipher) │   │    ──────────────────┬──────────────────────┘  ║
║  │  │ flutter_secure_store │   │                            │ HTTPS/TLS 1.3     ║
║  │  │ flutter_appauth      │   │                            │                   ║
║  │  └─────────────────────┘   │                            │                    ║
║  └──────────────┬──────────────┘                            │                   ║
║                 │ HTTPS/TLS 1.3                             │                   ║
║                 │                                           │                   ║
║  ┌──────────────▼───────────────────────────────────────────▼────────────────────┐ ║
║  │              VPS Hetzner CX31 — Ubuntu 24 (4 vCPU, 8 Go RAM, 80 Go SSD)   │  ║
║  │                                                                           │  ║
║ ┌─────────────────────────────────────────────────────────────────────────────┐│║
║  │  │  «artifact»  Nginx Alpine (Docker container)                         │ │  ║
║  │  │  Reverse Proxy + TLS Let's Encrypt (Certbot)                         │ │  ║
║  │  │  Ports : 80 → redirect 443 | 443 → routing                           │ │  ║
║  │  │  Routes :                                                            │ │  ║
║  │  │    /api/*          → Spring Boot :8080                               │ │  ║
║  │  │    /auth/*         → Keycloak :8080                                  │ │  ║
║  │  │    /*              → Angular (fichiers statiques ou Next.js)         │ │  ║
║  │  └──────┬──────────────────────────────┬──────────────────────────────────────┘ │  ║
║  │         │  Port 8080 (interne)          │  Port 8180 (interne)            │  ║
║  │  ┌──────▼──────────────────┐    ┌───────▼──────────────────────────────────────┐│  ║
║  │  │  «artifact»              │    │  «artifact»                                  ││  ║
║  │  │  Spring Boot 3.x (Java)  │    │  Keycloak 24.x                               ││  ║
║  │  │  ng-fields-api:latest    │    │  Realm : ng-fields                           ││  ║
║  │  │  «component»             │    │  Clients OIDC :                              ││  ║
║  │  │  ├── SecurityConfig      │    │   ng-fields-web (public)                     ││  ║
║  │  │  ├── InterventionSvc     │    │   ng-fields-mobile (public)                  ││  ║
║  │  │  ├── ClientSvc           │    │   ng-fields-backend (confidential)           ││  ║
║  │  │  ├── PdfService          │    │  Flux : Auth Code + PKCE                     ││  ║
║  │  │  ├── NotificationSvc     │    │  Tokens : JWT RS256 (15 min)                 ││  ║
║  │  │  ├── SyncService         │    │  JWKS : /realms/ng-fields/                   ││  ║
║  │  │  └── StorageService      │    │  MFA TOTP : ADMIN + MANAGER                  ││  ║
║  │  └──────────────┬───────────┘    └────────────────────────────────────────────┘│  ║
║  │                 │                                                                 │  ║
║  │  ┌──────────────▼──────────────────────────────────────────────────────────────┐│  ║
║  │  │  «artifact»  Redis 7 Alpine (Docker container)                               ││  ║
║  │  │  ├── Cache API (Spring Data Redis)                                           ││  ║
║  │  │  └── Queues asynchrones :                                                    ││  ║
║  │  │       pdf_queue | email_queue | push_queue | sync_queue             ││  ║
║  │  └─────────────────────────────────────────────────────────────────────────────┘│  ║
║  │                                                                                   │  ║
║  │  ┌─────────────────────────────────────────────────────────────────────────────┐ │  ║
║  │  │  «artifact»  MinIO (Docker container — S3-compatible)                        │ │  ║
║  │  │  ├── Bucket : intervention-photos (JPEG/PNG + métadonnées EXIF)             │ │  ║
║  │  │  └── Bucket : intervention-reports (PDF générés, AES-256)                   │ │  ║
║  │  └─────────────────────────────────────────────────────────────────────────────┘ │  ║
║  └───────────────────────────────────────────────────────────────────────────────────┘  ║
║                                                                                          ║
╚══════════════════════════════════════════════════════════════════════════════════════════╝
      │                  │                  │                      │                │
      │ SSL/TCP           │ HTTPS            │ HTTPS                │ HTTPS          │ SMTP TLS
      ▼                  ▼                  ▼                      ▼                ▼
┌──────────────┐  ┌───────────────┐  ┌───────────────┐  ┌───────────┐
│ Supabase     │  │ FCM (Android) │  │ Resend        │  │ SMTP      │
│ PostgreSQL   │  │ APNs (iOS)    │  │ (API REST)    │  │ / SMTP    │
│ (Cloud)      │  │               │  │               │  │           │
│ ├── SSL AES  │  │ Firebase      │  │ API Key       │  │ API Key   │
│ ├── Backup   │  │ Admin SDK     │  │               │  │ Port 587  │
│ └── Flyway   │  │               │  │               │  │           │
│   migrations │  │               │  │               │  │           │
└──────────────┘  └───────────────┘  └───────────────┘  └───────────┘
```


---

---
### Légende des connexions

| Connexion | Protocole | Sécurisation |
|---|---|---|
| Flutter / Angular → Nginx | HTTPS | TLS 1.3, certificat Let's Encrypt |
| Flutter → Keycloak (OIDC) | HTTPS | Authorization Code + PKCE via flutter_appauth |
| Angular → Keycloak (OIDC) | HTTPS | Authorization Code + PKCE via angular-oauth2-oidc |
| Nginx → Spring Boot | HTTP | Réseau Docker interne privé (non exposé) |
| Nginx → Keycloak | HTTP | Réseau Docker interne privé |
| Spring Boot → Keycloak (JWKS) | HTTPS | Validation signature JWT RS256 |
| Spring Boot → PostgreSQL (Supabase) | SSL/TCP | Certificat Supabase, pool HikariCP |
| Spring Boot → Redis | TCP (+ auth password) | Réseau Docker interne |
| Spring Boot → MinIO | HTTP (interne) ou HTTPS | Réseau Docker interne ou S3 HTTPS |
| Spring Boot → FCM | HTTPS | Service Account JSON Firebase |
| Spring Boot → APNs | HTTPS/2 | Clé privée `.p8` |
| Spring Boot → Resend | HTTPS | API Key |
| Flutter → Spring Boot | HTTPS | TLS 1.3 + Certificate Pinning (prod) |

---

## 10. Conclusion

Ce document de capture des besoins techniques (version 2.0) définit le cadre technique définitif du projet NG-Fields, intégrant les choix technologiques arrêtés lors du Sprint 0 : **Spring Boot** pour le backend, **Angular** pour l'application web, **Flutter** pour l'application mobile et **Keycloak** pour l'authentification centralisée.

Les quatre points structurants du projet sont les suivants :

**Une authentification d'entreprise avec Keycloak.** Le choix de Keycloak comme IAM centralisé apporte une sécurité de niveau professionnel au projet NG-STARs : gestion OIDC/OAuth 2.1 standardisée, RBAC granulaire, MFA TOTP pour les rôles sensibles, audit natif des connexions et évolutivité vers la fédération d'identité. Le backend Spring Boot délègue intégralement la validation des tokens à Keycloak via JWKS, garantissant un système stateless robuste.

**Une architecture offline-first non négociable.** La réalité terrain de NG-STARs impose une conception Flutter fondée sur la persistance locale chiffrée (Drift + SQLCipher) et la synchronisation automatique différée via SyncService. Le token Keycloak est stocké dans `flutter_secure_storage` pour permettre la continuité de l'expérience utilisateur même en zone sans réseau.

**Un écosystème Java/Spring cohérent et maîtrisé.** Spring Boot 3.x (Java 21) offre une intégration native avec Keycloak via `spring-security-oauth2-resource-server`, un ORM mature (Spring Data JPA + Hibernate), des migrations versionnées (Flyway), une supervision intégrée (Actuator + Micrometer/Prometheus) et un vaste écosystème de bibliothèques matures pour tous les besoins du projet (PDF, email, push, S3).

**Une interopérabilité maîtrisée avec les systèmes externes.** Les trois intégrations critiques (Keycloak, Email, FCM/APNs) disposent chacune d'un mécanisme de relance automatique (Redis queues) et, le cas échéant, d'un canal de secours, garantissant la continuité de service même en cas d'indisponibilité partielle d'un service tiers.

Ce document constitue la référence technique du projet à compter de sa date de validation officielle et devra être mis à jour à chaque évolution significative de l'architecture ou des choix technologiques.

---

| Auteur du Document | Validateur Technique | Validateur Final |
|---|---|---|
| Nelson Emmanuel FOLLY | Barnabé MIDJRATO | David KATOH |
| Signature : ___________ | Signature : ___________ | Signature : ___________ |
| Date : _______________ | Date : _______________ | Date : _______________ |
