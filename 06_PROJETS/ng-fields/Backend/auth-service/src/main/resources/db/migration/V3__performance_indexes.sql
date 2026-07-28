-- V2: Performance indexes - Auth Service
-- Ajout d'index sur company_access_log pour les requêtes par company_id et user_id

CREATE INDEX IF NOT EXISTS idx_access_log_company_id ON company_access_log(company_id);
CREATE INDEX IF NOT EXISTS idx_access_log_user_id ON company_access_log(user_id);
