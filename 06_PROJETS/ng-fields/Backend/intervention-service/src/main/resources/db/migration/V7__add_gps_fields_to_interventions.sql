ALTER TABLE interventions
ADD COLUMN IF NOT EXISTS gps_latitude DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS gps_longitude DOUBLE PRECISION;

CREATE INDEX IF NOT EXISTS idx_interventions_gps
ON interventions (gps_latitude, gps_longitude);

COMMENT ON COLUMN interventions.gps_latitude IS 'Latitude du lieu d''intervention (entre -90 et 90)';
COMMENT ON COLUMN interventions.gps_longitude IS 'Longitude du lieu d''intervention (entre -180 et 180)';
