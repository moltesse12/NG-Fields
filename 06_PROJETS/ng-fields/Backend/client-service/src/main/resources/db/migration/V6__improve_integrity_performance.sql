-- ============================================================================
-- V6: Améliorations d'intégrité, performance et audit pour client-service
-- ============================================================================

-- ── 1. CONTRAINTES CHECK ──────────────────────────────────────────────────

ALTER TABLE clients
    ADD CONSTRAINT chk_clients_email_format
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE contacts
    ADD CONSTRAINT chk_contacts_email_format
    CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- ── 2. INDEX MANQUANTS (performance) ─────────────────────────────────────

-- Composite index pour findByClientIdAndActiveTrue
CREATE INDEX IF NOT EXISTS idx_contacts_client_active
    ON contacts(client_id, active);

-- ── 3. INDEX REDONDANTS → DROP ────────────────────────────────────────────

-- idx_clients_email redondant avec l'UNIQUE constraint sur email
DROP INDEX IF EXISTS idx_clients_email;

-- ── 4. SÉQUENCE client_ref_seq améliorée ─────────────────────────────────

DROP SEQUENCE IF EXISTS client_ref_seq;
CREATE SEQUENCE client_ref_seq
    START 1
    INCREMENT 1
    NO MAXVALUE
    CACHE 20
    OWNED BY NONE;

COMMENT ON SEQUENCE client_ref_seq IS 'Génère les numéros séquentiels pour les références clients (CLT-XXXX)';

-- ── 5. AUDIT TRAIL : ajouter updated_by ───────────────────────────────────

ALTER TABLE clients ADD COLUMN IF NOT EXISTS updated_by UUID;
ALTER TABLE contacts ADD COLUMN IF NOT EXISTS created_by UUID;
ALTER TABLE contacts ADD COLUMN IF NOT EXISTS updated_by UUID;
ALTER TABLE contacts ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE;
