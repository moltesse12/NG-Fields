---
tags:
  - processus
  - it
created: 
modified: 
status: draft
---

# Processus IT

## 🎯 Objectif
> _Documenter les processus de gouvernance et d'exploitation IT_

## 📋 Processus Clés

### 1. Gestion des Incidents

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│   N1     │───►│   N2     │───►│   N3     │───►│  Externe │
│ (User)   │    │ (Tech)   │    │ (Expert) │    │ (Vendor) │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
```

| Niveau | Description | SLA | Exemples |
|--------|-------------|-----|----------|
| N1 | Résolution niveau 1 | 4h | Mot de passe, déconnexion |
| N2 | Support technique | 8h | Config, réseau |
| N3 | Expertise | 24h | Développement, infra |

### 2. Gestion des Changements

| Étape | Description | Responsable |
|-------|-------------|-------------|
| 1. Demande | Formulaire de changement | Demandeur |
| 2. Analyse | Impact, risque, coût | DSI |
| 3. Validation | CAB (Change Advisory Board) | COMEX |
| 4. Planification | Date, ressources | DSI |
| 5. Release | Déploiement | Équipe tech |
| 6. Post-mortem | Bilan, lessons learned | DSI |

### 3. Gestion des Projets IT

| Phase | Livrables | Durée |
|-------|-----------|-------|
| Initiation | Charter projet, RACI | 1 sem. |
| Planification | WBS, planning, budget | 2 sem. |
| Exécution | Deliverables | _varie_ |
| Tests | UAT, recettage | 2-4 sem. |
| Déploiement | Mise en prod | 1 sem. |
| Clôture | Bilan, transition | 1 sem. |

### 4. Gestion de la Sécurité

| Processus | Fréquence | Responsable |
|-----------|-----------|-------------|
| Scan vulnérabilités | Hebdomadaire | SecOps |
| Patch management | Mensuel | Infra |
| Test pentest | Annuel | Externe |
| Revue accès | Trimestriel | IAM |

### 5. Backup & Disaster Recovery

| Aspect | RPO | RTO |
|--------|-----|-----|
| Applications critiques | 1h | 4h |
| Données non-critiques | 24h | 48h |
| Postes de travail | 24h | 72h |

## ⚠️ Points d'Amélioration

| Problème | Impact | Solution |
|----------|--------|----------|
| Pas de catalogue услуг | Moyen | Créer [[06_PROJETS/Projet_App_Unique]] |
| Suivi incidents manuel | Élevé | Outil ITSM |
| Documentation incomplète | Moyen | Wiki technique |

## 🔗 Liens

- [[05_SYSTEME_IT/Architecture Actuelle]]
- [[05_SYSTEME_IT/Architecture Cible]]
- [[04_AUDIT/Audit IT]]
- [[06_PROJETS/Projet_Automatisation]]
- [[08_PROCESSUS/Index]]
