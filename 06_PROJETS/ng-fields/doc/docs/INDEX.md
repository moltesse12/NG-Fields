# Documentation Projet NG-Fields

**Mis à jour :** 23/07/2026 (Backend Complet)

## Structure

```
docs/
├── architecture/           Stack, flux, ADR
├── business/               Processus métier
├── database/               Modèle de données
├── mobile/                 Guide Flutter
├── integrations/           ~~OpenProject~~ ❌, ~~Twilio WhatsApp~~ ❌
├── tests/                  Postman
└── references/             Docs techniques
```

## Documents racine

| Document | Contenu |
|----------|---------|
| `Backlog.md` | Backlog produit (V0/V0.1/V1) — **V5.0 Post-Cadrage** |
| `Capture des besoins fonctionnels.md` | Besoins fonctionnels détaillés |
| `Cahier des charges - NG-Fields.md` | Cahier des charges projet |
| `Technologies.md` | Stack technique détaillée |
| `Setup.md` | Guide d'installation |
| `README.md` | Présentation du projet |

## Architecture

| Document | Emplacement |
|----------|-------------|
| Stack technique | `Technologies.md` (racine) |
| Schéma BDD | `infra/supabase/schema.sql` |
| Données de test | `infra/supabase/seed.sql` |
| Config Keycloak | `infra/keycloak/realm-export.json` |
| Docker Compose | `infra/docker-compose.yml` |

## Intégrations

| Intégration | Documentation | Statut |
|-------------|---------------|--------|
| ~~OpenProject (API REST v3)~~ | ~~`docs/integrations/openproject-api.md`~~ | ❌ SUPPRIMÉ (21/07/2026) |
| Keycloak | `docs/references/keycloak-reference.md` | ✅ Actif |
| ~~Twilio WhatsApp~~ | ~~`docs/integrations/twilio-whatsapp.md`~~ | ❌ SUPPRIMÉ (21/07/2026) |

## Mobile (Flutter)

| Document | Emplacement |
|----------|-------------|
| Guide Flutter | `docs/mobile/flutter-reference.md` |
| Wireframes | `wireframes/` |
| Guide UI | `docs/mobile/guide-ui.md` |

## Charte graphique NG-STARs

- Logo HD (PNG/SVG) — *à fournir*
- Couleurs corporate — *à fournir*
- Typographie — *à fournir*

## Documents obsolètes (conservés à titre historique)

| Document | Raison |
|----------|--------|
| `OpenProject.md` | Intégration supprimée du périmètre (21/07/2026) |
| `docs/integrations/openproject-api.md` | Intégration supprimée du périmètre (21/07/2026) |
| `docs/integrations/twilio-whatsapp.md` | WhatsApp supprimé du périmètre (21/07/2026) |

---

_Version 3.0 — 21/07/2026 (Post-Cadrage)_
