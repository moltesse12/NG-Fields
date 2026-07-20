-- ============================================================================
-- V6: Améliorations d'intégrité, performance et audit pour intervention-service
-- ============================================================================

-- ── 1. CONTRAINTES CHECK ──────────────────────────────────────────────────

ALTER TABLE interventions
    ADD CONSTRAINT chk_interventions_status
    CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'));

ALTER TABLE interventions
    ADD CONSTRAINT chk_interventions_result
    CHECK (result IS NULL OR result IN ('RESOLVED', 'PARTIAL', 'UNRESOLVED'));

ALTER TABLE intervention_photos
    ADD CONSTRAINT chk_photo_type
    CHECK (type IN ('BEFORE', 'AFTER', 'OTHER'));

ALTER TABLE intervention_items
    ADD CONSTRAINT chk_item_quantity_positive
    CHECK (quantity > 0);

-- ── 2. CONTRAINTES FK AVEC CASCADE ────────────────────────────────────────

ALTER TABLE intervention_items
    DROP CONSTRAINT IF EXISTS intervention_items_intervention_id_fkey;

ALTER TABLE intervention_items
    ADD CONSTRAINT intervention_items_intervention_id_fkey
    FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE;

ALTER TABLE intervention_photos
    DROP CONSTRAINT IF EXISTS intervention_photos_intervention_id_fkey;

ALTER TABLE intervention_photos
    ADD CONSTRAINT intervention_photos_intervention_id_fkey
    FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE;

-- ── 3. FIX intervention_photos.created_at ─────────────────────────────────

ALTER TABLE intervention_photos
    ALTER COLUMN created_at SET DEFAULT NOW();

UPDATE intervention_photos SET created_at = NOW() WHERE created_at IS NULL;

ALTER TABLE intervention_photos
    ALTER COLUMN created_at SET NOT NULL;

-- ── 4. STANDARDISER version → BIGINT ──────────────────────────────────────

ALTER TABLE interventions ALTER COLUMN version TYPE BIGINT;
ALTER TABLE interventions ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE intervention_items ALTER COLUMN version TYPE BIGINT;
ALTER TABLE intervention_items ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE intervention_photos ALTER COLUMN version TYPE BIGINT;
ALTER TABLE intervention_photos ALTER COLUMN version SET DEFAULT 0;

-- ── 5. RÉDUIRE status VARCHAR(255) → VARCHAR(20) ─────────────────────────

ALTER TABLE interventions ALTER COLUMN status TYPE VARCHAR(20);

-- ── 6. INDEX MANQUANTS (performance) ─────────────────────────────────────

-- FK index critique sur intervention_items (PostgreSQL ne crée pas d'index sur les FK)
CREATE INDEX IF NOT EXISTS idx_items_intervention_id ON intervention_items(intervention_id);

-- Composite indexes pour les requêtes paginées du repository
CREATE INDEX IF NOT EXISTS idx_interv_active_created
    ON interventions(active, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_interv_active_status_created
    ON interventions(active, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_interv_active_assigned_created
    ON interventions(active, assigned_to, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_interv_active_assigned_status_created
    ON interventions(active, assigned_to, status, created_at DESC);

-- ── 7. INDEX REDONDANTS → DROP ────────────────────────────────────────────

-- idx_interventions_local_id redondant avec l'UNIQUE constraint sur local_id
DROP INDEX IF EXISTS idx_interventions_local_id;

-- ── 8. AUDIT TRAIL : ajouter updated_by ───────────────────────────────────

ALTER TABLE interventions ADD COLUMN IF NOT EXISTS updated_by UUID;
ALTER TABLE intervention_items ADD COLUMN IF NOT EXISTS created_by UUID;
ALTER TABLE intervention_items ADD COLUMN IF NOT EXISTS updated_by UUID;
ALTER TABLE intervention_items ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE intervention_photos ADD COLUMN IF NOT EXISTS created_by UUID;
ALTER TABLE intervention_photos ADD COLUMN IF NOT EXISTS updated_by UUID;
ALTER TABLE intervention_photos ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE;
