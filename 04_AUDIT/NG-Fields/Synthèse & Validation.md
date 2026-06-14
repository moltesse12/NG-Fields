---
tags:
  - audit
  - ng-fields
  - synthèse
  - validation
created: 2026-04-15
modified: 2026-04-15
status: validé
---

# Synthèse des Réponses - NG-Fields

## 🎯 Points Clés Identifiés

---

## 📊 Contexte & Volume

| Critère | Réponse |
|---------|---------|
| Volume interventions | **10-50/mois** |
| Nombre techniciens | **15** |
| Managers/Admins | **10** |
| Clients (optionnel) | **999** (potentiel) |

---

## 👥 Équipe & Compétences

| Ressource | Niveau |
|-----------|--------|
| Développeur React Native | ✅ Senior |
| Développeur Node.js | ✅ Senior |
| DevOps | ✅ Senior |
| Designer UI/UX | ✅ Senior |
| **Compétences globales** | **ÉLEVÉES** |

---

## 📱 Appareils & Plateformes

- **Smartphones** : Android + iOS
- **Tablettes** : Oui
- **Navigateur web** : Oui

---

## ⚙️ Fonctionnalités MVP

### 🔴 Obligatoires
- Formulaire intervention complet (identique à la fiche papier)
- Signature numérique (3 signataires : Client + Technicien + Responsable)
- Photos (3-5 par intervention)
- GPS/Localisation
- Envoi email avec PDF
- Génération PDF complète (logo, photos, signature, QR code)
- Gestion clients
- RGPD obligatoire

### 🟠 Importantes
- Mode hors-ligne (essentiel - zones rurales souvent)
- Envoi WhatsApp

### 🟡 Souhaitables
- Dashboard manager
- Annotations photos (non - non demandé)

---

## 🔐 Sécurité & Conformité

| Exigence | Statut |
|----------|--------|
| RGPD | ✅ Obligatoire |
| Chiffrement données | ✅ |
| MFA | ✅ |
| Audit trail (logs) | ✅ |
| Sauvegarde quotidienne | ✅ |
| Plan de reprise (PRA) | ✅ |

---

## 🎨 Identité Visuelle

| Élément | Détail |
|---------|--------|
| Logo NG-STARs | ✅ HD disponible |
| Charte graphique | ✅ Disponible |
| Couleur principale | Spaceblue (21, 73, 99) |
| Couleur secondaire | Auroraglow (154, 197, 123) |
| Light | (198, 223, 233) |
| Dark | (8, 15, 21) |

---

## 💰 Budget & Délai

| Critère | Réponse |
|---------|---------|
| Budget disponible | **< 100€** |
| Coûts récurrents | ❌ Non acceptés |
| Date lancement | **30/06/2026** (3 mois après 31/03/2026) |
| Méthode tests | **Agile** |
| Jalon validation CdC | **15/04/2026** ✅ |

---

## 🏗️ Infrastructure Existante

| Service | Status |
|---------|--------|
| Cloud (AWS/Azure/GCP) | ✅ Utilisé |
| Serveur dédié | ✅ Utilisé |
| Hébergement mutualisé | ✅ Utilisé |
| PostgreSQL | ✅ Utilisé |
| Git/GitHub/GitLab | ✅ Utilisé |

---

## 📈 Fonctionnalités Spéciales

| Fonctionnalité | Statut |
|----------------|--------|
| Export CSV/Excel | ✅ Régulier |
| Historique client | ✅ Essentiel |
| QR code dans PDF | ✅ |
| Légendes photos | Optionnel |
| Annotations photos | ❌ Non |

---

## 🔔 Notifications

| Événement | Notification |
|-----------|--------------|
| Intervention créée | ✅ |
| Intervention terminée | ✅ |
| Intervention en retard | ✅ |
| Nouveau message client | ✅ |

**Destinataires** : Manager, Responsable, Tous techniciens

---

## 📋 Intégrations

