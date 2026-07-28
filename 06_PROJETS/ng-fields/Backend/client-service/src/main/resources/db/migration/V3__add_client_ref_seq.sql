-- V3: Create missing client_ref_seq sequence
-- V1 was baseline-only (tables pre-existed), so this sequence was never created

CREATE SEQUENCE IF NOT EXISTS client.client_ref_seq START WITH 1;
