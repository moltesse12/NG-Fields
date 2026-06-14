---
tags:
  - audit
  - ng-fields
  - questions
  - utilisateurs
created: 2026-04-15
modified: 
status: complété
---

# Questions - Utilisateurs & Périmètre

## 🎯 Objectif
> _Définir les utilisateurs et le périmètre fonctionnel_

---

## 1. Profils Utilisateurs

### Q10 : Quels seront les types d'utilisateurs ?

| Profil             | Description              | Nombre |
| ------------------ | ------------------------ | ------ |
| Technicien terrain | Saisie interventions     | _15    |
| Manager            | Supervision, reporting   | _10    |
| Admin              | Configuration, gestion   | _10    |
| Client             | Consultation (optionnel) | _999   |

### Q11 : Quel est le niveau technologique des techniciens ?
> _Sont-ils à l'aise avec les smartphones/apps ?_

**Réponse** :
- [x] Très à l'aise (utilisent apps quotidiennement) ✅ 2026-04-15
- [ ] Moyennement à l'aise
- [ ] Peu à l'aise (formation nécessaire)
- [ ] Résistants au changement

### Q12 : Quels appareils utilisent les techniciens ?
> _Smartphones, tablettes, ou les deux ?_

**Réponse** :
- [ ] Smartphones Android uniquement
- [ ] Smartphones iOS uniquement
- [x] Smartphones Android + iOS (les deux) ✅ 2026-04-15
- [x] Tablettes ✅ 2026-04-15
- [ ] Smartphones + Tablettes

---

## 2. Périmètre Fonctionnel

### Q13 : Quelles fonctionnalités sont obligatoires pour le MVP ?

| Fonctionnalité          | Priorité       | Commentaire |
| ----------------------- | -------------- | ----------- |
| Formulaire intervention | 🔴 Obligatoire |             |
| Signature numérique     | 🔴 Obligatoire |             |
| Photos                  | 🟠 Important   |             |
| GPS/Localisation        | 🟠 Important   |             |
| Mode hors-ligne         | 🟡 Souhaitable |             |
| Envoi email client      | 🔴 Obligatoire |             |
| Envoi WhatsApp          | 🟡 Souhaitable |             |
| Génération PDF          | 🔴 Obligatoire |             |
| Dashboard manager       | 🟡 Souhaitable |             |
| Gestion clients         | 🟠 Important   |             |

### Q14 : Quelles fonctionnalités sont exclues du périmètre (v1) ?

**Réponse** : AUCUNE - Toutes les fonctionnalités listées sont incluses dans le périmètre v1
> L'application inclura le formulaire complet, signature, photos, GPS, mode offline, email, WhatsApp, PDF et dashboard manager.

### Q15 : Faut-il intégrer avec d'autres systèmes existants ?

| Système      | Intégration nécessaire ? | Détails                                                            |
| ------------ | ------------------------ | ------------------------------------------------------------------ |
| OpenProject  | [x]Oui [ ]Non            | mais pas pour l'instant sa sera un autre projet apres le lancement |
| ERP          | [ ] Oui [x] Non          |                                                                    |
| CRM          | [ ] Oui [x] Non          |                                                                    |
| Comptabilité | [ ] Oui [x] Non          |                                                                    |
| Autre        | [ ] Oui [x] Non          |                                                                    |

---

## 3. Contraintes Utilisateurs

### Q16 : L'application doit-elle fonctionner hors-ligne ?
> _Y a-t-il des interventions dans des zones sans réseau ?_

**Réponse** :
- [x] Oui, souvent (zones rurales, sous-sols) ✅ 2026-04-15
- [ ] Oui, parfois
- [ ] Non, connexion toujours disponible

### Q17 : Quelle est la qualité de connexion habituelle ?

**Réponse** :
> Estimation professionnelle basée sur le contexte (Togo, zones rurales) :

| Lieu | Qualité | Fréquence |
|------|---------|-----------|
| Bureau/Siège | ☑ Bonne | Toujours |
| Client principal | ☑ Bonne ☐ Moyenne | ~70% du temps |
| Client secondaire | ☐ Moyenne ☑ Faible | ~30% du temps |
| Zones rurales | ☐ Faible | ~20% du temps |

**Conclusion** : Le mode offline est **essentiel** car ~50% des interventions peuvent se faire en zone de faible connexion.

### Q18 : Y a-t-il des contraintes d'autonomie batterie ?
> _Les techniciens ont-ils accès à des chargeurs ?_

**Réponse** :
- [ ] Oui, toujours
- [x] Oui, parfois ✅ 2026-04-15
- [ ] Rarement (autonomie critique)

---

## 📝 Notes Additionnelles

> _Observations ou informations complémentaires_
