-- NG-Fields PostgreSQL Schema
-- À exécuter dans Supabase SQL Editor ou via supabase db push
-- Généré depuis les entités JPA Spring Boot
-- Mis à jour : 21/07/2026 (Post-Cadrage)

-- Enums
CREATE TYPE role AS ENUM ('ADMIN', 'MANAGER', 'TECHNICIAN', 'CLIENT_ADMIN', 'CLIENT_USER', 'CLIENT_VIEWER');
CREATE TYPE intervention_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE photo_type AS ENUM ('BEFORE', 'AFTER', 'OTHER');

-- Users (technicians, managers, admins)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role role NOT NULL DEFAULT 'TECHNICIAN',
    department VARCHAR(255),
    phone VARCHAR(255),
    company_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Companies (enterprises clients — NOUVEAU Post-Cadrage 21/07/2026)
CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(30),
    address TEXT,
    contact_name VARCHAR(150),
    contact_phone VARCHAR(30),
    keycloak_organization_id UUID,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Company Users (NOUVEAU Post-Cadrage 21/07/2026)
CREATE TABLE IF NOT EXISTS company_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    keycloak_user_id UUID UNIQUE,
    email VARCHAR(150),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) NOT NULL, -- CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Company Access Log (NOUVEAU Post-Cadrage 21/07/2026)
CREATE TABLE IF NOT EXISTS company_access_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    user_id UUID REFERENCES company_users(id),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100),
    resource_id UUID,
    ip_address INET,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Clients (enterprises / sites)
CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    address TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    contact_name VARCHAR(255),
    contact_phone VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Interventions (field interventions)
CREATE TABLE IF NOT EXISTS interventions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id VARCHAR(255) NOT NULL UNIQUE,
    client_id UUID NOT NULL REFERENCES clients(id),
    technician_id UUID NOT NULL REFERENCES users(id),
    date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status intervention_status NOT NULL DEFAULT 'PENDING',
    type VARCHAR(255) NOT NULL,
    departure_time TIMESTAMPTZ,
    arrival_time TIMESTAMPTZ,
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ,
    return_time TIMESTAMPTZ,
    duration INTEGER,
    problem_desc TEXT,
    diagnosis TEXT,
    work_done TEXT,
    result TEXT,
    recommendations TEXT,
    signature_url TEXT,
    equipment_brand VARCHAR(255),
    equipment_model VARCHAR(255),
    equipment_serial VARCHAR(255),
    equipment_location VARCHAR(255),
    -- billable BOOLEAN DEFAULT TRUE,          -- ❌ SUPPRIMÉ (Post-Cadrage 21/07/2026)
    -- billing_amount NUMERIC(38,2),           -- ❌ SUPPRIMÉ (Post-Cadrage 21/07/2026)
    -- billing_notes TEXT,                     -- ❌ SUPPRIMÉ (Post-Cadrage 21/07/2026)
    -- openproject_ticket_id VARCHAR(50),      -- ❌ SUPPRIMÉ (Post-Cadrage 21/07/2026)
    -- openproject_ticket_url VARCHAR(500),    -- ❌ SUPPRIMÉ (Post-Cadrage 21/07/2026)
    observations TEXT,
    synced_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Intervention photos
CREATE TABLE IF NOT EXISTS intervention_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id UUID NOT NULL REFERENCES interventions(id) ON DELETE CASCADE,
    url VARCHAR(255) NOT NULL,
    type photo_type NOT NULL DEFAULT 'BEFORE',
    local_path VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Intervention items (replaced parts, consumables)
CREATE TABLE IF NOT EXISTS intervention_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    intervention_id UUID NOT NULL REFERENCES interventions(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Equipment (client equipment registry)
CREATE TABLE IF NOT EXISTS equipment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    brand VARCHAR(255),
    model VARCHAR(255),
    serial_number VARCHAR(255),
    location VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_interventions_client_id ON interventions(client_id);
CREATE INDEX IF NOT EXISTS idx_interventions_technician_id ON interventions(technician_id);
CREATE INDEX IF NOT EXISTS idx_interventions_date ON interventions(date);
CREATE INDEX IF NOT EXISTS idx_interventions_status ON interventions(status);
CREATE INDEX IF NOT EXISTS idx_interventions_local_id ON interventions(local_id);
CREATE INDEX IF NOT EXISTS idx_intervention_photos_intervention_id ON intervention_photos(intervention_id);
CREATE INDEX IF NOT EXISTS idx_intervention_items_intervention_id ON intervention_items(intervention_id);
CREATE INDEX IF NOT EXISTS idx_equipment_client_id ON equipment(client_id);
CREATE INDEX IF NOT EXISTS idx_companies_name ON companies(name);
CREATE INDEX IF NOT EXISTS idx_companies_active ON companies(active);
CREATE INDEX IF NOT EXISTS idx_company_users_company_id ON company_users(company_id);
CREATE INDEX IF NOT EXISTS idx_company_users_keycloak_id ON company_users(keycloak_user_id);
CREATE INDEX IF NOT EXISTS idx_company_access_company_id ON company_access_log(company_id);
CREATE INDEX IF NOT EXISTS idx_company_access_created_at ON company_access_log(created_at);
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users(company_id);

-- Updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_clients_updated_at
    BEFORE UPDATE ON clients
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_interventions_updated_at
    BEFORE UPDATE ON interventions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_companies_updated_at
    BEFORE UPDATE ON companies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_company_users_updated_at
    BEFORE UPDATE ON company_users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- Row Level Security (via Supabase)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE clients ENABLE ROW LEVEL SECURITY;
ALTER TABLE interventions ENABLE ROW LEVEL SECURITY;
ALTER TABLE intervention_photos ENABLE ROW LEVEL SECURITY;
ALTER TABLE intervention_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE equipment ENABLE ROW LEVEL SECURITY;
ALTER TABLE companies ENABLE ROW LEVEL SECURITY;
ALTER TABLE company_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE company_access_log ENABLE ROW LEVEL SECURITY;

-- RLS Policies (backend API gère l'auth directement, pas Supabase Auth)
CREATE POLICY "Allow all authenticated" ON users FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON clients FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON interventions FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON intervention_photos FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON intervention_items FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON equipment FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON companies FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON company_users FOR ALL USING (true);
CREATE POLICY "Allow all authenticated" ON company_access_log FOR ALL USING (true);
