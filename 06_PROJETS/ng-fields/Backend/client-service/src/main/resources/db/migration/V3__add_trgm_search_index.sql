CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_clients_search_trgm ON clients
    USING gin (
        (lower(company_name) || ' ' || lower(coalesce(contact_name, '')) || ' ' || lower(email))
        gin_trgm_ops
    );
