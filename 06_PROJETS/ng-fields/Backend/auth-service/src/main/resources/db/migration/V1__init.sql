-- V1__init.sql - Auth Service schema
-- Flyway baseline migration

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id     UUID NOT NULL,
    username        VARCHAR(50) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL,
    phone           VARCHAR(255),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    company_id      UUID,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      UUID,
    version         BIGINT DEFAULT 0,
    CONSTRAINT uk_users_keycloak_id UNIQUE (keycloak_id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_company_id ON users(company_id);

CREATE TABLE companies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(200) NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(30),
    address         VARCHAR(255),
    contact_name    VARCHAR(150),
    contact_phone   VARCHAR(30),
    keycloak_organization_id UUID,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version         BIGINT DEFAULT 0
);

CREATE TABLE company_users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id      UUID NOT NULL,
    keycloak_user_id UUID,
    email           VARCHAR(150),
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    role            VARCHAR(50) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version         BIGINT DEFAULT 0,
    CONSTRAINT fk_company_users_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uk_company_users_keycloak UNIQUE (keycloak_user_id)
);

CREATE INDEX idx_company_users_company_id ON company_users(company_id);

CREATE TABLE company_access_log (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id      UUID NOT NULL,
    user_id         UUID,
    action          VARCHAR(100) NOT NULL,
    resource        VARCHAR(100),
    resource_id     UUID,
    ip_address      INET,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_access_log_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_access_log_user FOREIGN KEY (user_id) REFERENCES company_users(id)
);

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID,
    action          VARCHAR(255) NOT NULL,
    resource        VARCHAR(255),
    resource_id     VARCHAR(255),
    details         TEXT,
    ip_address      VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);

CREATE TABLE failed_login_attempts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(255) NOT NULL,
    ip_address      VARCHAR(255) NOT NULL,
    successful      BOOLEAN NOT NULL,
    attempted_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    locked_until    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_failed_login_username ON failed_login_attempts(username);