| Système | Intégration |
|---------|-------------|
| OpenProject | ✅ Oui (projet séparé après lancement) |
| ERP | ❌ Non |
| CRM | ❌ Non |
| Comptabilité | ❌ Non |

---

## 📊 Volume Données

| Métrique | Estimation |
|----------|-----------|
| Utilisateurs simultanés | 20 |
| Interventions/jour (pic) | 10 |
| Stockage photos | 15 Go/mois |

---

## ✅ Points Validés

### Périmètre MVP
- [x] Formulaire intervention complet (exactement comme la fiche papier)
- [x] Signature numérique (Client + Technicien + Responsable)
- [x] Photos (3-5 par intervention)
- [x] GPS/Localisation
- [x] Mode hors-ligne (essentiel)
- [x] Envoi email avec PDF
- [x] Envoi WhatsApp
- [x] Génération PDF (logo, photos, signature, QR code)
- [x] Gestion clients (historique essentiel)
- [x] RGPD obligatoire

### Stack Technique
- [x] **Application sur mesure** (React Native + Expo)
- [x] **Backend API** (Node.js)
- [x] **Base de données** (PostgreSQL - existant)
- [x] **Hébergement** : Cloud existant + Serveur dédié
- [x] **Sécurité** : MFA, Chiffrement, Logs, Backup, PRA

### Ressources
- [x] **Équipe senior complète** disponible
- [x] **Budget < 100€** → Outils gratuits
- [x] **Logo et charte graphique** disponibles

---

## ❌ Points Exclus (v1)

- Connexion OpenProject (projet séparé après lancement)
- Annotations sur photos

---

## ⚠️ Points d'Attention (Risques)

| Risque | Mitigation |
|---------|------------|
| **Budget < 100€** incompatible développement classique | Outils gratuits : Expo, Firebase, Supabase, Cloudinary free tier |
| **Mode hors-ligne essentiel** | WatermelonDB ou SQLite obligatoire |
| **Migration données papier** | Scanner + OCR ou saisie manuelle |
| **Délai serré (3 mois)** | Prioriser MVP strict |

---

## 💡 Solution Technique Adaptée au Budget

| Composant | Solution Gratuite |
|-----------|------------------|
| Mobile | Expo (gratuit) |
| Backend | Node.js auto-hébergé (existant) |
| Database | PostgreSQL (existant) |
| Auth | Firebase Auth (gratuit < 50k users) |
| Storage (photos) | Cloudinary (500MB gratuit) ou S3 (existant) |
| Email | SendGrid (500 emails/jour gratuit) |
| Monitoring | Sentry (gratuit) |
| CI/CD | GitHub Actions (gratuit) |

**Coût estimé / mois** : ~0-10€

---

## 📅 Planning Suggéré

| Phase | Durée | Dates |
|-------|-------|-------|
| Validation CdC + Setup | 1 sem. | 15-21/04/26 |
| Sprint 1-2 (Auth, Dashboard, Formulaire) | 4 sem. | 21/04 - 19/05/26 |
| Sprint 3-4 (Signature, Photos, PDF) | 4 sem. | 19/05 - 16/06/26 |
| Sprint 5 (Offline, Tests, Bug fixes) | 2 sem. | 16/06 - 30/06/26 |
| **Lancement MVP** | | **30/06/2026** ✅ |

---

## 📋 Validé Par

| Rôle | Nom | Date | Signature |
|------|-----|------|-----------|
| Chef de projet | Barnabe | 15/04/26 | |
| DSI | David | 15/04/26 | |

---

## 🔗 Liens

- [[04_AUDIT/NG-Fields/Index]]
- [[04_AUDIT/NG-Fields/Questions - Contexte & Objectifs]]
- [[04_AUDIT/NG-Fields/Questions - Utilisateurs & Périmètre]]
- [[04_AUDIT/NG-Fields/Questions - Fonctionnalités]]
- [[04_AUDIT/NG-Fields/Questions - Technique & Budget]]
- [[06_PROJETS/Projet_NG-Fields/Cahier des charges - NG-Fields]]
