-- V2: Performance indexes - Intervention Service
-- Index partiels sur interventions.active pour optimiser les requêtes de stats et listes

-- Index partiel pour les interventions actives par statut (stats + filtres)
CREATE INDEX IF NOT EXISTS idx_interv_active_status_created ON interventions(status, created_at DESC) WHERE active = true;

-- Index partiel pour les interventions actives par client (listes + stats client)
CREATE INDEX IF NOT EXISTS idx_interv_active_client_created ON interventions(client_id, created_at DESC) WHERE active = true;

-- Index partiel pour les interventions actives par technicien (planning + stats tech)
CREATE INDEX IF NOT EXISTS idx_interv_active_assigned_created ON interventions(assigned_to, created_at DESC) WHERE active = true;

-- Index partiel pour les conflits de planning (overlap detection)
CREATE INDEX IF NOT EXISTS idx_interv_active_overlap ON interventions(assigned_to, start_time, end_time) WHERE active = true AND start_time IS NOT NULL AND end_time IS NOT NULL;

-- Index sur intervention_items pour le chargement par intervention_id (couvre déjà idx_intervention_items_intervention_id, mais on vérifie)
-- idx_intervention_items_intervention_id existe déjà dans V1
