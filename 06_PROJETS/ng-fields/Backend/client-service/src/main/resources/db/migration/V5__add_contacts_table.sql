CREATE TABLE IF NOT EXISTS contacts (
    id         UUID           PRIMARY KEY,
    client_id  UUID           NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    full_name  VARCHAR(150)   NOT NULL,
    email      VARCHAR(150),
    phone      VARCHAR(30),
    role       VARCHAR(50),
    active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_contacts_client_id ON contacts(client_id);
CREATE INDEX IF NOT EXISTS idx_contacts_active ON contacts(active);
