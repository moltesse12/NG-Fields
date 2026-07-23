-- V1__init.sql - Client Service schema
-- Flyway baseline migration

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE clients (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference       VARCHAR(20) NOT NULL,
    company_name    VARCHAR(200) NOT NULL,
    contact_name    VARCHAR(150),
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(30),
    address         TEXT,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    version         BIGINT DEFAULT 0,
    created_by      VARCHAR(100),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_clients_reference UNIQUE (reference),
    CONSTRAINT uk_clients_email UNIQUE (email)
);

CREATE INDEX idx_clients_company_name ON clients(company_name);

CREATE TABLE contacts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id       UUID NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(30),
    role            VARCHAR(50),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_by      UUID,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_contacts_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE INDEX idx_contacts_client_id ON contacts(client_id);
