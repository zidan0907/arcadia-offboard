CREATE SCHEMA IF NOT EXISTS maintenance;

CREATE TABLE IF NOT EXISTS maintenance.vehicle_warranties (
    warranty_id   UUID PRIMARY KEY,
    user_id       VARCHAR(64)  NOT NULL UNIQUE,
    warranty_date DATE NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vehicle_warranties_user_id
    ON maintenance.vehicle_warranties (user_id);

CREATE INDEX IF NOT EXISTS idx_vehicle_warranties_warranty_date
    ON maintenance.vehicle_warranties (warranty_date);
