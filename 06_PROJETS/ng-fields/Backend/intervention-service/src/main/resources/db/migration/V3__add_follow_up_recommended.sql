-- V3: Add missing follow_up_recommended column to interventions table

ALTER TABLE interventions ADD COLUMN IF NOT EXISTS follow_up_recommended BOOLEAN DEFAULT FALSE;
