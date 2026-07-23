# Écran 01 - Dashboard Technicien

## Objectif
Page d'accueil de l'application affichant les interventions du jour et l'état de connexion.

---

## Structure de la maquette

```
┌─────────────────────────────────────┐
│ [≡]  NG-Fields         [🔔] [⚙️]   │ Header (56px)
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │  ● Hors-ligne        Synchro ↻ │ │ Status Bar (44px)
│ │     2/2 en attente              │ │ (rouge si hors-ligne)
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│                                     │
│  Bonjour, Koffi                     │
│  Mardi 15 Avril 2026                │ Greeting
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ 📊 STATUT DU JOUR               │ │
│ │ ─────────────────────────────   │ │
│ │ ┌─────┐  ┌─────┐  ┌─────┐      │ │
│ │ │  2  │  │  1  │  │  0  │      │ │ Stats Cards
│ │ │EnCours│ │Terminé│ │ÀFacturer│ │ │
│ │ └─────┘  └─────┘  └─────┘      │ │
│ └─────────────────────────────────┘ │
│                                     │
│  INTERVENTIONS DU JOUR              │
│  ─────────────────────────────────  │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ 🟡 EN COURS                      │ │ Intervention Card 1
│ │ ══════════════════════════════  │ │
│ │ Client: Entreprise ABC          │ │
│ │ Adresse: 123 Rue de la Paix     │ │
│ │ Type: Maintenance               │ │
│ │ [🗺️ Maps] [📞 Appel] [▶️ Début] │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ⚪ EN ATTENTE                    │ │ Intervention Card 2
│ │ ══════════════════════════════  │ │
│ │ Client: Société XYZ             │ │
│ │ Adresse: 45 Avenue de la Liberté│ │
│ │ Type: Installation              │ │
│ │ [🗺️ Maps] [📞 Appel] [▶️ Début] │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ✅ TERMINÉES (1)              ↗  │ │ Section expandable
│ └─────────────────────────────────┘ │
│                                     │
├─────────────────────────────────────┤
│  [🏠]    [📋]    [📊]    [👤]      │ Bottom Nav (64px)
│  Home   Formulaires Rapports  Profil│
└─────────────────────────────────────┘
```

---

## Éléments interactifs

| Élément | Action | Comportement |
|---------|--------|--------------|
| Badge Hors-ligne | Indicateur seul | Affiche état sync en temps réel |
| Bouton Synchro | Tap | Force synchronisation |
| Card En Cours | Tap | Ouvre formulaire en cours |
| Bouton Maps | Tap | Ouvre Google Maps/Waze |
| Bouton Appel | Tap | Lance appel téléphone |
| Bouton Début | Tap | Démarre intervention |
| Bottom Nav | Tap | Navigation entre écrans |

---

## États

### États de connexion
- **En ligne**: Badge vert "En ligne", synchro automatique
- **Hors-ligne**: Badge rouge "Hors-ligne", données locales prioritaires

### États des interventions
- **En attente** (⚪): Non commencée
- **En cours** (🟡): Formulaire actif
- **En pause** (🟠): Temporairement arrêtée
- **Terminée** (✅): Complétée, en attente validation
- **Validée** (🔵): Approuvée par manager

---

## Contraintes UX

- Le bouton "Début" n'apparaît que pour les interventions en attente
- Le bouton "Maps" utilise le GPS pour calculer l'itinéraire
- Les cards en cours sont toujours en haut de la liste
- La liste est triable par: heure, priorité, distance
