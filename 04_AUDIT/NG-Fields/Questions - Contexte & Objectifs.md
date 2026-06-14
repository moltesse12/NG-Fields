---
tags:
  - audit
  - ng-fields
  - questions
  - contexte
created: 2026-04-15
modified: 
status: complété
---

# Questions - Contexte & Objectifs

## 🎯 Objectif
> _Comprendre le contexte et définir les objectifs du projet_

---

## 1. Contexte Métier

### Q1 : Quel est le volume actuel d'interventions ?
> _Combien de fiches d'intervention sont réalisées par semaine/mois ?_

**Réponse** :
- [ ] < 10 interventions/mois
- [x] 10-50 interventions/mois ✅ 2026-04-15
- [ ] 50-100 interventions/mois
- [ ] > 100 interventions/mois

### Q2 : Combien de techniciens utilisent les fiches ?
> _Combien de personnes seront amenées à utiliser l'application ?_

**Réponse** : 15

### Q3 : Quel est le processus actuel exact ?
> _Décrivez le processus actuel de A à Z_

**Réponse** :

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     PROCESSUS ACTUEL - FICHE D'INTERVENTION                  │
└─────────────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════
PHASE 1 : DÉPART (Au siège NG-STARs)
═══════════════════════════════════════════════════════════════════════════════

  1.  Le technicien est notifié d'une intervention (ticket OpenProject ou demande)
  
  2.  Le technicien informe son responsable hiérarchique de son départ
  
  3.  Le technicien se rend au département RH pour retirer la fiche FI-01-2025
  
  4.  Le technicien renseigne les informations PRÉALABLES sur la fiche :
      ├── REF (référence intervention)
      ├── Date du jour
      ├── Numéro d'intervention
      ├── Nom de l'entreprise cliente
      ├── Nom de l'intervenant / Administrateur
      ├── Service / Département
      └── Heure de SORTIE de la société

═══════════════════════════════════════════════════════════════════════════════
PHASE 2 : ARRIVÉE CHEZ LE CLIENT
═══════════════════════════════════════════════════════════════════════════════

  5.  Le technicien se déplace chez le client
  
  6.  Le technicien note l'heure d'ARRIVÉE sur site
  
  7.  Le technicien renseigne les informations CLIENT :
      ├── Nom du client
      ├── Adresse complète
      ├── Téléphone / Email
      └── Contact sur site

═══════════════════════════════════════════════════════════════════════════════
PHASE 3 : INTERVENTION
═══════════════════════════════════════════════════════════════════════════════

  8.  Le technicien relève la DESCRIPTION DU PROBLÈME :
      ├── Description du problème remonter par le client
      ├── Chez le client directement
      └── Ou via le contact/responsable

  9.  Le technicien note l'heure de DÉBUT d'intervention
      └── Puis INTERVIENT sur le système/matériel

 10. Le technicien donne son DIAGNOSTIC :
      ├── Analyse du problème identifié
      └── Cause racine

 11. Le technicien renseigne le TYPE D'INTERVENTION réalisé :
      ├── ☐ Maintenance
      ├── ☐ Dépannage
      ├── ☐ Installation
      ├── ☐ Mise à jour
      ├── ☐ Audit / Contrôle
      └── ☐ Autre (champ libre)

 12. Le technicien renseigne le MATÉRIEL / SYSTÈME concerné :
      ├── Marque
      ├── Modèle
      ├── Numéro de série / Version
      └── Localisation

 13. Le technicien décrit les TRAVAUX réalisés :
      ├── Actions effectuées
      └── Solutions mises en place

═══════════════════════════════════════════════════════════════════════════════
PHASE 4 : CONSOMMABLES & RÉSULTAT
═══════════════════════════════════════════════════════════════════════════════

  14. Le technicien note l'heure de FIN d'intervention
      └── Calcul manuel de la DURÉE totale

  15. Le technicien liste les CONSOMMABLES utilisés :
      ├── Pièces de rechange
      └── Fournitures

  16. Le technicien renseigne le RÉSULTAT de l'intervention :
      ├── ☐ Résolu
      ├── ☐ Partiellement résolu
      └── ☐ Non résolu

  17. Le technicien note les RECOMMANDATIONS / SUIVI :
      └── Actions futures préconisées

═══════════════════════════════════════════════════════════════════════════════
PHASE 5 : VALIDATION SUR SITE & FACTURATION
═══════════════════════════════════════════════════════════════════════════════

  18. Le technicien demande au CLIENT de SIGNER la fiche
   
  19. Le technicien renseigne les informations de FACTURATION :
      ├── ☐ Facturable
      └── ☐ Non facturable

