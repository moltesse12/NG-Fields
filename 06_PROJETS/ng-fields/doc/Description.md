---
tags:
  - projet
  - ng-fields
  - description
created: 2026-04-15
modified: 2026-05-18
status: validé
priority: haute
---

# Projet NG-Fields — SDCGI

> **Solution de Digitalisation et Centralisation de la Gestion des Interventions terrain (SDCGI)**

##  Objectif

Digitaliser et centraliser entièrement la gestion des interventions terrain de NG-STARs à travers le développement d'une application mobile et web performante, supprimer l'usage du papier, garantir une traçabilité complète des interventions, améliorer la réactivité du management et fournir des données fiables et exploitables pour le pilotage ainsi que l'amélioration continue des services.

##  Description

Application mobile-first de gestion des interventions terrain pour techniciens NG-STARs, permettant la saisie des fiches sur mobile/tablette, avec signature numérique (3 signataires : client, technicien, responsable), génération PDF automatique et envoi email, avec fonctionnement garanti en zone de faible connectivité.

### Contexte
NG-STARs utilise actuellement une fiche d'intervention papier (FI-01-2025) de 3 pages pour documenter les prestations techniques chez les clients.

### Volume actuel
| Critère            | Valeur    |
| ------------------ | --------- |
| Interventions/mois | **10-50** |
| Techniciens        | **15**    |
| Managers/Admins    | **10**    |
| Clients potentiels | **999**+  |

### Problématique identifiée
| #   | Problème                                     | Impact       |
| --- | -------------------------------------------- | ------------ |
| 1   | Perte de temps pour récupérer la fiche au RH | Productivité |
| 2   | Saisie manuelle sujette aux erreurs          | Qualité      |
| 3   | Pas de preuve de présence (GPS)              | Traçabilité  |
| 4   | Pas de photos du problème initial            | Preuves      |
| 5   | Calcul manuel de la durée                    | Erreurs      |
| 6   | Pas de traçabilité photos avant/après        | Preuves      |
| 7   | Signature papier → risque de perte           | Archivage    |
| 8   | Archivage physique → recherche difficile     | Efficacité   |
| 10  | Pas de notification automatique              | Réactivité   |
| 11  | Pas de suivi en temps réel                   | Management   |
| 12  | Pas de statistiques automatisées             | Pilotage     |

### Solution Proposée
| Fonctionnalité | Priorité | Status |
|----------------|----------|--------|
| Formulaire intervention (identique fiche papier) | 🔴 Obligatoire | ✅ Implémenté |
| Signature numérique (Client + Technicien + Responsable) | 🔴 Obligatoire | ✅ Implémenté |
| Photos (3-5 par intervention) | 🟠 Important | ✅ Implémenté |
| GPS/Localisation | 🟠 Important | ✅ Implémenté |
| Mode hors-ligne | 🟡 Souhaitable | ✅ Implémenté (API batch sync) |
| Envoi email avec PDF | 🔴 Obligatoire | ✅ Implémenté |
| Génération PDF (logo, photos, signature, QR code) | 🔴 Obligatoire | ✅ Implémenté |
| Dashboard manager | 🟡 Souhaitable | ✅ Implémenté (API + Angular 22+) |
| Gestion clients avec historique | 🟠 Important | ✅ Implémenté |
| RGPD | 🔴 Obligatoire | ✅ Implémenté |
| Gestion entreprises multi-tenant | 🟠 Important | ✅ Implémenté (API) |
| RBAC CLIENT (CLIENT_ADMIN/USER/VIEWER) | 🟠 Important | ✅ Implémenté |
| Notifications push Firebase | 🟠 Important | ✅ Implémenté (conditional) |
| SSE Real-time Dashboard | 🟡 Souhaitable | ✅ Implémenté (API) |
| Planning techniciens | 🟠 Important | ✅ Implémenté (API) |
| Actuator + Métriques + Logs JSON | 🟡 Souhaitable | ✅ Implémenté |
| Tests unitaires (65 tests) | 🔴 Obligatoire | ✅ Implémenté |
| Clean Architecture (DTOs projection) | 🟡 Souhaitable | ✅ Implémenté |

## 👥 Parties Prenantes

| Rôle                    | Nom                   | Responsabilité                                                                                               |
| ----------------------- | --------------------- | ------------------------------------------------------------------------------------------------------------ |
| Validateur technique    | David KATOH           | Responsable IT                                                                                               |
| Chef de projet          | Barnabé MIDJRATO      | Pilotage                                                                                                     |
| Rédacteur / Développeur | FOLLY Nelson Emmanuel | Analyse, conception, développement                                                                           |
| Directeur               | Rachid DERMAN         | -Validation stratégique                                                  -Décision stratégique et budgétaire |
|                         |                       |                                                                                                              |

