-- V1__init.sql - Report Service schema
-- Flyway baseline migration

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE pdf_templates (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    template_type   VARCHAR(255) NOT NULL DEFAULT 'INTERVENTION_REPORT',
    config          JSONB NOT NULL DEFAULT '{}',
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pdf_templates_template_type ON pdf_templates(template_type);

CREATE TABLE email_templates (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    template_key    VARCHAR(255) NOT NULL,
    subject         VARCHAR(255) NOT NULL,
    body_html       TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_email_templates_key UNIQUE (template_key)
);
