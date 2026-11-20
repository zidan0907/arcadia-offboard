CREATE SCHEMA IF NOT EXISTS maintenance;

CREATE TABLE IF NOT EXISTS maintenance.service_schedules (
    schedule_id            UUID PRIMARY KEY,
    user_id                VARCHAR(64)  NOT NULL UNIQUE,
    total_distance_covered NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    service_schedule_date  DATE NOT NULL,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_service_schedules_user_id
    ON maintenance.service_schedules (user_id);

CREATE INDEX IF NOT EXISTS idx_service_schedules_date
    ON maintenance.service_schedules (service_schedule_date);
