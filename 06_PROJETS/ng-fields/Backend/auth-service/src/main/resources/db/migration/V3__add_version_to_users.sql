-- Ajout de la colonne version pour le verrouillage optimiste sur la table users
ALTER TABLE users ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
