---
tags:
  - audit
  - ng-fields
  - questions
  - fonctionnalités
created: 2026-04-15
modified: 
status: complété
---

# Questions - Fonctionnalités Détaillées

## 🎯 Objectif
> _Préciser les besoins fonctionnels pour le formulaire et les processus_

---

## 1. Formulaire d'Intervention

### Q19 : Le formulaire doit-il reproduire exactement la fiche papier ?

**Réponse** :
- [x] Oui, reprendre tous les champs ✅ 2026-04-15
- [ ] Non, revoir la structure
- [ ] En partie, modifications à prévoir

### Q20 : Quels champs sont les plus utilisés ?

| Champ             | Fréquence d'utilisation     | Important ? |
| ----------------- | --------------------------- | ----------- |
| Infos client      | [x]Haute []Moyenne []Faible | [x]         |
| Horaires          | [x]Haute []Moyenne []Faible | [x]         |
| Type intervention | [x]Haute []Moyenne []Faible | [x]         |
| Équipement        | [x]Haute []Moyenne []Faible | [x]         |
| Diagnostic        | [x]Haute []Moyenne []Faible | [x]         |
| Travaux           | [x]Haute []Moyenne []Faible | [x]         |
| Consommables      | [x]Haute []Moyenne []Faible | [x]         |
| Photos            | [x]Haute []Moyenne []Faible | [x]         |
| Signature         | [x]Haute []Moyenne []Faible | [x]         |

### Q21 : Y a-t-il des champs spécifiques à ajouter ?

**Réponse** : Champs的建议és (à valider) :

| Champ | Justification | Priorité |
|-------|---------------|----------|
| **Type facturation** (Facturable/Non) | Distinguer interventions facturables | 🔴 Haute |
| **Numéro de ticket OpenProject** | Lien avec existant | 🟠 Moyenne |
| **Commentaire libre technician** | Observations terrain | 🟡 Basse |
| **Urgence de l'intervention** | Priorisation | 🟡 Basse |

---

## 2. Gestion des Clients

### Q22 : La liste des clients existe-t-elle déjà ?

**Réponse** :
- [ ] Oui, dans : ___________
- [ ] Non, à créer de zéro
- [x] Partiellement ✅ 2026-04-15

### Q23 : Quelles informations clients sont nécessaires ?

| Information      | Nécessaire ?  | Source |
| ---------------- | ------------- | ------ |
| Nom              | [x]Oui [ ]Non |        |
| Adresse          | [x]Oui [ ]Non |        |
| Téléphone        | [x]Oui [ ]Non |        |
| Email            | [x]Oui [ ]Non |        |
| GPS/Coordonnées  | [x]Oui [ ]Non |        |
| Contact sur site | [x]Oui [ ]Non |        |

### Q24 : Faut-il un historique des interventions par client ?

**Réponse** :
- [x] Oui, essentiel ✅ 2026-04-15
- [ ] Oui, mais secondaire
- [ ] Non nécessaire

---

## 3. Photos & Documents

### Q25 : Combien de photos par intervention en moyenne ?

**Réponse** :
- [ ] 0-2 photos
- [x] 3-5 photos ✅ 2026-04-15
- [ ] 6-10 photos
- [ ] Plus de 10

### Q26 : Faut-il annoter/dessiner sur les photos ?

**Réponse** :
- [ ] Oui, indispensable
- [ ] Oui, mais secondaire
- [x] Non ✅ 2026-04-15

### Q27 : Les photos doivent-elles avoir des légendes ?

**Réponse** :
- [ ] Oui, obligatoire
- [x] Oui, optionnel ✅ 2026-04-15
- [ ] Non

---

## 4. Signature & Validation

### Q28 : La signature est-elle légalement requise ?

**Réponse** :
- [x] Oui, c'est un document officiel ✅ 2026-04-15
- [ ] Oui, mais pas obligatoire
- [ ] Non, juste pour confirmation

### Q29 : Qui doit signer ?

| Signataire            | Obligatoire ? |
| --------------------- | ------------- |
| Client                | [x]Oui [ ]Non |
| Technicien            | [x]Oui [ ]Non |
| Responsable technique | [x]Oui [ ]Non |

### Q30 : Faut-il un accusé de réception电子？

**Réponse** :
- [x] Oui, email obligatoire ✅ 2026-04-15
- [ ] Oui, WhatsApp suffisant
- [ ] Les deux
- [ ] Non

---

## 5. Rapports & Exports

### Q31 : Le PDF doit-il inclure...

| Élément              | Dans le PDF ? |
| -------------------- | ------------- |
| Logo NG-STARs        | [x]Oui [ ]Non |
| Informations client  | [x]Oui [ ]Non |
| Photos               | [x]Oui [ ]Non |
| Signature scannée    | [x]Oui [ ]Non |
| Détails consommables | [x]Oui [ ]Non |
| QR code              | [x]Oui [ ]Non |

### Q32 : Faut-il exporter les données (CSV, Excel) ?

**Réponse** :
- [x] Oui, régulièrement ✅ 2026-04-15
- [ ] Oui, occasionnellement
- [ ] Non

---

## 6. Notifications & Alertes

### Q33 : Faut-il des notifications pour les interventions

| Événement              | Notification ? |
| ---------------------- | -------------- |
| Intervention créée     | [x]Oui [ ]Non  |
| Intervention terminée  | [x]Oui [ ]Non  |
| Intervention en retard | [x]Oui [ ]Non  |
| Nouveau message client | [x]Oui [ ]Non  |

### Q34 : À qui envoyer les notifications ?

- [x] Manager ✅ 2026-04-15
- [x] Responsable ✅ 2026-04-15
- [x] Tous les techniciens (si le projet est toujours en suspends en retard) ✅ 2026-04-15
- [ ] Client

---

## 📝 Notes Additionnelles

> _Observations ou informations complémentaires_