## 💰 Budget & Délai

| Critère | Valeur |
|---------|--------|
| Coût initial | **0 €** |
| Coûts récurrents | **0 €** |
| Hébergement | Supabase (free tier) |
| Base de données | PostgreSQL via Supabase (500MB free) |
| Storage photos | Supabase Storage (1GB free) |
| Méthodologie | Scrum (sprints 1 semaine) |
| Nombre de sprints | **7 sprints (V0-S1 à V1-S2)** |
| Date début | **1 juin 2026** |
| Date fin | **--** |

## 👨‍💻 Équipe Technique

| Rôle                  | Compétences                        | Status             |
| --------------------- | ---------------------------------- | ------------------ |
| Développeur principal | Spring Boot (Java), Angular 22+ (TypeScript) | ✅ Stagiaire        |
| DevOps                | CI/CD, Docker, Supabase            | ✅ Disponible       |
| Designer UI/UX        | Maquettes, assets                  | ✅ Disponible       |
| Testeurs              | 3 techniciens (UAT)                | ✅ En fin de projet |

**Stack retenu :** Spring Boot 4.1.0 (Java 25) + Angular 22+ (TypeScript) + Keycloak 26.6.4 + PostgreSQL 18.

## 📱 Appareils Cibles

| Appareil | OS | Support |
|----------|-----|---------|
| Smartphones | Android | ✅ Obligatoire |
| Smartphones | iOS | ✅ Obligatoire |
| Tablettes | Android/iOS | ✅ Obligatoire |
| Navigateur web | Chrome/Safari/Edge | ✅ Dashboard manager |

**Niveau technologique** : Les techniciens sont **très à l'aise** avec les applications mobiles.

## 🌐 Contraintes de Connexion

| Lieu | Qualité | Fréquence |
|------|---------|-----------|
| Bureau/Siège | Bonne | Toujours |
| Client principal | Bonne/Moyenne | ~70% |
| Client secondaire | Moyenne/Faible | ~30% |
| Zones rurales | Faible | ~20% |

**Conclusion** : Le mode offline est **ESSENTIEL** — la solution doit être conçue en **offline-first** car ~50% des interventions se font en zone de faible connexion.

## 🔐 Sécurité & Conformité

| Exigence                                    | Status |
| ------------------------------------------- | ------ |
| RGPD                                        | ✅      |
| Chiffrement TLS 1.3                         | ✅      |
| JWT + MFA                                   | ✅      |
| Audit trail (logs)                          | ✅      |
| Sauvegarde quotidienne (rétention 30 jours) | ✅      |
| Plan de reprise (PRA)                       | ✅      |

## 🎨 Identité Visuelle

| Élément | Valeur |
|---------|--------|
| Logo NG-STARs | ✅ HD disponible |
| Charte graphique | ✅ Disponible |
| Couleur principale | Spaceblue (RGB: 21, 73, 99) |
| Couleur secondaire | Auroraglow (RGB: 154, 197, 123) |
| Light | (RGB: 198, 223, 233) |
| Dark | (RGB: 8, 15, 21) |

## 🔗 Liens

### Sources :
- [[04_AUDIT/NG-Fields/Questions - Contexte & Objectifs]]
- [[04_AUDIT/NG-Fields/Questions - Utilisateurs & Périmètre]]
- [[04_AUDIT/NG-Fields/Questions - Fonctionnalités]]
- [[04_AUDIT/NG-Fields/Questions - Technique & Budget]]
- [[04_AUDIT/NG-Fields/Synthèse & Validation]]
- [[99_ARCHIVES/FI-01-2025_MODEL V2]]

### Fichiers projet :
- [[06_PROJETS/Projet_NG-Fields/Cahier des charges - NG-Fields]]
- [[06_PROJETS/Projet_NG-Fields/Objectifs]]
- [[06_PROJETS/Projet_NG-Fields/Architecture Technique]]
- [[06_PROJETS/Projet_NG-Fields/Roadmap]]
- [[06_PROJETS/Projet_NG-Fields/Technologies]]
- [[06_PROJETS/Projet_NG-Fields/Backlog]]
- [[06_PROJETS/Projet_NG-Fields/Plan Développement MVP]]
