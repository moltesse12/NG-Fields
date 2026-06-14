---
tags:
  - architecture
  - actuelle
created: 
modified: 
status: documenté
---

# Architecture Actuelle - NG-STARs

## 🎯 Objectif
> _Documenter l'état actuel du système d'information_

## 🏗️ Vue d'Ensemble

```
┌─────────────────────────────────────────────────────────────┐
│                        UTILISATEURS                          │
│         ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│         │ Postes   │  │ Postes   │  │ Postes   │          │
│         │ WINDOWS  │  │ MAC      │  │ LINUX    │          │
│         └────┬─────┘  └────┬─────┘  └────┬─────┘          │
└──────────────┼─────────────┼─────────────┼──────────────────┘
               │             │             │
               └─────────────┼─────────────┘
                             │
                    ┌────────▼────────┐
                    │   RÉSEAU LAN    │
                    │  (Switch/FW)    │
                    └────────┬────────┘
                             │
     ┌───────────────────────┼───────────────────────┐
     │                       │                       │
┌────▼────┐           ┌─────▼─────┐           ┌─────▼─────┐
│ SERVEURS │           │  SERVEURS │           │  CLOUD    │
│ On-Prem  │           │  Virtual  │           │ (SaaS)    │
└──────────┘           └───────────┘           └───────────┘
```

## 🖥️ Infrastructure

### Serveurs

| Serveur | OS | Rôle | CPU/RAM | Criticité |
|---------|----|------|---------|-----------|
| _srv-01_ | | | | Critique |
| _srv-02_ | | | | Élevée |

### Stockage

| Type | Capacité | Utilisation |
|------|----------|-------------|
| SAN/NAS | _To_ | Fichiers |
| Backup | _To_ | Sauvegarde |

### Réseau

| Composant | Modèle | Rôle |
|-----------|--------|------|
| Firewall | | |
| Switch principal | | |
| WiFi | | |

## 📦 Applications

### ERP
| Champ | Valeur |
|-------|--------|
| Solution | _à compléter_ |
| Version | |
| Utilisateurs | |
| Hébergement | On-Prem/Cloud |

### CRM
| Champ | Valeur |
|-------|--------|
| Solution | |
| Version | |
| Utilisateurs | |

### Autres Applications

| Application | Département | Criticité |
|-------------|-------------|-----------|
| | | |

## ☁️ Services Cloud

| Service | Provider | Usage |
|---------|----------|-------|
| | | |
| | | |

## ⚠️ Points de Fragilité

| Problème | Risque | Impact |
|----------|--------|--------|
| Pas de redondance | Élevé | Indisponibilité |
| | | |

## 📊 Coûts IT Annuels

| Poste | Coût estimé |
|-------|-------------|
| Matériel | _€_ |
| Licences | _€_ |
| Cloud | _€_ |
| Maintenance | _€_ |
| **Total** | **_** |

## 🔗 Liens

- [[05_SYSTEME_IT/Architecture Cible]]
- [[05_SYSTEME_IT/Stack Technique]]
- [[04_AUDIT/Audit IT]]
- [[10_MEETINGS/Interview DSI]]
