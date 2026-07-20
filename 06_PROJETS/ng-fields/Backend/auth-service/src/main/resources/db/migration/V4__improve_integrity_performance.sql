-- ============================================================================
-- V4: Améliorations d'intégrité, performance et audit pour auth-service
-- ============================================================================

-- ── 1. CONTRAINTES CHECK ──────────────────────────────────────────────────

ALTER TABLE users
    ADD CONSTRAINT chk_users_role
    CHECK (role IN ('ADMIN', 'MANAGER', 'TECHNICIAN', 'CLIENT_PORTAL'));

-- ── 2. INDEX MANQUANTS (performance) ─────────────────────────────────────

-- Index pour findByAction dans AuditLogRepository
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);

-- Composite index pour les requêtes paginées par user + date
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_created
    ON audit_logs(user_id, created_at DESC);

-- ── 3. AUDIT TRAIL : ajouter updated_by ───────────────────────────────────

ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by UUID;
