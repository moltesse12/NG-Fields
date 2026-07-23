-- V1__init.sql - Intervention Service schema
-- Flyway baseline migration

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE interventions (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference               VARCHAR(255) NOT NULL,
    client_id               UUID NOT NULL,
    client_name             VARCHAR(255),
    client_email            VARCHAR(255),
    client_phone            VARCHAR(255),
    client_address          VARCHAR(255),
    equipment_type          VARCHAR(255),
    equipment_brand         VARCHAR(255),
    equipment_model         VARCHAR(255),
    equipment_serial        VARCHAR(255),
    equipment_location      VARCHAR(255),
    reported_issue          VARCHAR(255),
    diagnosis               VARCHAR(255),
    work_done               VARCHAR(255),
    status                  VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    intervention_date       TIMESTAMP WITH TIME ZONE,
    created_by              UUID,
    assigned_to             UUID,
    site_address            VARCHAR(255),
    site_city               VARCHAR(255),
    estimated_cost          NUMERIC(19, 2),
    gps_latitude            DOUBLE PRECISION,
    gps_longitude           DOUBLE PRECISION,
    total_cost              NUMERIC(19, 2),
    client_signature        VARCHAR(255),
    technician_signature    VARCHAR(255),
    manager_signature       VARCHAR(255),
    signed_at               TIMESTAMP WITH TIME ZONE,
    departure_time          TIMESTAMP WITH TIME ZONE,
    arrival_time            TIMESTAMP WITH TIME ZONE,
    start_time              TIMESTAMP WITH TIME ZONE,
    end_time                TIMESTAMP WITH TIME ZONE,
    duration_minutes        INTEGER,
    result                  VARCHAR(20),
    follow_up_recommended   BOOLEAN DEFAULT FALSE,
    recommendations         TEXT,
    local_id                VARCHAR(255),
    notes                   VARCHAR(255),
    active                  BOOLEAN NOT NULL DEFAULT TRUE,
    version                 BIGINT DEFAULT 0,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by              UUID,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_interventions_reference UNIQUE (reference),
    CONSTRAINT uk_interventions_local_id UNIQUE (local_id)
);

CREATE INDEX idx_interventions_client_id ON interventions(client_id);
CREATE INDEX idx_interventions_status ON interventions(status);
CREATE INDEX idx_interventions_assigned_to ON interventions(assigned_to);
CREATE INDEX idx_interventions_created_at ON interventions(created_at);

CREATE TABLE intervention_items (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    intervention_id UUID NOT NULL,
    type            VARCHAR(255) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    quantity        INTEGER NOT NULL DEFAULT 1,
    unit_price      NUMERIC(19, 2) NOT NULL DEFAULT 0,
    total           NUMERIC(19, 2) NOT NULL DEFAULT 0,
    version         BIGINT DEFAULT 0,
    created_by      UUID,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_items_intervention FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE
);

CREATE INDEX idx_intervention_items_intervention_id ON intervention_items(intervention_id);

CREATE TABLE intervention_photos (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    intervention_id     UUID NOT NULL,
    url                 TEXT NOT NULL,
    type                VARCHAR(10) NOT NULL,
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    taken_at            TIMESTAMP WITH TIME ZONE,
    original_filename   VARCHAR(200),
    version             BIGINT DEFAULT 0,
    created_by          UUID,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by          UUID,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_photos_intervention FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE
);

CREATE INDEX idx_intervention_photos_intervention_id ON intervention_photos(intervention_id);
