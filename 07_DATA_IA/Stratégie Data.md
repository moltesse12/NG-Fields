---
tags:
  - data
  - stratégie
  - ia
created: 
modified: 
status: draft
---

# Stratégie Data & IA - NG-STARs

## 🎯 Objectif
> _Faire de la donnée un actif stratégique pour piloter et innover_

## 📊 État des Lieux

### Données Disponibles

| Source | Type | Volume | Qualité |
|--------|------|--------|---------|
| ERP | Transac. | _To_ | Bonne |
| CRM | Client | _To_ | Moyenne |
| Log. production | IoT | _Go_ | Variable |
| Web | Comport. | _Go_ | Bonne |

### Problèmes Identifiés
> _Basé sur [[04_AUDIT/Audit IT]]_

- [ ] Données silotées par département
- [ ] Pas de gouvernance centralisée
- [ ] Qualité de données inconsistante
- [ ] Pas d'outils analytics accessibles

## 🎯 Vision Data 2028

```
┌─────────────────────────────────────────────────────────────┐
│                      PYRAMIDE DATA                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                         AI / ML                             │
│                    ┌──────────────┐                         │
│                    │ Prédictions  │                         │
│                    │ Automatisation│                         │
│                    └──────┬───────┘                         │
│                           │                                 │
│                      ANALYTICS                              │
│                    ┌──────────────┐                         │
│                    │ Dashboards   │                         │
│                    │ Rapports     │                         │
│                    └──────┬───────┘                         │
│                           │                                 │
│                   DATA WAREHOUSE                            │
│                    ┌──────────────┐                         │
│                    │ Centralisation│                        │
│                    │ Consolidation │                        │
│                    └──────┬───────┘                         │
│                           │                                 │
│                   DATA LAKE / LAKEHOUSE                     │
│                    ┌──────────────┐                         │
│                    │ Raw Data     │                         │
│                    │ Toutes sources│                        │
│                    └──────────────┘                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 🏗️ Architecture Data Cible

### Composants

| Composant | Solution | Rôle |
|-----------|----------|------|
| Ingestion | Airbyte/Fivetran | ETL/ELT |
| Storage | Snowflake/Cloud | Data Lake |
| Processing | Spark/dbt | Transformation |
| BI | Power BI/Tableu | Visualisation |
| ML | MLflow/AWS SageMaker | IA |

### Flux de Données

```
Sources        Ingestion        Storage         Analytics        Consommateurs
─────────      ────────         ───────         ─────────        ─────────────
ERP      ───►  API Connectors  ───► Data Lake  ───► Dashboards  ───► Mgmt
CRM      ───►  CDC/ETL         ───► DW          ───► Rapports    ───► Finance
Apps     ───►  Event Stream    ───► Curated     ───► ML Models   ───► Ops
IoT      ───►  Batch/Realtime  ───► Gold Layer  ───► API         ───► Products
```

## 💡 Cas d'Usage IA

> _Voir : [[07_DATA_IA/Cas d'usage IA]]_

### Priorité 1 - Quick Wins
| Use Case | Impact | Complexité | ROI |
|---------|--------|------------|-----|
| Chatbot support | Élevé | Faible | Rapide |
| Prédiction churn | Élevé | Moyenne | Moyen |

### Priorité 2 - Valeur Ajoutée
| Use Case | Impact | Complexité |
|---------|--------|------------|
| Prédiction demande | Élevé | Élevée |
| Maintenance prédictive | Élevé | Élevée |

## 👥 Gouvernance Data

### Rôles

| Rôle | Responsable | Mission |
|------|-------------|---------|
| CDO (Chief Data Officer) | _À nommer_ | Stratégie data |
| Data Architect | _À recruter_ | Architecture |
| Data Engineer | _À recruter_ | Pipeline |
| Data Analyst | _À définir_ | Analyse |
| Data Steward | Par dept | Qualité |

### Politiques

| Politique | Description |
|-----------|-------------|
| Data Quality | Standards de qualité |
| Data Security | Classification et accès |
| Data Retention | Durée de conservation |
| Metadata | Documentation obligatoire |

## 📅 Roadmap

### 2024 - Fondations
- [ ] Déployer Data Lake
- [ ] Premiers dashboards
- [ ] Chatbot support

### 2025 - Consolidation
- [ ] Data Warehouse complet
- [ ] Data Governance opérationnel
- [ ] 3 cas ML en prod

### 2026-2028 - Excellence
- [ ] IA embarquée dans les processus
- [ ] Data mesh / domain-driven
- [ ] Auto-ML

## 💰 Budget Estimé

| Poste | Annuel |
|-------|--------|
| Outils/Licences | _k€_ |
| Équipe (3-5 pers.) | _k€_ |
| Cloud/Infrastructure | _k€_ |
| Formation | _k€_ |
| **Total** | **_** |

## 📊 KPIs Data

| KPI | Actuel | Cible 2025 | Cible 2028 |
|-----|--------|-----------|------------|
| Disponibilité dashboards | 0 | 20 | 50 |
| Couverture qualité | _%_ | 60% | 90% |
| Cas ML en prod | 0 | 3 | 10 |

## 🔗 Liens

- [[07_DATA_IA/Dashboard]]
- [[07_DATA_IA/Cas d'usage IA]]
- [[07_DATA_IA/Modèles IA]]
- [[06_PROJETS/Projet_Data]]
- [[06_PROJETS/Projet_IA]]
- [[05_SYSTEME_IT/Architecture Cible]]