═══════════════════════════════════════════════════════════════════════════════
PHASE 6 : RETOUR AU SIÈGE
═══════════════════════════════════════════════════════════════════════════════

  20. Le technicien note l'heure de RETOUR à la société
   
  21. Le responsable hiérarchique vérifie et signe la fiche :
      ├── Lecture complète de la fiche
      ├── Pose de questions si nécessaire
      └── Validation

  22. Le responsable recueille le FEEDBACK du client 
  
  23. La fiche est ARCHIVÉE (classement physique)
   
═══════════════════════════════════════════════════════════════════════════════
PHASE 7 : TRAITEMENT ULTÉRIEUR
═══════════════════════════════════════════════════════════════════════════════

  24. Les données sont potentiellement saisies dans OpenProject (si ticket lié)
  
  25. La facturation est traitée par le département concerné
  
  26. Un rapport est potentiellement généré pour le client (si demandé)

═══════════════════════════════════════════════════════════════════════════════
PROBLÈMES IDENTIFIÉS DANS CE PROCESSUS :
═══════════════════════════════════════════════════════════════════════════════

  ❌ Étape 3  : Perte de temps pour récupérer la fiche au RH
  ❌ Étape 4  : Saisie manuelle sujette aux erreurs
  ❌ Étape 6  : Pas de preuve de présence (GPS)
  ❌ Étape 8  : Pas de photos du problème initial
  ❌ Étape 14 : Calcul manuel de la durée (erreurs possibles)
  ❌ Étape 13-17 : Pas de traçabilité photos avant/après
  ❌ Étape 18 : Signature papier → risque de perte, détérioration
  ❌ Étape 23 : Archivage physique → recherche difficile, perte possible
  ❌ Étape 24 : Double saisie possible (fiche + OpenProject)
  ❌ AUCUN : Pas de notification automatique au manager
  ❌ AUCUN : Pas de suivi en temps réel
  ❌ AUCUN : Pas de statistiques automatisées
```

### Q4 : Quels sont les 3 principaux problèmes avec le processus actuel ?
> _Listez les problèmes les plus critiques_

**Réponse** :

1. **Perte de temps administrative**
   - Déplacement physique au RH pour récupérer la fiche
   - Saisie manuelle des informations (erreurs, oublis)
   - Calcul manuel des durées

2. **Risque de perte d'information**
   - Archivage physique sujet à la perte ou détérioration
   - Pas de sauvegarde numérique
   - Recherche fastidieuse dans les archives
   - **INTERVENTIONS SOUS ABONNEMENT NON ENREGISTRÉES**

   3. **Absence de traçabilité et réactivité**
      - Pas de suivi en temps réel pour le manager
      - Pas de notifications automatiques
      - Pas de photos comme preuves
      - Pas de géolocalisation pour prouver la présence
      - Interventions parfois non enregistrées
### Q5 : Y a-t-il des contraintes réglementaires ?
> _Normes, conformité, RGPD, etc._

**Réponse** :
- [ ] Aucune
- [x] RGPD obligatoire ✅ 2026-04-15
- [ ] Normes sectorielles : ___________
- [ ] Autre : ___________

---

---

## 2. Objectifs du Projet

### Q6 : Quel est l'objectif principal ?
> _En une phrase, pourquoi faites-vous ce projet ?_

**Réponse** :
> Digitaliser et centraliser la gestion des interventions terrain de NG-STARs pour éliminer le papier, garantir la traçabilité de toutes les interventions, améliorer la réactivité du management et fournir des données exploitables pour le pilotage et l'amélioration continue des services.

### Q7 : Quels sont les objectifs mesurables ?
> _Définissez des KPIs_

| Objectif | Indicateur | Valeur actuelle | Cible (6 mois) | Cible (12 mois) |
| -------- | ---------- | --------------- | --------------- | --------------- |
| Temps de saisie | Minutes/intervention | ~15-20 min | 5 min | 3 min |
| Taux de perte fiches | % | ~15% | 0% | 0% |
| Satisfaction client | Score/10 | _À mesurer_ | 8/10 | 9/10 |
| Interventions tracées | % | ~50% | 100% | 100% |
| Temps de réponse manager | Heures | 24-48h | 2h | Temps réel |
| Productivité technicien | Interventions/jour | _À mesurer_ | +20% | +40% |

### Q8 : Quelle est la date de lancement souhaitée ?
> _Quand voulez-vous avoir l'application en production ?_

**Réponse** : **30/06/2026** (3 mois après le 31/03/2026)

### Q9 : Qui sont les décideurs pour ce projet ?
> _Qui doit valider les choix importants ?_

| Rôle             | Nom   | Responsabilité |
| ---------------- | ----- | -------------- |
| Validateur final | David | Responsable IT |
| Directeur        |       | Boss           |

---

## 📝 Notes Additionnelles

> _Observations ou informations complémentaires_
