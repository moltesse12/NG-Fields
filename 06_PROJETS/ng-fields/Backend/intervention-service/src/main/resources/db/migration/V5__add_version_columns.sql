-- Ajout des colonnes version pour le verrouillage optimiste sur les tables d'intervention

ALTER TABLE interventions ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE intervention_items ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE intervention_photos ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;
