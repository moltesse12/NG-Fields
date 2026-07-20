-- Ajout de la colonne version pour le verrouillage optimiste sur la table clients
ALTER TABLE clients ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
