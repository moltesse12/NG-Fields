-- V2: Performance fix - Client Service
-- Suppression de l'index redondant idx_clients_email (le UNIQUE constraint uk_clients_email crée déjà un index)

DROP INDEX IF EXISTS client.idx_clients_email;
