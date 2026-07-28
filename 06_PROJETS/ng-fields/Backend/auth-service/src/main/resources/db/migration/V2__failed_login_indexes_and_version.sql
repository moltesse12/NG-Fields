-- V2: Create failed_login_attempts table and add version column + indexes
-- Handles both cases: table already exists (fresh V1) or not (migration from older V1)

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'failed_login_attempts') THEN
        CREATE TABLE failed_login_attempts (
            id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            username        VARCHAR(255) NOT NULL,
            ip_address      VARCHAR(255) NOT NULL,
            successful      BOOLEAN NOT NULL,
            attempted_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
            locked_until    TIMESTAMP WITH TIME ZONE
        );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'failed_login_attempts' AND column_name = 'version') THEN
        ALTER TABLE failed_login_attempts ADD COLUMN version BIGINT DEFAULT 0;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_failed_login_username ON failed_login_attempts(username);
CREATE INDEX IF NOT EXISTS idx_failed_login_ip ON failed_login_attempts(ip_address);
CREATE INDEX IF NOT EXISTS idx_failed_login_username_attempt ON failed_login_attempts(username, successful, attempted_at);
