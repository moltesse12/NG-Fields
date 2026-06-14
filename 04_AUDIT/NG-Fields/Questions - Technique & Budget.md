---
tags:
  - audit
  - ng-fields
  - questions
  - technique
created: 2026-04-15
modified: 
status: complété
---

# Questions - Technique & Budget

## 🎯 Objectif
> _Définir les contraintes techniques et le budget_

---

## 1. Infrastructure Existante

### Q35 : Utilisez-vous déjà...

| Service                    | Utilisation actuelle          |
| -------------------------- | ----------------------------- |
| Cloud (AWS, Azure, GCP)    | [x]Oui [ ]Non Fournisseur : _ |
| Serveur dédié              | [x]Oui [ ]Non                 |
| Hébergement mutualisé      | [x]Oui [ ]Non                 |
| Base de données PostgreSQL | [x]Oui [ ]Non                 |
| Git/GitHub/GitLab          | [x]Oui [ ]Non                 |

### Q36 : Avez-vous une équipe technique ?

| Rôle                        | Disponible ?  | Compétences |
| --------------------------- | ------------- | ----------- |
| Développeur React Native    | [x]Oui [ ]Non | senior      |
| Développeur Node.js/Backend | [x]Oui [ ]Non | senior      |
| DevOps                      | [x]Oui [ ]Non | senior      |
| Designer UI/UX              | [x]Oui [ ]Non | senior      |

### Q37 : Quel est le niveau de compétences techniques de l'équipe ?

**Réponse** :
- [x] Élevé (peuvent maintenir et évoluer l'app) ✅ 2026-04-15
- [ ] Moyen (peuvent faire des modifications simples)
- [ ] Faible (besoin d'accompagnement)
- [ ] Nul (besoin d'externe pour tout)

---

## 2. Contraintes Techniques

### Q38 : Faut-il une compatibilité spécifique ?

| Plateforme     | Required ?                    |
| -------------- | ----------------------------- |
| iOS            | [x]Oui [ ]Non Version min : _ |
| Android        | [x]Oui [ ]Non Version min : _ |
| Navigateur web | [x]Oui [ ]Non                 |
| Tablettes      | [x]Oui [ ]Non                 |

### Q39 : Y a-t-il des exigences de sécurité ?

| Exigence                  | Required ?    |
| ------------------------- | ------------- |
| Chiffrement des données   | [x]Oui [ ]Non |
| Authentification MFA      | [x]Oui [ ]Non |
| Audit trail (logs)        | [x]Oui [ ]Non |
| Sauvegarde quotidienne    | [x]Oui [ ]Non |
| Plan de reprise (PRA/DRP) | [x]Oui [ ]Non |

### Q40 : Quel est le volume de données attendu ?

**Réponse** :

| Métrique | Estimation | Calcul |
|----------|-----------|--------|
| Utilisateurs simultanés | 20 | 15 techniciens + 5 managers |
| Interventions/jour (pic) | 10 | ~50/mois ÷ 22 jours × 1.5 (peak) |
| Stockage photos (Go/mois) | 15 | 10 interventions × 5 photos × 300KB × 30j ≈ 4.5 Go (on estimation large) |
| Requêtes API/jour | ~500-1000 | 10 interventions × ~50 requêtes (sync, upload, etc.) |

**Conclusion** : Volume modeste, infrastructure légère suffisante.

---

## 3. Identité Visuelle

### Q41 : Avez-vous une charte graphique ?

**Réponse** :
- [x] Oui, je peux la fournir ✅ 2026-04-15
- [ ] Non, à créer
- [ ] En partie

### Q42 : Le logo NG-STARs est-il disponible en haute définition ?

**Réponse** :
- [x] Oui ✅ 2026-04-15
- [ ] Non, à récupérer
- [ ] À créer

### Q43 : Quelles sont les couleurs/médicas de l'entreprise ?

**Réponse** :
- Couleur principale : Spaceblue(21,73,99)
- Couleur secondaire : Auroraglow(154,197,123)___________
- Autres : - Light(198,223,233)
		  - Dark(8,15,21) 
- ___________

---

## 4. Budget

### Q44 : Quel est le budget disponible pour ce projet ?

**Réponse** :
- [x] < 100€ ✅ 2026-04-15
- [ ] 5 000 - 15 000 €
- [ ] 15 000 - 30 000 €
- [ ] 30 000 - 50 000 €
- [ ] > 50 000 €
- [ ] Budget flexible selon ROI

### Q45 : Y a-t-il des coûts récurrents acceptables (licences, abonnements) ?

**Réponse** :
- [ ] Oui, jusqu'à _€/mois
- [x] Non, uniquement investissement initial ✅ 2026-04-15
- [ ] Pas de préférence

### Q46 : Avez-vous comparé avec des solutions no-code ?

| Solution    | Connue ?      | Evaluée ?     | Intéressé ?   |
| ----------- | ------------- | ------------- | ------------- |
| Kizeo Forms | [x]Oui [ ]Non | [x]Oui [ ]Non | [ ]Oui [x]Non |
| AppSheet    | [x]Oui [ ]Non | [x]Oui [ ]Non | [ ]Oui [x]Non |
| Power Apps  | [x]Oui [ ]Non | [x]Oui [ ]Non | [ ]Oui [x]Non |

---

## 5. Délai & Jalons

### Q47 : Quelles sont les dates clés ?

**Réponse** :

| Jalon | Date | Statut |
|-------|------|--------|
| Validation cahier des charges | 15/04/26 | ✅ Terminé |
| Choix solution/démarrage | 18/04/26 | ✅ Confirmé |
| Lancement développement | 18/04/26 | ✅ Confirmé |
| Tests/UAT | Méthode Agile (sprints) | En cours |
| Mise en production | 30/06/26 | ⏳ 3 mois |

### Q48 : Y a-t-il des événements à respecter ?

> _Lancement commercial, salon, etc._

**Réponse** :
- Aucun événement externe identifié à ce jour
- Délai interne : Fin du trimestre (Q2 2026)

### Q49 : Faut-il une migration des données existantes ?

**Réponse** :
- [x] Oui, migrations fiches papier → numérique ✅ 2026-04-15
- [ ] Oui, intégration base clients existante
- [ ] Non, démarrage de zéro

### Q50 : Quel support est attendu après lancement ?

| Support                     | Requis ?      |
| --------------------------- | ------------- |
| Formation utilisateurs      | [x]Oui [ ]Non |
| Documentation               | [x]Oui [ ]Non |
| Support technique (hotline) | [x]Oui [ ]Non |
| Maintenance évolutive       | [x]Oui [ ]Non |

---

## 📝 Notes Additionnelles

> _Observations ou informations complémentaires_
